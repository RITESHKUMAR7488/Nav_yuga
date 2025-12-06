package com.example.navyuga.feature.auth.data.repository

import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.auth.data.model.UserModel
import com.example.navyuga.feature.auth.domain.repository.AuthRepository
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

    // COROUTINES EXPLANATION:
    // 1. suspend fun: This function pauses execution without blocking the main thread.
    // 2. flow { ... }: We use Flow to emit multiple values (Loading -> Success/Failure) over time.
    // This makes your code robust because it handles the stream of data state reactively.

    override suspend fun loginUser(email: String, pass: String): Flow<UiState<UserModel>> = flow {
        emit(UiState.Loading) // 1. Emit Loading State immediately
        try {
            // 2. Use .await() to suspend coroutine until Firebase finishes (no callbacks!)
            auth.signInWithEmailAndPassword(email, pass).await()
            val uid = auth.currentUser?.uid ?: throw Exception("UID is null")

            val snapshot = firestore.collection("users").document(uid).get().await()
            val user = snapshot.toObject(UserModel::class.java)
                ?: throw Exception("User profile not found")

            emit(UiState.Success(user)) // 3. Emit Success with Data

        } catch (e: Exception) {
            emit(UiState.Failure(e.localizedMessage ?: "Login Failed"))
        }
    }

    override suspend fun registerUser(user: UserModel, pass: String): Flow<UiState<String>> = flow {
        emit(UiState.Loading)
        try {
            auth.createUserWithEmailAndPassword(user.email, pass).await()
            val uid = auth.currentUser?.uid ?: throw Exception("User creation failed")

            val newUser = user.copy(uid = uid)
            firestore.collection("users").document(uid).set(newUser).await()

            emit(UiState.Success("Account Created"))
        } catch (e: Exception) {
            emit(UiState.Failure(e.localizedMessage ?: "Registration Failed"))
        }
    }
}