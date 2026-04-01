package com.example.mahayuga.feature.admin.domain.repository

import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.admin.data.model.InvestmentModel
import com.example.mahayuga.feature.auth.data.model.AssetManagerModel
import com.example.mahayuga.feature.auth.data.model.UserModel
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getAllUsers(): Flow<UiState<List<UserModel>>>
    suspend fun toggleUserStatus(uid: String, isActive: Boolean): UiState<String>

    // --- User Requests ---
    fun getPendingRequests(): Flow<UiState<List<UserModel>>>
    suspend fun approveUserRequest(uid: String, role: String): UiState<String>
    suspend fun rejectUserRequest(uid: String): UiState<String>

    // --- Asset Manager Requests (NEW) ---
    fun getPendingAssetManagers(): Flow<UiState<List<AssetManagerModel>>>
    suspend fun approveAssetManager(uid: String): UiState<String>
    suspend fun rejectAssetManager(uid: String): UiState<String>

    // --- Investment Registration ---
    suspend fun registerInvestment(investment: InvestmentModel): UiState<String>

    // --- Portfolio Management ---
    fun getUserInvestments(userId: String): Flow<UiState<List<InvestmentModel>>>
    suspend fun deleteInvestment(investment: InvestmentModel): UiState<String>
    suspend fun deleteUserConstructively(userId: String): UiState<String>
}