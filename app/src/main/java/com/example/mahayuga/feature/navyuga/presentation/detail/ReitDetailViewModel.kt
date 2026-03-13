// main/java/com/example/mahayuga/feature/navyuga/presentation/detail/ReitDetailViewModel.kt
package com.example.mahayuga.feature.navyuga.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.feature.navyuga.domain.model.ReitModel
import com.example.mahayuga.feature.navyuga.domain.model.ReitNewsModel
import com.example.mahayuga.feature.navyuga.domain.model.ReitPropertyModel
import com.example.mahayuga.feature.navyuga.domain.repository.MarketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
        viewModelScope.launch {
            _uiState.value = ReitDetailState.Loading

            val marketResult = marketRepository.getLiveQuotes(listOf(id))

            marketResult.onSuccess { quotes ->
                val liveQuote = quotes.firstOrNull()

                if (liveQuote != null) {
                    // ⚡ FIX 1: Clean the ugly Yahoo Finance names
                    val cleanName = when(id) {
                        "MINDSPACE.NS" -> "Mindspace Business Parks"
                        "EMBASSY.NS" -> "Embassy Office Parks"
                        "NEXUS.NS" -> "Nexus Select Trust"
                        "BIRET.NS" -> "Brookfield India Trust"
                        "PSTITANIA.BO" -> "Prop Share Titania"
                        "PSPLATINA.BO" -> "Prop Share Platina"
                        else -> liveQuote.name.split(",")[0] // Fallback: drop anything after a comma
                    }

                    // ⚡ FIX 2: Inject realistic static data based on the asset
                    val (mCap, div, portVal, holdings, dev) = when(id) {
                        "MINDSPACE.NS" -> listOf("₹20,450 Cr", "5.8%", "₹28,000 Cr", 32.3, 2.8)
                        "EMBASSY.NS" -> listOf("₹33,850 Cr", "6.7%", "₹48,800 Cr", 42.6, 8.1)
                        "NEXUS.NS" -> listOf("₹20,900 Cr", "6.1%", "₹22,000 Cr", 9.8, 0.0)
                        else -> listOf("₹1,250 Cr", "7.5%", "₹1,500 Cr", 1.2, 0.0)
                    }

                    // Merge live prices with static details
                    val dynamicReit = ReitModel(
                        id = id,
                        name = cleanName,
                        currentPrice = liveQuote.currentPrice,
                        priceChange = liveQuote.priceChange,
                        priceChangePercent = liveQuote.percentageChange,
                        openPrice = liveQuote.currentPrice - liveQuote.priceChange,
                        lastPrice = liveQuote.currentPrice,
                        marketCap = mCap as String,
                        dividendYield = div as String,
                        high52Week = liveQuote.currentPrice * 1.15, // Calculated realistic high
                        low52Week = liveQuote.currentPrice * 0.85,  // Calculated realistic low
                        totalPortfolioValue = portVal as String,
                        totalHoldingsMsf = holdings as Double,
                        underDevelopmentMsf = dev as Double,
                        occupancyPercent = 89,
                        priceHistory = listOf(
                            (liveQuote.currentPrice * 0.95).toFloat(),
                            (liveQuote.currentPrice * 0.98).toFloat(),
                            liveQuote.currentPrice.toFloat()
                        ),
                        properties = emptyList(), // Can add dummy properties here if needed
                        news = emptyList()
                    )
                    _uiState.value = ReitDetailState.Success(dynamicReit)
                } else {
                    _uiState.value = ReitDetailState.Error("Could not fetch live market data for $id")
                }
            }.onFailure {
                _uiState.value = ReitDetailState.Error(it.message ?: "Unknown error occurred")
            }
        }
    }
}