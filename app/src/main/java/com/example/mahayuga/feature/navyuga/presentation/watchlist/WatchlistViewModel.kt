// main/java/com/example/mahayuga/feature/navyuga/presentation/watchlist/WatchlistViewModel.kt
package com.example.mahayuga.feature.navyuga.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.feature.navyuga.domain.repository.MarketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WatchlistItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val currentPrice: Double,
    val priceChange: Double,
    val percentageChange: Double,
    val isPositive: Boolean,
    val sparklineData: List<Float>
)

sealed class WatchlistState {
    object Loading : WatchlistState()
    data class Success(val items: List<WatchlistItem>) : WatchlistState()
    data class Error(val message: String) : WatchlistState()
}

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val marketRepository: MarketRepository // ⚡ Inject real repository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WatchlistState>(WatchlistState.Loading)
    val uiState: StateFlow<WatchlistState> = _uiState.asStateFlow()

    init {
        fetchWatchlist()
    }

    private fun fetchWatchlist() {
        viewModelScope.launch {
            _uiState.value = WatchlistState.Loading

            // ⚡ Fetch Real Data
            val result = marketRepository.getLiveQuotes(listOf("PSTITANIA.BO", "EMBASSY.NS"))

            result.onSuccess { quotes ->
                val items = quotes.map { liveQuote ->
                    // Clean names
                    val cleanName = when (liveQuote.symbol) {
                        "EMBASSY.NS" -> "Embassy REIT"
                        "PSTITANIA.BO" -> "Prop Share Titania"
                        else -> liveQuote.name.split(",")[0]
                    }

                    WatchlistItem(
                        id = liveQuote.symbol,
                        name = cleanName,
                        subtitle = "Location/Manager ⭐", // ⭐ Marked Hardcoded
                        currentPrice = liveQuote.currentPrice,
                        priceChange = liveQuote.priceChange,
                        percentageChange = liveQuote.percentageChange,
                        isPositive = liveQuote.isPositive,
                        // ⭐ Hardcoded Chart Data
                        sparklineData = if (liveQuote.isPositive) {
                            listOf(10f, 15f, 13f, 20f, 25f, 22f, 30f, 35f)
                        } else {
                            listOf(35f, 30f, 32f, 25f, 20f, 22f, 15f, 10f)
                        }
                    )
                }
                _uiState.value = WatchlistState.Success(items)
            }.onFailure {
                _uiState.value = WatchlistState.Error(it.message ?: "Failed to fetch watchlist")
            }
        }
    }
}