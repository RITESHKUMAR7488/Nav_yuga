// main/java/com/example/mahayuga/feature/navyuga/presentation/home/HomeViewModel.kt
package com.example.mahayuga.feature.navyuga.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.feature.navyuga.domain.model.MarketQuote
import com.example.mahayuga.feature.navyuga.domain.repository.MarketRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val tickerQuotes: List<MarketQuote> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val marketRepository: MarketRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _supportNumber = MutableStateFlow("919876543210")
    val supportNumber: StateFlow<String> = _supportNumber.asStateFlow()

    init {
        fetchLiveMarketData()
    }

    private fun fetchLiveMarketData() {
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

    // ⚡ NEW: Toggle Watchlist functionality
    fun toggleWatchlist(symbol: String) {
        val uid = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(uid)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val watchlisted = snapshot.get("watchlistedAssets") as? List<String> ?: emptyList()

            if (watchlisted.contains(symbol)) {
                transaction.update(userRef, "watchlistedAssets", FieldValue.arrayRemove(symbol))
            } else {
                if (!snapshot.exists()) {
                    transaction.set(userRef, mapOf("watchlistedAssets" to listOf(symbol)), SetOptions.merge())
                } else {
                    transaction.update(userRef, "watchlistedAssets", FieldValue.arrayUnion(symbol))
                }
            }
        }.addOnFailureListener { e ->
            e.printStackTrace()
        }
    }
}