// main/java/com/example/mahayuga/feature/navyuga/presentation/watchlist/WatchlistViewModel.kt
package com.example.mahayuga.feature.navyuga.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.feature.navyuga.domain.model.MarketQuote
import com.example.mahayuga.feature.navyuga.domain.repository.MarketRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WatchlistState(
    val isLoading: Boolean = false,
    val watchlistedQuotes: List<MarketQuote> = emptyList(),
    val tickerQuotes: List<MarketQuote> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val marketRepository: MarketRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchlistState(isLoading = true))
    val uiState: StateFlow<WatchlistState> = _uiState.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        fetchWatchlistData()
    }

    private fun fetchWatchlistData() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _uiState.update { it.copy(isLoading = false, error = "User not logged in") }
            return
        }

        // Live stream the arrays saved by the user
        listenerRegistration = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                    return@addSnapshotListener
                }

                val symbols = snapshot?.get("watchlistedAssets") as? List<String> ?: emptyList()
                fetchLiveQuotes(symbols)
            }
    }

    private fun fetchLiveQuotes(symbols: List<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Retrieve live data based on saved list
            val quotes = if (symbols.isNotEmpty()) {
                marketRepository.getLiveQuotes(symbols).getOrNull() ?: emptyList()
            } else {
                emptyList()
            }

            // Also retrieve standard index data for the ticker animation exactly like Home
            val tickerSymbols = listOf(
                "^NSEI",
                "^BSESN",
                "EMBASSY.NS",
                "MINDSPACE.NS",
                "NEXUS.NS",
                "BIRET.NS",
                "PSTITANIA.BO",
                "PSPLATINA.BO"
            )
            val tickerQuotes =
                marketRepository.getLiveQuotes(tickerSymbols).getOrNull() ?: emptyList()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    watchlistedQuotes = quotes,
                    tickerQuotes = tickerQuotes,
                    error = null
                )
            }
        }
    }

    fun removeWatchlist(symbol: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .update("watchlistedAssets", FieldValue.arrayRemove(symbol))
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}