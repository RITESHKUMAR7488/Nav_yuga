package com.example.mahayuga.feature.admin.data.repository

import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.admin.data.model.InvestmentModel
import com.example.mahayuga.feature.admin.domain.repository.AdminRepository
import com.example.mahayuga.feature.auth.data.model.UserModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AdminRepository {

    override fun getAllUsers(): Flow<UiState<List<UserModel>>> = callbackFlow {
        trySend(UiState.Loading)
        val listener = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(UiState.Failure(error.message ?: "Unknown Error"))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val users = snapshot.toObjects(UserModel::class.java)
                    trySend(UiState.Success(users))
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun toggleUserStatus(uid: String, isActive: Boolean): UiState<String> {
        return try {
            firestore.collection("users").document(uid).update("isActive", isActive).await()
            UiState.Success("User status updated")
        } catch (e: Exception) {
            UiState.Failure(e.message ?: "Update failed")
        }
    }

    override fun getPendingRequests(): Flow<UiState<List<UserModel>>> = callbackFlow {
        trySend(UiState.Loading)
        val listener = firestore.collection("users")
            .whereEqualTo("isApproved", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(UiState.Failure(error.message ?: "Sync Error"))
                    return@addSnapshotListener
                }
                val users = snapshot?.toObjects(UserModel::class.java) ?: emptyList()
                trySend(UiState.Success(users))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun approveUserRequest(uid: String, role: String): UiState<String> {
        return try {
            firestore.collection("users").document(uid)
                .update(
                    mapOf("isApproved" to true, "role" to role, "isActive" to true)
                ).await()
            UiState.Success("User Approved")
        } catch (e: Exception) {
            UiState.Failure(e.message ?: "Approval Failed")
        }
    }

    override suspend fun rejectUserRequest(uid: String): UiState<String> {
        return try {
            firestore.collection("users").document(uid).delete().await()
            UiState.Success("Request Rejected")
        } catch (e: Exception) {
            UiState.Failure(e.message ?: "Rejection Failed")
        }
    }

    // ⚡ CRITICAL: This transaction ensures ROI starts at 0% and Area/Rent are calculated correctly
    override suspend fun registerInvestment(investment: InvestmentModel): UiState<String> {
        return try {
            firestore.runTransaction { transaction ->
                // 1. References
                val userRef = firestore.collection("users").document(investment.userId)
                val propertyRef = firestore.collection("properties").document(investment.propertyId)
                val investmentRef = firestore.collection("investments").document()

                // 2. Read (Must come before writes)
                val userSnapshot = transaction.get(userRef)
                val propertySnapshot = transaction.get(propertyRef)

                // 3. Helper to parse clean numbers from strings
                fun parseLong(str: String?): Long = str?.replace(Regex("[^\\d]"), "")?.toLongOrNull() ?: 0L
                fun parseDouble(str: String?): Double = str?.replace(Regex("[^\\d.]"), "")?.toDoubleOrNull() ?: 0.0

                val propValuation = parseLong(propertySnapshot.getString("totalValuation"))
                val propArea = parseDouble(propertySnapshot.getString("area"))
                val propRent = parseLong(propertySnapshot.getString("monthlyRent"))

                // 4. Calculate User's Share (Pro-rated)
                // If valuation is 1Cr and user invests 1L, they own 1% of Area and Rent
                val ownershipFraction = if (propValuation > 0) investment.amount.toDouble() / propValuation else 0.0

                val addedArea = propArea * ownershipFraction
                val addedRent = (propRent * ownershipFraction).toLong()

                // 5. Calculate New User Totals
                val currentInvest = userSnapshot.getLong("totalInvestment") ?: 0L
                val currentVal = userSnapshot.getLong("currentValue") ?: 0L
                val currentArea = userSnapshot.getDouble("totalArea") ?: 0.0
                val currentRent = userSnapshot.getLong("totalRent") ?: 0L

                val newInvest = currentInvest + investment.amount

                // ⚡ FIX ROI: Increase currentValue by investment amount too.
                // Before: (0 - 1000) / 1000 = -100%
                // After: (1000 - 1000) / 1000 = 0%
                val newVal = currentVal + investment.amount

                val newArea = currentArea + addedArea
                val newRent = currentRent + addedRent

                // 6. Calculate New Property Funding
                val currentPropFunding = parseLong(propertySnapshot.getString("totalFunding"))
                val newPropFunding = currentPropFunding + investment.amount

                // 7. Write Updates
                // A. Create Investment Record
                transaction.set(investmentRef, investment.copy(id = investmentRef.id))

                // B. Update User Portfolio
                transaction.update(userRef, mapOf(
                    "totalInvestment" to newInvest,
                    "currentValue" to newVal,
                    "totalArea" to newArea,
                    "totalRent" to newRent,
                    "investedProperties" to FieldValue.arrayUnion(investment.propertyId)
                ))

                // C. Update Property Funding Status
                transaction.update(propertyRef, "totalFunding", newPropFunding.toString())

            }.await()

            UiState.Success("Investment Registered Successfully")
        } catch (e: Exception) {
            UiState.Failure(e.message ?: "Transaction Failed")
        }
    }
}