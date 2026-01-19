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

    override suspend fun registerInvestment(investment: InvestmentModel): UiState<String> {
        return try {
            firestore.runTransaction { transaction ->
                val userRef = firestore.collection("users").document(investment.userId)
                val propertyRef = firestore.collection("properties").document(investment.propertyId)
                val investmentRef = firestore.collection("investments").document()

                val userSnapshot = transaction.get(userRef)
                val propertySnapshot = transaction.get(propertyRef)

                fun parseLong(str: String?): Long = str?.replace(Regex("[^\\d]"), "")?.toLongOrNull() ?: 0L
                fun parseDouble(str: String?): Double = str?.replace(Regex("[^\\d.]"), "")?.toDoubleOrNull() ?: 0.0

                val propValuation = parseLong(propertySnapshot.getString("totalValuation"))
                val propArea = parseDouble(propertySnapshot.getString("area"))
                val propRent = parseLong(propertySnapshot.getString("monthlyRent"))
                val propAssetId = propertySnapshot.getString("assetId") ?: ""

                val ownershipFraction = if (propValuation > 0) investment.amount.toDouble() / propValuation else 0.0
                val addedArea = propArea * ownershipFraction
                val addedRent = (propRent * ownershipFraction).toLong()

                val currentInvest = userSnapshot.getLong("totalInvestment") ?: 0L
                val currentVal = userSnapshot.getLong("currentValue") ?: 0L
                val currentArea = userSnapshot.getDouble("totalArea") ?: 0.0
                val currentRent = userSnapshot.getLong("totalRent") ?: 0L

                val newInvest = currentInvest + investment.amount
                val newVal = currentVal + investment.amount
                val newArea = currentArea + addedArea
                val newRent = currentRent + addedRent

                val currentPropFunding = parseLong(propertySnapshot.getString("totalFunding"))
                val newPropFunding = currentPropFunding + investment.amount

                transaction.set(investmentRef, investment.copy(id = investmentRef.id, assetId = propAssetId))

                transaction.update(userRef, mapOf(
                    "totalInvestment" to newInvest,
                    "currentValue" to newVal,
                    "totalArea" to newArea,
                    "totalRent" to newRent,
                    "investedProperties" to FieldValue.arrayUnion(investment.propertyId)
                ))
                transaction.update(propertyRef, "totalFunding", newPropFunding.toString())

            }.await()
            UiState.Success("Investment Registered Successfully")
        } catch (e: Exception) {
            UiState.Failure(e.message ?: "Transaction Failed")
        }
    }

    override fun getUserInvestments(userId: String): Flow<UiState<List<InvestmentModel>>> = callbackFlow {
        trySend(UiState.Loading)
        val listener = firestore.collection("investments")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(UiState.Failure(error.message ?: "Error loading investments"))
                    return@addSnapshotListener
                }
                val investments = snapshot?.toObjects(InvestmentModel::class.java) ?: emptyList()
                trySend(UiState.Success(investments))
            }
        awaitClose { listener.remove() }
    }

    // ⚡ FIX: Correctly revert Area and Rent
    override suspend fun deleteInvestment(investment: InvestmentModel): UiState<String> {
        return try {
            firestore.runTransaction { transaction ->
                val propertyRef = firestore.collection("properties").document(investment.propertyId)
                val userRef = firestore.collection("users").document(investment.userId)
                val invRef = firestore.collection("investments").document(investment.id)

                val propSnapshot = transaction.get(propertyRef)
                val userSnapshot = transaction.get(userRef)

                // 1. Revert Property Funding
                if (propSnapshot.exists()) {
                    val currentFunding = propSnapshot.getString("totalFunding")?.replace(Regex("[^\\d]"), "")?.toLongOrNull() ?: 0L
                    val newFunding = (currentFunding - investment.amount).coerceAtLeast(0)
                    transaction.update(propertyRef, "totalFunding", newFunding.toString())
                }

                // 2. Revert User Stats
                if (userSnapshot.exists() && propSnapshot.exists()) {
                    val currentInvest = userSnapshot.getLong("totalInvestment") ?: 0L
                    val currentVal = userSnapshot.getLong("currentValue") ?: 0L
                    val currentArea = userSnapshot.getDouble("totalArea") ?: 0.0
                    val currentRent = userSnapshot.getLong("totalRent") ?: 0L

                    // Calculate amounts to remove
                    fun parseLong(str: String?): Long = str?.replace(Regex("[^\\d]"), "")?.toLongOrNull() ?: 0L
                    fun parseDouble(str: String?): Double = str?.replace(Regex("[^\\d.]"), "")?.toDoubleOrNull() ?: 0.0

                    val propValuation = parseLong(propSnapshot.getString("totalValuation"))
                    val propArea = parseDouble(propSnapshot.getString("area"))
                    val propRent = parseLong(propSnapshot.getString("monthlyRent"))

                    val ownershipFraction = if (propValuation > 0) investment.amount.toDouble() / propValuation else 0.0
                    val areaToRemove = propArea * ownershipFraction
                    val rentToRemove = (propRent * ownershipFraction).toLong()

                    val newInvest = (currentInvest - investment.amount).coerceAtLeast(0)
                    val newVal = (currentVal - investment.amount).coerceAtLeast(0)
                    val newArea = (currentArea - areaToRemove).coerceAtLeast(0.0)
                    val newRent = (currentRent - rentToRemove).coerceAtLeast(0)

                    transaction.update(userRef, mapOf(
                        "totalInvestment" to newInvest,
                        "currentValue" to newVal,
                        "totalArea" to newArea, // ⚡ Updated
                        "totalRent" to newRent, // ⚡ Updated
                        "investedProperties" to FieldValue.arrayRemove(investment.propertyId)
                    ))
                }

                // 3. Delete the Record
                transaction.delete(invRef)
            }.await()
            UiState.Success("Investment Deleted & Funds Reverted")
        } catch (e: Exception) {
            UiState.Failure(e.message ?: "Delete Failed")
        }
    }

    override suspend fun deleteUserConstructively(userId: String): UiState<String> {
        return try {
            val snapshot = firestore.collection("investments")
                .whereEqualTo("userId", userId)
                .get().await()

            val investments = snapshot.toObjects(InvestmentModel::class.java)

            investments.forEach { inv ->
                val result = deleteInvestment(inv)
                if (result is UiState.Failure) throw Exception("Failed to unwind investment: ${inv.id}")
            }

            firestore.collection("users").document(userId).delete().await()

            UiState.Success("User and Portfolio Deleted Successfully")
        } catch (e: Exception) {
            UiState.Failure(e.message ?: "Cascade Delete Failed")
        }
    }
}