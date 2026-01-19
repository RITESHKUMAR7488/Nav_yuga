package com.example.mahayuga.feature.auth.domain.repository

import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.auth.data.model.AssetManagerModel
import com.example.mahayuga.feature.auth.data.model.UserModel
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginUser(email: String, pass: String): Flow<UiState<UserModel>>
    suspend fun registerUser(user: UserModel, pass: String): Flow<UiState<String>>

    // ⚡ NEW: Asset Manager Registration
    suspend fun registerAssetManager(am: AssetManagerModel, pass: String): Flow<UiState<String>>

    fun getCurrentUser(): Flow<UiState<UserModel>>
}