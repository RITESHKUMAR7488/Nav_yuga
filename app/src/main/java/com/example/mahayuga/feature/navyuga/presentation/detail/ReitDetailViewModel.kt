package com.example.mahayuga.feature.navyuga.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.feature.navyuga.domain.repository.MarketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- MISSING UI STATE MODELS ADDED HERE ---
data class ReitDetailData(
    val id: String,
    val name: String,
    val symbol: String,
    val currentPrice: Double,
    val priceChange: Double,
    val percentageChange: Double,
    val isPositive: Boolean,
    val images: List<String>,
    val description: String,
    val propertyType: String,
    val totalArea: String,
    val occupancyRate: String,
    val majorTenants: List<String>,
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
    val newsItems: List<Any> = emptyList()
)

sealed class ReitDetailState {
    object Loading : ReitDetailState()
    data class Success(val data: ReitDetailData) : ReitDetailState()
    data class Error(val message: String) : ReitDetailState()
}
// ------------------------------------------

@HiltViewModel
class ReitDetailViewModel @Inject constructor(
    private val marketRepository: MarketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReitDetailState>(ReitDetailState.Loading)
    val uiState: StateFlow<ReitDetailState> = _uiState.asStateFlow()

    private val _isWatchlisted = MutableStateFlow(false)
    val isWatchlisted: StateFlow<Boolean> = _isWatchlisted.asStateFlow()

    fun fetchAssetDetails(assetId: String) {
        val cleanSymbol = assetId.replace(".NS", "").replace(".BO", "")

        viewModelScope.launch {
            _uiState.value = ReitDetailState.Loading

            // 1. Fetch Fundamental Historical Data using Lisuns REST API
            val histResult = marketRepository.getHistoricalData(cleanSymbol)
            val historicalQuote = histResult.getOrNull()

            // 2. Connect to the Websocket stream for Real-Time data
            marketRepository.getLiveQuotesFlow(listOf(cleanSymbol)).collect { quotes ->
                val liveQuote = quotes.firstOrNull() ?: return@collect

                val mCapString = if (historicalQuote?.marketCap != null && historicalQuote.marketCap > 0) {
                    "₹${historicalQuote.marketCap / 10000000} Cr"
                } else "N/A"

                val data = ReitDetailData(
                    id = assetId,
                    name = cleanSymbol,
                    symbol = "$cleanSymbol • NSE",
                    currentPrice = liveQuote.currentPrice,
                    priceChange = liveQuote.priceChange,
                    percentageChange = liveQuote.percentageChange,
                    isPositive = liveQuote.isPositive,
                    images = listOf(
                        "https://images.unsplash.com/photo-1497366216548-37526070297c",
                        "https://images.unsplash.com/photo-1416331108676-a22ccb276e35"
                    ),
                    description = "Real estate investment trust offering consistent yields through premium commercial properties.",
                    propertyType = "Commercial Office",
                    totalArea = "31.3M sq ft",
                    occupancyRate = "89.5%",
                    majorTenants = listOf("Accenture", "Barclays", "Cognizant"),
                    chartPoints = generateDummyChartData(),
                    marketCap = mCapString,
                    peRatio = "45.2",
                    dividendYield = "6.2%",
                    dayLow = liveQuote.dayLow.toString(),
                    dayHigh = liveQuote.dayHigh.toString(),
                    week52Low = "385.00",
                    week52High = "480.25",
                    volume = historicalQuote?.volume?.toString() ?: "N/A",
                    avgVolume = "850K"
                )

                _uiState.value = ReitDetailState.Success(data)
            }
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
        return points
    }
}