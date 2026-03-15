// main/java/com/example/mahayuga/feature/navyuga/presentation/detail/ReitDetailViewModel.kt
package com.example.mahayuga.feature.navyuga.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.feature.navyuga.domain.model.ReitModel
import com.example.mahayuga.feature.navyuga.domain.repository.MarketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

sealed class ReitDetailState {
    object Loading : ReitDetailState()
    data class Success(val reit: ReitModel) : ReitDetailState()
    data class Error(val message: String) : ReitDetailState()
}

@HiltViewModel
class ReitDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val marketRepository: MarketRepository
) : ViewModel() {

    private val assetId: String = checkNotNull(savedStateHandle["assetId"])
    private val _uiState = MutableStateFlow<ReitDetailState>(ReitDetailState.Loading)
    val uiState: StateFlow<ReitDetailState> = _uiState.asStateFlow()

    init {
        fetchReitData(assetId)
    }

    private fun fetchReitData(id: String) {
        // ⚡ COROUTINE USAGE: viewModelScope.launch ties this network call to the lifecycle of this ViewModel.
        // If the user quickly navigates back before the Yahoo API responds, this coroutine is automatically cancelled,
        // preventing memory leaks and avoiding crashes caused by trying to update a dead UI.
        viewModelScope.launch {
            _uiState.value = ReitDetailState.Loading

            val marketResult = marketRepository.getLiveQuotes(listOf(id))

            marketResult.onSuccess { quotes ->
                val liveQuote = quotes.firstOrNull()

                if (liveQuote != null) {
                    val cleanName = when (id) {
                        "MINDSPACE.NS" -> "Mindspace Business Parks"
                        "EMBASSY.NS" -> "Embassy Office Parks"
                        "NEXUS.NS" -> "Nexus Select Trust"
                        "BIRET.NS" -> "Brookfield India Trust"
                        "PSTITANIA.BO" -> "Prop Share Titania"
                        "PSPLATINA.BO" -> "Prop Share Platina"
                        else -> liveQuote.name.split(",")[0]
                    }

                    val (portVal, holdings, dev) = when (id) {
                        "MINDSPACE.NS" -> listOf("₹28,000 Cr", 32.3, 2.8)
                        "EMBASSY.NS" -> listOf("₹48,800 Cr", 42.6, 8.1)
                        "NEXUS.NS" -> listOf("₹22,000 Cr", 9.8, 0.0)
                        else -> listOf("₹1,500 Cr", 1.2, 0.0)
                    }

                    val marketCapInCr = liveQuote.marketCap / 10_000_000.0
                    val formattedMarketCap =
                        "₹${String.format(Locale.US, "%.0f", marketCapInCr)} Cr"
                    val formattedDividend =
                        "${String.format(Locale.US, "%.1f", liveQuote.dividendYield)}%"

                    val dynamicReit = ReitModel(
                        id = id,
                        name = cleanName,
                        currentPrice = liveQuote.currentPrice,
                        priceChange = liveQuote.priceChange,
                        priceChangePercent = liveQuote.percentageChange,
                        openPrice = liveQuote.openPrice,
                        lastPrice = liveQuote.previousClose,
                        marketCap = formattedMarketCap,
                        dividendYield = formattedDividend,
                        high52Week = liveQuote.fiftyTwoWeekHigh,
                        low52Week = liveQuote.fiftyTwoWeekLow,
                        totalPortfolioValue = portVal as String,
                        totalHoldingsMsf = holdings as Double,
                        underDevelopmentMsf = dev as Double,
                        occupancyPercent = 89,
                        priceHistory = listOf(
                            (liveQuote.currentPrice * 0.95).toFloat(),
                            (liveQuote.currentPrice * 0.98).toFloat(),
                            liveQuote.currentPrice.toFloat()
                        ),
                        properties = emptyList(),
                        news = emptyList()
                    )
                    _uiState.value = ReitDetailState.Success(dynamicReit)
                } else {
                    _uiState.value =
                        ReitDetailState.Error("Could not fetch live market data for $id")
                }
            }.onFailure {
                _uiState.value = ReitDetailState.Error(it.message ?: "Unknown error occurred")
            }
        }
    }
}