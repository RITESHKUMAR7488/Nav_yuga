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

// --- DATA MODELS ---

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

data class PortfolioPosition(
    val id: String,
    val name: String,
    val type: String,
    val orderType: String,
    val quantity: String,
    val orderPrice: String,
    val status: String
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
    val holdings: List<NewPortfolioHolding> = emptyList(),
    val positions: List<PortfolioPosition> = emptyList()
)

@HiltViewModel
class PortfolioViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NewPortfolioState())
    val uiState: StateFlow<NewPortfolioState> = _uiState.asStateFlow()

    init {
        loadPortfolioData()
    }

    private fun loadPortfolioData() {
        // COROUTINE EXPLANATION:
        // viewModelScope.launch starts a coroutine tied to this ViewModel's lifecycle.
        // The delay(800) simulates a network call to your broker API.
        // This makes the code robust because if the user leaves the screen, the call is cleanly cancelled,
        // and the UI thread remains completely unblocked while waiting for the data.
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Simulate broker API network delay
            delay(800)

            _uiState.value = NewPortfolioState(
                isLoading = false,
                portfolioValue = "₹48,800 Cr",
                dailyChangeValue = "₹346.47",
                dailyChangePercent = "+0.54%",
                isPositiveChange = true,
                smReitPercent = 25f, // Adjusted for visual demonstration
                reitPercent = 75f,
                propertiesCount = "02",
                totalInvested = "₹5,001",
                totalSqFt = "14",
                totalDividend = "₹1",
                avgRoi = "8.5%",
                totalGrowth = "12%",
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
                        priceHistory = listOf(500f, 490f, 510f, 520f, 515f, 530f, 550f)
                    )
                ),
                positions = listOf(
                    PortfolioPosition(
                        id = "P1",
                        name = "Nexus Select Trust",
                        type = "REIT",
                        orderType = "BUY",
                        quantity = "50",
                        orderPrice = "₹135.50",
                        status = "PENDING"
                    ),
                    PortfolioPosition(
                        id = "P2",
                        name = "Mindspace Business Parks",
                        type = "REIT",
                        orderType = "SELL",
                        quantity = "20",
                        orderPrice = "₹340.00",
                        status = "EXECUTING"
                    )
                )
            )
        }
    }
}