package com.example.navyuga.feature.auth.domain.repository

import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.auth.data.model.UserModel
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginUser(email: String, pass: String): Flow<UiState<UserModel>>
    suspend fun registerUser(user: UserModel, pass: String): Flow<UiState<String>>
}