// main/java/com/example/mahayuga/feature/navyuga/presentation/detail/ReitDetailViewModel.kt
package com.example.mahayuga.feature.navyuga.presentation.detail

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
data class ReitDetailData(
    val id: String,
    val name: String,
    val symbol: String,
    val currentPrice: Double,
    val priceChange: Double,
    val percentageChange: Double,
    val isPositive: Boolean,

    // Estate Data
    val images: List<String>,
    val description: String,
    val propertyType: String,
    val totalArea: String,
    val occupancyRate: String,
    val majorTenants: List<String>,

    // Finance Data (Expanded with High/Low/Volume)
    val chartPoints: List<Pair<Float, Float>>,
    val marketCap: String,
    val peRatio: String,
    val dividendYield: String,
    val dayLow: String,
    val dayHigh: String,
    val week52Low: String,
    val week52High: String,
    val volume: String,
    val avgVolume: String,

    // News Data
    val newsItems: List<ReitNewsItem>
)

data class ReitNewsItem(
    val id: String,
    val title: String,
    val source: String,
    val timeAgo: String
)

sealed class ReitDetailState {
    object Loading : ReitDetailState()
    data class Success(val data: ReitDetailData) : ReitDetailState()
    data class Error(val message: String) : ReitDetailState()
}

@HiltViewModel
class ReitDetailViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<ReitDetailState>(ReitDetailState.Loading)
    val uiState: StateFlow<ReitDetailState> = _uiState.asStateFlow()

    private val _isWatchlisted = MutableStateFlow(false)
    val isWatchlisted: StateFlow<Boolean> = _isWatchlisted.asStateFlow()

    fun fetchAssetDetails(assetId: String) {
        viewModelScope.launch {
            _uiState.value = ReitDetailState.Loading
            delay(600) // Simulate API call

            // Simulating fetching data based on typical REIT market stats
            val data = ReitDetailData(
                id = assetId,
                name = "Mindspace Business Parks REIT",
                symbol = "MINDSPACE • NSE",
                currentPrice = 458.90,
                priceChange = 4.96,
                percentageChange = 1.09,
                isPositive = true,
                images = listOf(
                    "https://images.unsplash.com/photo-1497366216548-37526070297c",
                    "https://images.unsplash.com/photo-1416331108676-a22ccb276e35",
                    "https://images.unsplash.com/photo-1572025442646-866d16c84a54"
                ),
                description = "Mindspace Business Parks REIT owns and operates a portfolio of office parks and commercial properties in India.",
                propertyType = "Commercial Office",
                totalArea = "31.3M sq ft",
                occupancyRate = "89.5%",
                majorTenants = listOf("Accenture", "Barclays", "Cognizant"),
                chartPoints = generateDummyChartData(),
                marketCap = "₹27,215 Cr",
                peRatio = "45.2",
                dividendYield = "6.2%",
                dayLow = "450.10",
                dayHigh = "462.50",
                week52Low = "385.00",
                week52High = "480.25",
                volume = "1.2M",
                avgVolume = "850K",
                newsItems = listOf(
                    ReitNewsItem(
                        "1",
                        "Mindspace REIT reports 15% YoY growth in Net Operating Income for Q3.",
                        "Moneycontrol",
                        "2h ago"
                    ),
                    ReitNewsItem(
                        "2",
                        "Global tech giants renew leases at Mindspace Airoli West.",
                        "Economic Times",
                        "1d ago"
                    ),
                    ReitNewsItem(
                        "3",
                        "Analysis: Why Indian Office REITs are bouncing back.",
                        "Bloomberg",
                        "3d ago"
                    )
                )
            )

            _uiState.value = ReitDetailState.Success(data)
        }
    }

    fun toggleWatchlist() {
        _isWatchlisted.value = !_isWatchlisted.value
    }

    private fun generateDummyChartData(): List<Pair<Float, Float>> {
        val points = mutableListOf<Pair<Float, Float>>()
        var currentY = 440f
        for (i in 0..50) {
            points.add(Pair(i.toFloat(), currentY))
            currentY += (-5..6).random().toFloat()
        }
        points.add(Pair(51f, 458.9f))
        return points
    }
}