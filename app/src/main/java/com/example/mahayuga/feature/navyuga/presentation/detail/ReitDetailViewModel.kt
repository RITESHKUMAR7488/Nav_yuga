package com.example.mahayuga.feature.navyuga.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.feature.navyuga.domain.repository.MarketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

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

    // ⚡ REPLACED HARDCODED FIELDS WITH NEW REAL ONES
    val openPrice: String,
    val previousClose: String,
    val dayLow: String,
    val dayHigh: String,
    val volume: String,
    val averageTradedPrice: String,
    val buyPrice: String,
    val buyQty: String,
    val sellPrice: String,
    val sellQty: String,
    val lastTradeQty: String,
    val openInterest: String,
    val quotationLot: String,
    val tradedValue: String,
    val isDummy: Boolean,

    val newsItems: List<Any> = emptyList()
)
sealed class ReitDetailState {
    object Loading : ReitDetailState()
    data class Success(val data: ReitDetailData) : ReitDetailState()
    data class Error(val message: String) : ReitDetailState()
}

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

            // Historical data is now guaranteed to succeed via API or Fallback
            val histResult = marketRepository.getHistoricalData(cleanSymbol)
            val quote = histResult.getOrNull() ?: return@launch

            // Format Market Cap dynamically
            val mCapString = if (quote.marketCap > 0) {
                "₹${quote.marketCap / 10000000} Cr"
            } else "N/A"

            val data = ReitDetailData(
                id = assetId,
                name = cleanSymbol,
                symbol = "$cleanSymbol • NSE",
                currentPrice = quote.currentPrice,
                priceChange = String.format(Locale.US, "%.2f", quote.priceChange).toDouble(),
                percentageChange = String.format(Locale.US, "%.2f", quote.percentageChange).toDouble(),
                isPositive = quote.isPositive,
                images = listOf("https://images.unsplash.com/photo-1497366216548-37526070297c", "https://images.unsplash.com/photo-1416331108676-a22ccb276e35"),
                description = "Real estate investment trust offering consistent yields through premium commercial properties.",
                propertyType = "Commercial Office",
                totalArea = "31.3M sq ft",
                occupancyRate = "89.5%",
                majorTenants = listOf("Accenture", "Barclays", "Cognizant"),
                chartPoints = generateDummyChartData(),

                // ⚡ Mapping the Real Data directly from the WebSocket
                openPrice = quote.openPrice.toString(),
                previousClose = quote.previousClose.toString(),
                dayLow = quote.dayLow.toString(),
                dayHigh = quote.dayHigh.toString(),
                volume = quote.volume.toString(),
                averageTradedPrice = quote.averageTradedPrice.toString(),
                buyPrice = quote.buyPrice.toString(),
                buyQty = quote.buyQty.toString(),
                sellPrice = quote.sellPrice.toString(),
                sellQty = quote.sellQty.toString(),
                lastTradeQty = quote.lastTradeQty.toString(),
                openInterest = quote.openInterest.toString(),
                quotationLot = quote.quotationLot.toString(),
                tradedValue = quote.tradedValue.toString(),
                isDummy = quote.isDummy
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
        return points
    }
}