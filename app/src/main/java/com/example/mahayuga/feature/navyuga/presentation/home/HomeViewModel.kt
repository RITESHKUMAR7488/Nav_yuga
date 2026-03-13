// main/java/com/example/mahayuga/feature/navyuga/presentation/home/HomeViewModel.kt
package com.example.mahayuga.feature.navyuga.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.feature.navyuga.domain.model.MarketQuote
import com.example.mahayuga.feature.navyuga.domain.repository.MarketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ⚡ FIX: Explicitly defining the state class here so the compiler knows what 'HomeUiState' and 'copy' are.
data class HomeUiState(
    val isLoading: Boolean = false,
    val tickerQuotes: List<MarketQuote> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val marketRepository: MarketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _supportNumber = MutableStateFlow("919876543210")
    val supportNumber: StateFlow<String> = _supportNumber.asStateFlow()

    init {
        fetchLiveMarketData()
    }

    private fun fetchLiveMarketData() {
        // ⚡ COROUTINE USAGE HERE ⚡
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val symbolsToFetch = listOf(
                "^NSEI", "^BSESN",
                "EMBASSY.NS", "MINDSPACE.NS", "NEXUS.NS", "BIRET.NS",
                "PSTITANIA.BO", "PSPLATINA.BO"
            )

            val result = marketRepository.getLiveQuotes(symbolsToFetch)

            result.onSuccess { liveQuotes ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        tickerQuotes = liveQuotes,
                        error = null
                    )
                }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
            }
        }
    }
}