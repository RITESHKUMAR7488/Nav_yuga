package com.example.mahayuga.feature.auth.domain.repository

import android.util.Log
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.auth.data.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun loginUser(email: String, pass: String): Flow<UiState<UserModel>> = flow {
        emit(UiState.Loading)
        try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val uid = result.user?.uid

            if (uid != null) {
                // Fetch fresh data
                val document = firestore.collection("users").document(uid).get().await()
                val user = document.toObject(UserModel::class.java)

                if (user != null) {
                    // Check if approved (Note: Admin accounts set to true in DB will pass this)
                    if (user.isApproved) {
                        emit(UiState.Success(user))
                    } else {
                        emit(UiState.Failure("Account pending admin approval."))
                        auth.signOut()
                    }
                } else {
                    emit(UiState.Failure("User data not found"))
                }
            } else {
                emit(UiState.Failure("Login failed"))
            }
        } catch (e: Exception) {
            emit(UiState.Failure(e.message ?: "Unknown error"))
        }
    }

    override suspend fun registerUser(user: UserModel, pass: String): Flow<UiState<String>> = flow {
        emit(UiState.Loading)
        try {
            val result = auth.createUserWithEmailAndPassword(user.email, pass).await()
            val uid = result.user?.uid

            if (uid != null) {
                // ⚡ FORCE 'isApproved = false' for all new registrations
                // The @PropertyName in UserModel ensures this saves correctly as "isApproved"
                val newUser = user.copy(uid = uid, isApproved = false)

                firestore.collection("users").document(uid).set(newUser).await()

                Log.d("AuthRepo", "User Registered: ${newUser.email}, isApproved: ${newUser.isApproved}")
                emit(UiState.Success("Request Sent! Waiting for Admin Approval."))
            } else {
                emit(UiState.Failure("Registration failed: No UID returned"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Registration Error", e)
            emit(UiState.Failure(e.message ?: "Unknown error"))
        }
    }

    override fun getCurrentUser(): Flow<UiState<UserModel>> = callbackFlow {
        trySend(UiState.Loading)
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(UiState.Failure("Not logged in"))
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(UiState.Failure(error.message ?: "Sync error"))
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(UserModel::class.java)
                if (user != null) trySend(UiState.Success(user)) else trySend(UiState.Failure("User not found"))
            }
        awaitClose { listener.remove() }
    }
}