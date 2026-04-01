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
import kotlinx.coroutines.Job
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

    // Tracks the active Flow collection to prevent memory leaks
    private var quotesFetchJob: Job? = null

    init {
        fetchWatchlistData()
    }

    private fun fetchWatchlistData() {
        val uid = auth.currentUser?.uid ?: return

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

    private fun fetchLiveQuotes(savedSymbols: List<String>) {
        // MEMORY LEAK FIX: Cancel the previous collection before starting a new one
        quotesFetchJob?.cancel()

        quotesFetchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val cleanSaved = savedSymbols.map { it.replace(".NS", "").replace(".BO", "") }
            val tickerSymbols = listOf("EMBASSY", "MINDSPACE", "NEXUS", "BIRET")
            val allSymbols = (cleanSaved + tickerSymbols).distinct()

            marketRepository.getLiveQuotesFlow(allSymbols).collect { quotes ->
                val watchlisted = quotes.filter { cleanSaved.contains(it.symbol) }
                val ticker = quotes.filter { tickerSymbols.contains(it.symbol) }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        watchlistedQuotes = watchlisted,
                        tickerQuotes = ticker,
                        error = null
                    )
                }
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
        quotesFetchJob?.cancel() // Ensure cleanup when ViewModel dies
    }
}