// main/java/com/example/mahayuga/feature/navyuga/presentation/search/SearchViewModel.kt
package com.example.mahayuga.feature.navyuga.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.feature.navyuga.domain.model.MarketQuote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Using a predefined list of API Assets (REITs & SM REITs) as requested
    private val _apiAssets = MutableStateFlow<List<MarketQuote>>(emptyList())

    val searchResults: StateFlow<List<MarketQuote>> = combine(
        _searchQuery,
        _apiAssets
    ) { query, assets ->
        if (query.isBlank()) {
            emptyList()
        } else {
            assets.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.symbol.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadApiAssets()
    }

    private fun loadApiAssets() {
        // COROUTINE USAGE: Launching in viewModelScope to load API data on a background thread.
        viewModelScope.launch {
            _isLoading.value = true
            delay(500) // Simulating API latency

            // Mocking the API Ticker Quotes used in HomeScreen
            _apiAssets.value = listOf(
                MarketQuote("EMBASSY", "Embassy Office Parks REIT", 380.50, 2.5, 0.6, true),
                MarketQuote("MINDSPACE", "Mindspace Business Parks REIT", 320.10, -1.2, -0.3, false),
                MarketQuote("NEXUS", "Nexus Select Trust", 135.40, 1.1, 0.8, true),
                MarketQuote("BIRET", "Brookfield India Real Estate Trust", 255.00, 0.5, 0.2, true),
                MarketQuote("PSTITANIA", "PropShare Titania", 1000.0, 5.0, 0.5, true),
                MarketQuote("PSPLATINA", "PropShare Platina", 1050.0, -2.0, -0.1, false)
            )
            _isLoading.value = false
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}