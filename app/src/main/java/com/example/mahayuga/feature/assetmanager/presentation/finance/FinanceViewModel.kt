// main/java/com/example/mahayuga/feature/assetmanager/presentation/finance/FinanceViewModel.kt
package com.example.mahayuga.feature.assetmanager.presentation.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
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
    private val firestore: FirebaseFirestore
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
                        // Filter Logic
                        val myProps = uiState.data.filter { it.assetManager.equals(amName, true) }
                        calculateFinancials(myProps)
                    }

                    is UiState.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is UiState.Failure -> _state.value = _state.value.copy(isLoading = false)
                    else -> {}
                }
            }
        }
    }

    private fun calculateFinancials(properties: List<PropertyModel>) {
        var globalRevenue = 0.0
        var globalNdi = 0.0
        var yieldAccumulator = 0.0

        val financeModels = properties.map { prop ->
            val value = parseCurrency(prop.totalValuation)
            val rentMonthly = parseDouble(prop.monthlyRent)
            val taxAnnual = parseDouble(prop.annualPropertyTax)

            val monthlyTax = taxAnnual / 12
            val maintEstimate = rentMonthly * 0.10
            val totalExpenses = monthlyTax + maintEstimate

            val reserves = rentMonthly * 0.05
            val ndi = (rentMonthly - totalExpenses - reserves).coerceAtLeast(0.0)

            val annualizedNdi = ndi * 12
            val yield = if (value > 0) (annualizedNdi / value) * 100 else 0.0

            globalRevenue += rentMonthly
            globalNdi += ndi
            yieldAccumulator += yield

            AssetFinanceModel(
                property = prop,
                grossRent = rentMonthly,
                expenses = totalExpenses,
                reserves = reserves,
                ndi = ndi,
                yield = yield
            )
        }

        val avgYield = if (properties.isNotEmpty()) yieldAccumulator / properties.size else 0.0

        _state.value = FinanceState(
            isLoading = false,
            totalRevenue = globalRevenue,
            totalNdi = globalNdi,
            avgPortfolioYield = avgYield,
            assets = financeModels
        )
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

    private fun parseCurrency(value: String): Double {
        val clean = value.replace("₹", "").replace(",", "").trim().lowercase()
        return when {
            clean.contains("cr") -> clean.replace("cr", "").toDoubleOrNull()?.times(10000000) ?: 0.0
            clean.contains("l") -> clean.replace("lakhs", "").replace("lakh", "").replace("l", "")
                .toDoubleOrNull()?.times(100000) ?: 0.0

            else -> clean.toDoubleOrNull() ?: 0.0
        }
    }

    private fun parseDouble(value: String): Double {
        return value.replace(",", "").replace("₹", "").trim().toDoubleOrNull() ?: 0.0
    }
}