// main/java/com/example/mahayuga/feature/navyuga/presentation/portfolio/PortfolioViewModel.kt
package com.example.mahayuga.feature.navyuga.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- NEW DATA MODELS ---

data class NewPortfolioHolding(
    val id: String,
    val name: String,
    val type: String,
    val invested: String,
    val totalUnits: String,
    val pricePerUnit: String,
    val buyPrice: String,
    val currentPrice: String,
    val currentValue: String,
    val growth: String,
    val isPositiveGrowth: Boolean,
    val priceHistory: List<Float>
)

data class NewPortfolioState(
    val isLoading: Boolean = false,
    val portfolioValue: String = "₹48,800 Cr",
    val dailyChangeValue: String = "₹346.47",
    val dailyChangePercent: String = "+0.54%",
    val isPositiveChange: Boolean = true,
    val smReitPercent: Float = 10f,
    val reitPercent: Float = 90f,
    val propertiesCount: String = "01",
    val totalInvested: String = "₹1",
    val totalSqFt: String = "01",
    val totalDividend: String = "₹1",
    val avgRoi: String = "1%",
    val totalGrowth: String = "1%",
    val holdings: List<NewPortfolioHolding> = emptyList()
)

@HiltViewModel
class PortfolioViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NewPortfolioState())
    val uiState: StateFlow<NewPortfolioState> = _uiState.asStateFlow()

    init {
        loadPortfolioData()
    }

    private fun loadPortfolioData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Simulate network delay
            delay(800)

            _uiState.value = NewPortfolioState(
                isLoading = false,
                portfolioValue = "₹48,800 Cr",
                dailyChangeValue = "₹346.47",
                dailyChangePercent = "+0.54%",
                isPositiveChange = true,
                smReitPercent = 10f,
                reitPercent = 90f,
                propertiesCount = "01",
                totalInvested = "₹1",
                totalSqFt = "01",
                totalDividend = "₹1",
                avgRoi = "1%",
                totalGrowth = "1%",
                holdings = listOf(
                    NewPortfolioHolding(
                        id = "1",
                        name = "Mumbai Tech Park",
                        type = "SM REIT",
                        invested = "₹1",
                        totalUnits = "4",
                        pricePerUnit = "₹1",
                        buyPrice = "₹1",
                        currentPrice = "₹1",
                        currentValue = "₹18,407.95",
                        growth = "20%",
                        isPositiveGrowth = true,
                        // Dummy data for the line chart (trending upwards)
                        priceHistory = listOf(100f, 105f, 103f, 110f, 115f, 112f, 120f)
                    ),
                    NewPortfolioHolding(
                        id = "2",
                        name = "Embassy Tech Village",
                        type = "REIT",
                        invested = "₹5,000",
                        totalUnits = "10",
                        pricePerUnit = "₹500",
                        buyPrice = "₹500",
                        currentPrice = "₹550",
                        currentValue = "₹5,500.00",
                        growth = "10%",
                        isPositiveGrowth = true,
                        priceHistory = listOf(500f, 510f, 520f, 515f, 530f, 540f, 550f)
                    )
                )
            )
        }
    }
}