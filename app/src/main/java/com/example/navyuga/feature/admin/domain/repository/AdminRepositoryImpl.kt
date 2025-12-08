package com.example.navyuga.feature.admin.data.repository

import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.admin.domain.repository.AdminRepository
import com.example.navyuga.feature.auth.data.model.UserModel
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

        // Listen to real-time updates
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
            firestore.collection("users").document(uid)
                .update("isActive", isActive)
                .await()
            UiState.Success("User status updated")
        } catch (e: Exception) {
            UiState.Failure(e.message ?: "Update failed")
        }
    }
}