// main/java/com/example/mahayuga/feature/navyuga/presentation/watchlist/WatchlistViewModel.kt
package com.example.mahayuga.feature.navyuga.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.feature.navyuga.data.remote.AccelPixApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

// 1. Keep your EXACT existing data model so the UI doesn't break
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

// 2. Keep your EXACT existing state so the Compose 'when' statement works
sealed class WatchlistState {
    data object Loading : WatchlistState()
    data class Success(val items: List<WatchlistItem>) : WatchlistState()
    data class Error(val message: String) : WatchlistState()
}

// 3. Static fallback database (Moved here for simplicity, can be moved to a UseCase later)
data class StaticAsset(val id: String, val ticker: String, val cleanName: String, val subtitle: String)

val localAssets = listOf(
    StaticAsset("1", "EMBASSY", "Embassy Office Parks", "Commercial Office \u2B50"),
    StaticAsset("2", "MINDSPACE", "Mindspace Business", "IT Parks \u2B50"),
    StaticAsset("3", "BIRET", "Brookfield India", "Grade-A Office \u2B50")
)

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val accelPixApi: AccelPixApi // Injecting the Retrofit interface you created
) : ViewModel() {

    private val _uiState = MutableStateFlow<WatchlistState>(WatchlistState.Loading)
    val uiState: StateFlow<WatchlistState> = _uiState.asStateFlow()

    init {
        fetchWatchlist()
    }

    fun fetchWatchlist() {
        // Tied to viewModelScope to prevent memory leaks if user navigates away
        viewModelScope.launch {
            _uiState.value = WatchlistState.Loading

            try {
                // withContext(Dispatchers.IO) guarantees this won't freeze the UI thread
                val items = withContext(Dispatchers.IO) {
                    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")
                    val now = LocalDateTime.now()
                    val endDateString = now.format(formatter)
                    val startDateString = now.minusDays(5).format(formatter)

                    // Fire parallel GET requests for every asset in our local database
                    val deferredQuotes = localAssets.map { asset ->
                        async {
                            try {
                                val quote = accelPixApi.getLiveQuote(
                                    ticker = asset.ticker,
                                    startDate = startDateString,
                                    endDate = endDateString,
                                    interval = "1",
                                    apiToken = "%2F6FDlKysBUWiU9pZhqKcrt%2FL%2BFI%3D"
                                )
                                Pair(asset, quote) // Return a pair mapping the static data to the live data
                            } catch (e: Exception) {
                                null // Catch individual 404s so the whole list doesn't crash
                            }
                        }
                    }

                    // Wait for all parallel API calls to finish
                    val results = deferredQuotes.awaitAll().filterNotNull()

                    // Map the results back to your existing WatchlistItem UI model
                    results.map { (staticAsset, liveQuote) ->
                        val priceDiff = liveQuote.cp - liveQuote.op
                        val percentChange = if (liveQuote.op > 0) (priceDiff / liveQuote.op) * 100 else 0.0
                        val isPositive = percentChange >= 0

                        WatchlistItem(
                            id = staticAsset.ticker,
                            name = staticAsset.cleanName,
                            subtitle = staticAsset.subtitle,
                            currentPrice = liveQuote.cp,
                            priceChange = kotlin.math.abs(priceDiff),
                            percentageChange = kotlin.math.abs(percentChange),
                            isPositive = isPositive,
                            // Dummy data for sparkline until historical data is implemented
                            sparklineData = if (isPositive) listOf(10f, 15f, 13f, 20f, 25f) else listOf(25f, 20f, 22f, 15f, 10f)
                        )
                    }
                }

                _uiState.value = WatchlistState.Success(items)

            } catch (e: Exception) {
                _uiState.value = WatchlistState.Error(e.message ?: "Network error occurred")
            }
        }
    }
}