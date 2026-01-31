package com.example.mahayuga.feature.assetmanager.presentation.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.navyuga.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

// Holds the calculated financials for a specific asset
data class AssetFinanceModel(
    val property: PropertyModel,
    val grossRent: Double,
    val expenses: Double, // Tax + Ops
    val reserves: Double, // 5% Safety Margin
    val ndi: Double,      // Net Distributable Income
    val yield: Double,    // Annualized Yield %
    val payoutStatus: String = "Pending" // Pending, Paid, Processing
)

data class FinanceState(
    val isLoading: Boolean = true,
    val totalRevenue: Double = 0.0,
    val totalNdi: Double = 0.0,
    val avgPortfolioYield: Double = 0.0,
    val assets: List<AssetFinanceModel> = emptyList(),
    // For the "Run Distribution" Modal
    val selectedAsset: AssetFinanceModel? = null
)

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FinanceState())
    val state: StateFlow<FinanceState> = _state.asStateFlow()

    init {
        loadFinanceData()
    }

    private fun loadFinanceData() {
        viewModelScope.launch {
            propertyRepository.getAllProperties().collectLatest { uiState ->
                when (uiState) {
                    is UiState.Success -> calculateFinancials(uiState.data)
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
            // 1. Parse Raw Data
            val value = parseCurrency(prop.totalValuation)
            val rentMonthly = parseDouble(prop.monthlyRent)
            val taxAnnual = parseDouble(prop.annualPropertyTax)

            // 2. Apply BRICX Formulas
            // Expenses = (Tax / 12) + (Maintenance 10% Estimate)
            val monthlyTax = taxAnnual / 12
            val maintEstimate = rentMonthly * 0.10
            val totalExpenses = monthlyTax + maintEstimate

            // Reserves (5% of Gross Rent for Capital Reserve)
            val reserves = rentMonthly * 0.05

            // Formula: NDI = Gross - Expenses - Reserves
            val ndi = (rentMonthly - totalExpenses - reserves).coerceAtLeast(0.0)

            // Formula: Yield = (NDI * 12) / Value * 100
            val annualizedNdi = ndi * 12
            val yield = if (value > 0) (annualizedNdi / value) * 100 else 0.0

            // Aggregation
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

    // --- Actions ---

    fun selectAssetForPayout(asset: AssetFinanceModel) {
        _state.value = _state.value.copy(selectedAsset = asset)
    }

    fun clearSelection() {
        _state.value = _state.value.copy(selectedAsset = null)
    }

    fun executePayout() {
        // Here we would trigger the API call to process the payment
        // For now, we simulate success and close the modal
        clearSelection()
    }

    // --- Helpers ---
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