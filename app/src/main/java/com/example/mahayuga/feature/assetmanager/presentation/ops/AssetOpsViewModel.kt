// main/java/com/example/mahayuga/feature/assetmanager/presentation/ops/AssetOpsViewModel.kt
package com.example.mahayuga.feature.assetmanager.presentation.ops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- DOMAIN MODELS ---
enum class AssetStatus { LISTED, PENDING }

sealed class TrustAsset {
    abstract val id: String
    abstract val trustName: String
    abstract val stockPrice: Double
    abstract val status: AssetStatus

    data class SmReit(
        override val id: String,
        override val trustName: String,
        override val stockPrice: Double,
        override val status: AssetStatus,
        val propertyName: String,
        val spvTenants: List<String> // SPV1, SPV2, etc.
    ) : TrustAsset()

    data class Reit(
        override val id: String,
        override val trustName: String,
        override val stockPrice: Double,
        override val status: AssetStatus,
        val spvBuildings: List<SpvBuilding>
    ) : TrustAsset()
}

data class SpvBuilding(val buildingName: String, val tenants: List<String>)

// --- STATE ---
sealed class AssetsUiState {
    object Loading : AssetsUiState()
    data class Success(val assets: List<TrustAsset>) : AssetsUiState()
    data class Error(val message: String) : AssetsUiState()
}

@HiltViewModel
class AssetOpsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<AssetsUiState>(AssetsUiState.Loading)
    val uiState: StateFlow<AssetsUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allAssets = listOf<TrustAsset>()

    init {
        fetchAssets()
    }

    // ⚡ COROUTINE USAGE HERE ⚡
    private fun fetchAssets() {
        // viewModelScope.launch starts a coroutine tied to the ViewModel's lifecycle.
        // It prevents the UI thread from blocking while we wait for backend data.
        viewModelScope.launch {
            _uiState.value = AssetsUiState.Loading

            // suspend function simulating network latency.
            // In reality, this would be a suspend function call to your Repository/Retrofit.
            delay(1500)

            allAssets = listOf(
                TrustAsset.Reit(
                    id = "R-101",
                    trustName = "Bricx Nexus REIT Trust",
                    stockPrice = 345.50,
                    status = AssetStatus.LISTED,
                    spvBuildings = listOf(
                        SpvBuilding(
                            "SPV1 (Alpha Tower)",
                            listOf("SPVa (TechCorp)", "SPVb (FinServe)")
                        ),
                        SpvBuilding(
                            "SPV2 (Beta Block)",
                            listOf("SPVa (Retail)", "SPVb (Logistics)")
                        )
                    )
                ),
                TrustAsset.SmReit(
                    id = "SM-205",
                    trustName = "CyberHub SM-REIT",
                    stockPrice = 112.75,
                    status = AssetStatus.PENDING,
                    propertyName = "SPV Main (CyberHub)",
                    spvTenants = listOf("SPV1 (WeWork)", "SPV2 (Starbucks)", "SPV3 (Cisco)")
                ),
                TrustAsset.Reit(
                    id = "R-102",
                    trustName = "Bricx Horizon Trust",
                    stockPrice = 420.00,
                    status = AssetStatus.LISTED,
                    spvBuildings = listOf(
                        SpvBuilding("SPV1 (Horizon A)", listOf("SPVa (Oracle)", "SPVb (SAP)")),
                        SpvBuilding("SPV3 (Horizon B)", listOf("SPVa (Adobe)"))
                    )
                )
            )
            _uiState.value = AssetsUiState.Success(allAssets)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterAssets(query)
    }

    private fun filterAssets(query: String) {
        val currentState = _uiState.value
        if (currentState is AssetsUiState.Success || allAssets.isNotEmpty()) {
            val filtered = if (query.isBlank()) {
                allAssets
            } else {
                allAssets.filter { it.trustName.contains(query, ignoreCase = true) }
            }
            _uiState.value = AssetsUiState.Success(filtered)
        }
    }
}