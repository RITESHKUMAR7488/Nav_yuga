// main/java/com/example/mahayuga/feature/assetmanager/presentation/finance/FinanceViewModel.kt
package com.example.mahayuga.feature.assetmanager.presentation.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.assetmanager.domain.usecase.CalculateFinanceMetricsUseCase
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.navyuga.domain.repository.PropertyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class AssetFinanceModel(
    val property: PropertyModel,
    val grossRent: Double,
    val expenses: Double,
    val reserves: Double,
    val ndi: Double,
    val yield: Double,
    val payoutStatus: String = "Pending"
)

data class FinanceState(
    val isLoading: Boolean = true,
    val totalRevenue: Double = 0.0,
    val totalNdi: Double = 0.0,
    val avgPortfolioYield: Double = 0.0,
    val assets: List<AssetFinanceModel> = emptyList(),
    val selectedAsset: AssetFinanceModel? = null
)

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val calculateFinanceMetricsUseCase: CalculateFinanceMetricsUseCase // ⚡ Injected Use Case
) : ViewModel() {

    private val _state = MutableStateFlow(FinanceState())
    val state: StateFlow<FinanceState> = _state.asStateFlow()

    init {
        loadFinanceData()
    }

    private fun loadFinanceData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            var amName = ""
            try {
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    val doc = firestore.collection("asset_managers").document(uid).get().await()
                    if (doc.exists()) {
                        val brand = doc.getString("brandName")
                        val legal = doc.getString("entityLegalName")
                        val contact = doc.getString("contactName")
                        amName = when {
                            !brand.isNullOrBlank() -> brand
                            !legal.isNullOrBlank() -> legal
                            !contact.isNullOrBlank() -> contact
                            else -> "PARTNER"
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            propertyRepository.getAllProperties().collectLatest { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        val myProps = uiState.data.filter { it.assetManager.equals(amName, true) }

                        // ⚡ DELEGATED LOGIC TO USE CASE
                        val result = calculateFinanceMetricsUseCase(myProps)

                        _state.value = FinanceState(
                            isLoading = false,
                            totalRevenue = result.totalRevenue,
                            totalNdi = result.totalNdi,
                            avgPortfolioYield = result.avgPortfolioYield,
                            assets = result.assets
                        )
                    }

                    is UiState.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is UiState.Failure -> _state.value = _state.value.copy(isLoading = false)
                    else -> {}
                }
            }
        }
    }

    fun selectAssetForPayout(asset: AssetFinanceModel) {
        _state.value = _state.value.copy(selectedAsset = asset)
    }

    fun clearSelection() {
        _state.value = _state.value.copy(selectedAsset = null)
    }

    fun executePayout() {
        clearSelection()
    }
}