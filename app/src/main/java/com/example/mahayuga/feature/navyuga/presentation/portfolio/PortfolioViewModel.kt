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
    val symbol: String, // Added symbol for navigation
    val name: String,
    val location: String, // Added location field
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
        // COROUTINES IN ACTION:
        // Function: loadPortfolioData()
        // Usage: viewModelScope.launch { ... }
        // Why it makes the code good: Instead of blocking the Main (UI) thread while waiting for
        // network data, the coroutine suspends execution silently. The UI stays fully responsive,
        // animations remain smooth, and if the user navigates away, the ViewModel safely cancels
        // the coroutine, preventing memory leaks and crashes.
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Simulate broker API network delay non-blockingly
            delay(800)

            _uiState.value = NewPortfolioState(
                isLoading = false,
                portfolioValue = "₹48,800",
                dailyChangeValue = "₹346.47",
                dailyChangePercent = "+0.54%",
                isPositiveChange = true,
                smReitPercent = 35f,
                reitPercent = 65f,
                propertiesCount = "02",
                totalInvested = "₹5,001",
                totalSqFt = "14",
                totalDividend = "₹1",
                avgRoi = "8.5%",
                totalGrowth = "12%",
                holdings = listOf(
                    NewPortfolioHolding(
                        id = "1",
                        symbol = "PSTITANIA",
                        name = "PropShare Titania",
                        location = "Mumbai, Maharashtra",
                        type = "SM REIT",
                        invested = "₹10,000",
                        totalUnits = "10",
                        pricePerUnit = "₹1,000",
                        buyPrice = "₹1,000",
                        currentPrice = "₹1,150",
                        currentValue = "₹11,500.00",
                        growth = "15%",
                        isPositiveGrowth = true,
                        priceHistory = listOf(100f, 105f, 103f, 110f, 115f, 112f, 120f)
                    ),
                    NewPortfolioHolding(
                        id = "2",
                        symbol = "EMBASSY",
                        name = "Embassy Tech Village",
                        location = "Bengaluru, Karnataka",
                        type = "REIT",
                        invested = "₹5,000",
                        totalUnits = "10",
                        pricePerUnit = "₹500",
                        buyPrice = "₹500",
                        currentPrice = "₹480",
                        currentValue = "₹4,800.00",
                        growth = "-4%",
                        isPositiveGrowth = false,
                        priceHistory = listOf(500f, 490f, 510f, 495f, 485f, 490f, 480f)
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
                    )
                )
            )
        }
    }
}