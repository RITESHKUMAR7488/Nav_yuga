package com.example.mahayuga.feature.auth.domain.repository

import android.util.Log
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.auth.data.model.AssetManagerModel
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
                // 1. Try fetching as User
                val userDoc = firestore.collection("users").document(uid).get().await()
                if (userDoc.exists()) {
                    val user = userDoc.toObject(UserModel::class.java)
                    if (user != null && user.isApproved) {
                        emit(UiState.Success(user))
                    } else {
                        emit(UiState.Failure("Account pending admin approval."))
                        auth.signOut()
                    }
                } else {
                    // 2. Try fetching as Asset Manager
                    val amDoc = firestore.collection("asset_managers").document(uid).get().await()
                    if (amDoc.exists()) {
                        // For login flow, we map AM to a basic UserModel structure to satisfy the return type
                        // In a real app, you might want a sealed class for UserType
                        val am = amDoc.toObject(AssetManagerModel::class.java)
                        val mappedUser = UserModel(
                            uid = uid,
                            email = am?.email ?: "",
                            name = am?.contactName ?: "Partner",
                            role = "asset_manager",
                            isApproved = am?.accountStatus == "VERIFIED"
                        )
                        emit(UiState.Success(mappedUser))
                    } else {
                        emit(UiState.Failure("User data not found"))
                    }
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
                val newUser = user.copy(uid = uid, isApproved = false)
                firestore.collection("users").document(uid).set(newUser).await()
                emit(UiState.Success("Request Sent! Waiting for Admin Approval."))
            } else {
                emit(UiState.Failure("Registration failed: No UID returned"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Registration Error", e)
            emit(UiState.Failure(e.message ?: "Unknown error"))
        }
    }

    // ⚡ NEW IMPLEMENTATION
    override suspend fun registerAssetManager(am: AssetManagerModel, pass: String): Flow<UiState<String>> = flow {
        emit(UiState.Loading)
        try {
            val result = auth.createUserWithEmailAndPassword(am.email, pass).await()
            val uid = result.user?.uid

            if (uid != null) {
                // Save to 'asset_managers' collection
                val newAm = am.copy(uid = uid, accountStatus = "PENDING")
                firestore.collection("asset_managers").document(uid).set(newAm).await()
                emit(UiState.Success("Partner Application Submitted Successfully!"))
            } else {
                emit(UiState.Failure("Registration failed: No UID returned"))
            }
        } catch (e: Exception) {
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

        // Listener for Users
        val userListener = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(UserModel::class.java)
                    if (user != null) trySend(UiState.Success(user))
                } else {
                    // Fallback check for Asset Managers if not found in users
                    firestore.collection("asset_managers").document(uid).get()
                        .addOnSuccessListener { amSnap ->
                            if (amSnap.exists()) {
                                val am = amSnap.toObject(AssetManagerModel::class.java)
                                val mapped = UserModel(
                                    uid = uid,
                                    name = am?.contactName ?: "",
                                    role = "asset_manager"
                                )
                                trySend(UiState.Success(mapped))
                            } else {
                                trySend(UiState.Failure("User not found"))
                            }
                        }
                }
            }
        awaitClose { userListener.remove() }
    }
}