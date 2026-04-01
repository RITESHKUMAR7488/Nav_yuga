package com.example.mahayuga.feature.assetmanager.presentation.benchmark

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class MarketMetric(
    val name: String,
    val myValue: Double,
    val marketValue: Double,
    val unit: String
)

data class BenchmarkState(
    val rank: Int = 12, // e.g., "12th in Mumbai"
    val trustScore: Int = 88, // 0-100
    val metrics: List<MarketMetric> = emptyList()
)

@HiltViewModel
class BenchmarkingViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(BenchmarkState())
    val state = _state.asStateFlow()

    init {
        // Simulated Market Data
        _state.value = BenchmarkState(
            rank = 14,
            trustScore = 92,
            metrics = listOf(
                MarketMetric("Avg Rental Yield", 8.2, 6.5, "%"),
                MarketMetric("Occupancy Rate", 94.0, 85.0, "%"),
                MarketMetric("Rent Collection", 98.5, 92.0, "%"),
                MarketMetric("Cap Appreciation", 5.5, 6.2, "%") // Lagging metric demo
            )
        )
    }
}