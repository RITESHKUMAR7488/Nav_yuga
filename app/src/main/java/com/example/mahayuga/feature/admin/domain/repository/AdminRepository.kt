package com.example.mahayuga.feature.admin.domain.repository

import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.auth.data.model.UserModel
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getAllUsers(): Flow<UiState<List<UserModel>>>
    suspend fun toggleUserStatus(uid: String, isActive: Boolean): UiState<String>

    // ⚡ NEW METHODS for Requests
    fun getPendingRequests(): Flow<UiState<List<UserModel>>>
    suspend fun approveUserRequest(uid: String, role: String): UiState<String>
    suspend fun rejectUserRequest(uid: String): UiState<String>
}