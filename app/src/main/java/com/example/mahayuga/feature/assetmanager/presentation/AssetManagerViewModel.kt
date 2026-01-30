package com.example.mahayuga.feature.assetmanager.presentation

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

data class PortfolioState(
    val isLoading: Boolean = true,
    // Formula 1: AUM
    val totalAum: Double = 0.0,
    // Formula 3: Cash Inflow
    val cashInflow: Double = 0.0,
    // Formula 4: Obligations
    val obligations: Double = 0.0,
    // Formula 6: Portfolio IRR (Average ROI for now)
    val portfolioIrr: Double = 0.0,
    // Formula 7: Red Flag
    val hasRedFlag: Boolean = false,

    // Breakdown for Asset Grid
    val assetBreakdown: Map<String, Int> = emptyMap()
)

@HiltViewModel
class AssetManagerViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PortfolioState())
    val state: StateFlow<PortfolioState> = _state.asStateFlow()

    init {
        loadPortfolioData()
    }

    private fun loadPortfolioData() {
        viewModelScope.launch {
            propertyRepository.getAllProperties().collectLatest { uiState ->
                when (uiState) {
                    is UiState.Success -> calculateMetrics(uiState.data)
                    is UiState.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is UiState.Failure -> _state.value = _state.value.copy(isLoading = false)
                    else -> {}
                }
            }
        }
    }

    private fun calculateMetrics(properties: List<PropertyModel>) {
        var sumAum = 0.0
        var sumMonthlyRent = 0.0
        var sumMonthlyTax = 0.0
        var sumRoi = 0.0
        val typeCount = mutableMapOf<String, Int>()

        properties.forEach { prop ->
            // AUM
            sumAum += parseCurrency(prop.totalValuation)

            // Cash Flow (Rent)
            val rent = parseDouble(prop.monthlyRent)
            sumMonthlyRent += rent

            // Obligations (Tax / 12)
            val taxAnnual = parseDouble(prop.annualPropertyTax)
            sumMonthlyTax += (taxAnnual / 12)

            // IRR/ROI
            sumRoi += prop.roi

            // Breakdown
            val type = prop.type.ifBlank { "Other" }
            typeCount[type] = typeCount.getOrDefault(type, 0) + 1
        }

        val surplus = sumMonthlyRent - sumMonthlyTax
        val avgIrr = if (properties.isNotEmpty()) sumRoi / properties.size else 0.0
        val redFlag = surplus < 0 // Simple flag logic based on cash flow

        _state.value = PortfolioState(
            isLoading = false,
            totalAum = sumAum,
            cashInflow = sumMonthlyRent,
            obligations = sumMonthlyTax,
            portfolioIrr = avgIrr,
            hasRedFlag = redFlag,
            assetBreakdown = typeCount
        )
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
