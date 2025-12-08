package com.example.navyuga.feature.admin.domain.repository

import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.auth.data.model.UserModel
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getAllUsers(): Flow<UiState<List<UserModel>>>
    suspend fun toggleUserStatus(uid: String, isActive: Boolean): UiState<String>
}