// main/java/com/example/mahayuga/feature/navyuga/presentation/discover/DiscoverViewModel.kt
package com.example.mahayuga.feature.navyuga.presentation.discover

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// DATA MODELS
data class FlashVideo(
    val id: String,
    val title: String,
    val description: String,
    val authorName: String,
    val likesCount: Int,
    val commentsCount: Int,
    val thumbnailUrl: String, // Added Thumbnail
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
)

data class LongVideo(
    val id: String,
    val title: String,
    val authorName: String,
    val aum: String,
    val duration: String,
    val views: String,
    val postedTime: String,
    val thumbnailUrl: String, // Added Thumbnail
    val isPlayingPreview: Boolean = false
)

data class DiscoverState(
    val isLoading: Boolean = false,
    val topBannerTitle: String = "Mastering REITs in 2026",
    val topBannerSubtitle: String = "Learn the secrets of stable dividends.",
    val topBannerUrl: String = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&q=80",
    val flashes: List<FlashVideo> = emptyList(),
    val longVideos: List<LongVideo> = emptyList(),

    // Search specific state
    val searchQuery: String = "",
    val searchSuggestions: List<String> = listOf(
        "Top Asset Managers",
        "Commercial Real Estate",
        "SM REIT Regulations",
        "Dividend Strategies"
    ),
    val searchResults: List<Any> = emptyList()
)

@HiltViewModel
class DiscoverViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverState())
    val uiState: StateFlow<DiscoverState> = _uiState.asStateFlow()

    // Added high-quality Unsplash real estate & business thumbnails
    private val allFlashes = listOf(
        FlashVideo(
            "f1",
            "Why SM REITs are Booming",
            "A quick dive into the regulatory changes making SM REITs the hottest asset class.",
            "Bricx Daily",
            1240,
            89,
            "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&q=80"
        ),
        FlashVideo(
            "f2",
            "Mumbai vs Bangalore CRE",
            "Which city offers better yields for commercial real estate investors right now?",
            "Property Insights",
            856,
            42,
            "https://images.unsplash.com/photo-1554118811-1e0d58224f24?auto=format&fit=crop&q=80"
        ),
        FlashVideo(
            "f3",
            "Understanding Cap Rates",
            "Stop guessing. Here is the exact formula to calculate capitalization rates.",
            "Finance Guru",
            3200,
            210,
            "https://images.unsplash.com/photo-1416331108676-a22ccb276e35?auto=format&fit=crop&q=80"
        ),
        FlashVideo(
            "f4",
            "Top 3 Mistakes to Avoid",
            "Don't lose money on your first fractional investment. Watch this.",
            "Invest Smart",
            150,
            12,
            "https://images.unsplash.com/photo-1572025442646-866d16c84a54?auto=format&fit=crop&q=80"
        ),
        FlashVideo(
            "f5",
            "Nexus Select Trust Review",
            "A 60-second breakdown of India's first retail REIT.",
            "REIT Tracker",
            940,
            65,
            "https://images.unsplash.com/photo-1560518883-ce09059eeffa?auto=format&fit=crop&q=80"
        )
    )

    private val allLongVideos = listOf(
        LongVideo(
            "l1",
            "Complete Guide to Fractional Real Estate",
            "Bricx Education",
            "₹5,000 Cr",
            "12:45",
            "15K views",
            "2 days ago",
            "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&q=80"
        ),
        LongVideo(
            "l2",
            "Interview with Mindspace REIT CEO",
            "Market Movers",
            "₹28,000 Cr",
            "45:20",
            "8.2K views",
            "1 week ago",
            "https://images.unsplash.com/photo-1552566626-52f8b828add9?auto=format&fit=crop&q=80"
        ),
        LongVideo(
            "l3",
            "Tax Implications of REIT Dividends",
            "Tax Simplified",
            "N/A",
            "08:15",
            "22K views",
            "3 weeks ago",
            "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&q=80"
        ),
        LongVideo(
            "l4",
            "Building a Passive Income Portfolio",
            "Wealth Builders",
            "N/A",
            "18:30",
            "5K views",
            "1 day ago",
            "https://images.unsplash.com/photo-1600880292203-757bb62b4baf?auto=format&fit=crop&q=80"
        )
    )

    init {
        _uiState.value = _uiState.value.copy(
            flashes = allFlashes,
            longVideos = allLongVideos
        )
    }

    fun toggleFlashLike(flashId: String) {
        val currentFlashes = _uiState.value.flashes.toMutableList()
        val index = currentFlashes.indexOfFirst { it.id == flashId }
        if (index != -1) {
            val flash = currentFlashes[index]
            currentFlashes[index] = flash.copy(
                isLiked = !flash.isLiked,
                likesCount = if (flash.isLiked) flash.likesCount - 1 else flash.likesCount + 1
            )
            _uiState.value = _uiState.value.copy(flashes = currentFlashes)
            updateSearchResultsIfNeeded()
        }
    }

    fun toggleFlashSave(flashId: String) {
        val currentFlashes = _uiState.value.flashes.toMutableList()
        val index = currentFlashes.indexOfFirst { it.id == flashId }
        if (index != -1) {
            val flash = currentFlashes[index]
            currentFlashes[index] = flash.copy(isSaved = !flash.isSaved)
            _uiState.value = _uiState.value.copy(flashes = currentFlashes)
        }
    }

    fun onSearchQueryChange(query: String) {
        val lowercaseQuery = query.lowercase()
        val results = mutableListOf<Any>()

        if (lowercaseQuery.isNotBlank()) {
            results.addAll(_uiState.value.flashes.filter {
                it.title.lowercase().contains(lowercaseQuery) || it.description.lowercase()
                    .contains(lowercaseQuery)
            })
            results.addAll(_uiState.value.longVideos.filter {
                it.title.lowercase().contains(lowercaseQuery)
            })
        }

        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            searchResults = results
        )
    }

    private fun updateSearchResultsIfNeeded() {
        if (_uiState.value.searchQuery.isNotBlank()) {
            onSearchQueryChange(_uiState.value.searchQuery)
        }
    }
}