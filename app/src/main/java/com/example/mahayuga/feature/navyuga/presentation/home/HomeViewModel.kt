package com.example.mahayuga.feature.navyuga.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.feature.navyuga.domain.model.MarketQuote
import com.example.mahayuga.feature.navyuga.domain.repository.MarketRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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
    val watchlistedSymbols: List<String> = emptyList(),
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

    private var listenerRegistration: ListenerRegistration? = null

    init {
        fetchLiveMarketData()
        listenToWatchlist()
    }

    private fun fetchLiveMarketData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // ⚡ FIXED: Added SM REITs to the Lisuns fetch list
            val symbolsToFetch =
                listOf("EMBASSY", "MINDSPACE", "NEXUS", "BIRET", "PSTITANIA", "PSPLATINA")

            marketRepository.getLiveQuotesFlow(symbolsToFetch).collect { liveQuotes ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        tickerQuotes = liveQuotes,
                        error = null
                    )
                }
            }
        }
    }

    private fun listenToWatchlist() {
        val uid = auth.currentUser?.uid ?: return
        listenerRegistration = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val watchlisted = snapshot?.get("watchlistedAssets") as? List<String> ?: emptyList()
                _uiState.update { it.copy(watchlistedSymbols = watchlisted) }
            }
    }

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
                    transaction.set(
                        userRef,
                        mapOf("watchlistedAssets" to listOf(symbol)),
                        SetOptions.merge()
                    )
                } else {
                    transaction.update(userRef, "watchlistedAssets", FieldValue.arrayUnion(symbol))
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}