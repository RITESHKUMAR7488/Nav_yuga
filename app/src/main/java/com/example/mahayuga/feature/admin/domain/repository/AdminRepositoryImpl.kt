package com.example.mahayuga.feature.admin.data.repository

import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.admin.domain.repository.AdminRepository
import com.example.mahayuga.feature.auth.data.model.UserModel
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

    // ⚡ CRITICAL: Fetches Pending Requests
    override fun getPendingRequests(): Flow<UiState<List<UserModel>>> = callbackFlow {
        trySend(UiState.Loading)
        val listener = firestore.collection("users")
            .whereEqualTo("isApproved", false) // Listens for false
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
                    mapOf(
                        "isApproved" to true,
                        "role" to role,
                        "isActive" to true
                    )
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
}