package com.example.mahayuga.feature.navyuga.presentation.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- DATA MODELS ---
data class DiscoverStory(
    val id: String,
    val name: String,
    val imageUrl: String,
    val isBricx: Boolean = false,
    val isSeen: Boolean = false
)

data class DiscoverAd(
    val id: String,
    val imageUrl: String
)

data class DiscoverReel(
    val id: String,
    val assetManager: String,
    val aum: String,
    val subtitle: String,
    val views: String,
    val thumbnailUrl: String
)

data class DiscoverEducation(
    val id: String,
    val title: String,
    val assetManager: String,
    val aum: String,
    val views: String,
    val duration: String,
    val thumbnailUrl: String
)

sealed class DiscoverState {
    object Loading : DiscoverState()
    data class Success(
        val stories: List<DiscoverStory>,
        val adBoard: DiscoverAd,
        val reels: List<DiscoverReel>,
        val educationVideos: List<DiscoverEducation>
    ) : DiscoverState()
    data class Error(val message: String) : DiscoverState()
}

@HiltViewModel
class DiscoverViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<DiscoverState>(DiscoverState.Loading)
    val uiState: StateFlow<DiscoverState> = _uiState.asStateFlow()

    init {
        fetchDiscoverData()
    }

    private fun fetchDiscoverData() {
        // ⚡ COROUTINE USAGE: Launching safely within the ViewModel's lifecycle
        viewModelScope.launch {
            _uiState.value = DiscoverState.Loading

            // Simulating network fetch
            delay(800)

            _uiState.value = DiscoverState.Success(
                stories = listOf(
                    DiscoverStory("0", "BRICX", "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab", isBricx = true),
                    DiscoverStory("1", "Embassy", "https://images.unsplash.com/photo-1497366216548-37526070297c"),
                    DiscoverStory("2", "Mindspace", "https://images.unsplash.com/photo-1416331108676-a22ccb276e35"),
                    DiscoverStory("3", "Brookfield", "https://images.unsplash.com/photo-1572025442646-866d16c84a54"),
                    DiscoverStory("4", "Nexus", "https://images.unsplash.com/photo-1554118811-1e0d58224f24")
                ),
                adBoard = DiscoverAd(
                    id = "ad1",
                    imageUrl = "https://images.unsplash.com/photo-1560518883-ce09059eeffa?auto=format&fit=crop&q=80&w=1000"
                ),
                reels = listOf(
                    DiscoverReel("r1", "Embassy REIT", "₹48,800 Cr", "Top 5 REITs for 2026", "1.2M", "https://images.unsplash.com/photo-1554118811-1e0d58224f24"),
                    DiscoverReel("r2", "Mindspace", "₹28,000 Cr", "SM REITs Explained", "800K", "https://images.unsplash.com/photo-1497366216548-37526070297c"),
                    DiscoverReel("r3", "Brookfield", "₹12,500 Cr", "Commercial vs Residential", "450K", "https://images.unsplash.com/photo-1416331108676-a22ccb276e35"),
                    DiscoverReel("r4", "Navyuga", "₹5,000 Cr", "Tax benefits of REITs", "2.1M", "https://images.unsplash.com/photo-1572025442646-866d16c84a54")
                ),
                educationVideos = listOf(
                    DiscoverEducation("e1", "How to analyze a property prospectus like a pro", "Navyuga Assets", "₹5,000 Cr", "120K views", "14:20", "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab"),
                    DiscoverEducation("e2", "Understanding Dividend Yields and Capital Appreciation", "Embassy REIT", "₹48,800 Cr", "45K views", "08:15", "https://images.unsplash.com/photo-1560518883-ce09059eeffa"),
                    DiscoverEducation("e3", "The future of Grade-A co-working spaces in India", "Mindspace", "₹28,000 Cr", "89K views", "22:10", "https://images.unsplash.com/photo-1497366216548-37526070297c"),
                    DiscoverEducation("e4", "Regulatory changes in SM REITs you must know", "SEBI Guidelines", "N/A", "210K views", "11:45", "https://images.unsplash.com/photo-1416331108676-a22ccb276e35")
                )
            )
        }
    }
}