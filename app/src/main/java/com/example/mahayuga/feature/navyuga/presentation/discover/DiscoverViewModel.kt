// main/java/com/example/mahayuga/feature/navyuga/presentation/discover/DiscoverViewModel.kt
package com.example.mahayuga.feature.navyuga.presentation.discover

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class FlashVideo(
    val id: String,
    val title: String,
    val description: String,
    val authorName: String,
    val likesCount: Int,
    val commentsCount: Int,
    val mp4Url: String,
    val thumbnailUrl: String,
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
    val mp4Url: String,
    val thumbnailUrl: String,
    val isPlayingPreview: Boolean = false
)

data class DiscoverState(
    val isLoading: Boolean = false,
    val topBannerTitle: String = "Mastering REITs in 2026",
    val topBannerSubtitle: String = "Learn the secrets of stable dividends.",
    val topBannerUrl: String = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&q=80",
    val flashes: List<FlashVideo> = emptyList(),
    val longVideos: List<LongVideo> = emptyList(),
    val searchQuery: String = "",
    val searchSuggestions: List<String> = listOf("Top Asset Managers", "Commercial Real Estate", "SM REIT Regulations", "Dividend Strategies"),
    val searchResults: List<Any> = emptyList()
)

@HiltViewModel
class DiscoverViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverState())
    val uiState: StateFlow<DiscoverState> = _uiState.asStateFlow()

    // 100% Reliable Google-hosted HTTPS MP4 testing URLs
    private val allFlashes = listOf(
        FlashVideo(
            id = "f1",
            title = "Real Estate Investing",
            description = "The number one rule you must follow before buying your first property.",
            authorName = "Bricx Daily",
            likesCount = 1240,
            commentsCount = 89,
            mp4Url = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&q=80"
        ),
        FlashVideo(
            id = "f2",
            title = "Commercial Yields",
            description = "Which city offers better yields for commercial real estate investors right now?",
            authorName = "Property Insights",
            likesCount = 856,
            commentsCount = 42,
            mp4Url = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1554118811-1e0d58224f24?auto=format&fit=crop&q=80"
        ),
        FlashVideo(
            id = "f3",
            title = "Why REITs are Booming",
            description = "A quick dive into the regulatory changes making SM REITs the hottest asset class.",
            authorName = "Finance Guru",
            likesCount = 3200,
            commentsCount = 210,
            mp4Url = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1416331108676-a22ccb276e35?auto=format&fit=crop&q=80"
        ),
        FlashVideo(
            id = "f4",
            title = "Avoid these 3 Mistakes",
            description = "Don't lose money on your first fractional investment. Watch this.",
            authorName = "Invest Smart",
            likesCount = 150,
            commentsCount = 12,
            mp4Url = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1572025442646-866d16c84a54?auto=format&fit=crop&q=80"
        )
    )

    private val allLongVideos = listOf(
        LongVideo(
            id = "l1",
            title = "Complete Guide to Real Estate Investment Trusts (REITs)",
            authorName = "Bricx Education",
            aum = "₹5,000 Cr",
            duration = "12:45",
            views = "15K views",
            postedTime = "2 days ago",
            mp4Url = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&q=80"
        ),
        LongVideo(
            id = "l2",
            title = "Mindspace Business Parks REIT - Full Analysis",
            authorName = "Market Movers",
            aum = "₹28,000 Cr",
            duration = "45:20",
            views = "8.2K views",
            postedTime = "1 week ago",
            mp4Url = "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1552566626-52f8b828add9?auto=format&fit=crop&q=80"
        ),
        LongVideo(
            id = "l3",
            title = "Fractional Real Estate Investing in India",
            authorName = "Wealth Builders",
            aum = "N/A",
            duration = "18:30",
            views = "5K views",
            postedTime = "1 day ago",
            mp4Url = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&q=80"
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
                it.title.lowercase().contains(lowercaseQuery) || it.description.lowercase().contains(lowercaseQuery)
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