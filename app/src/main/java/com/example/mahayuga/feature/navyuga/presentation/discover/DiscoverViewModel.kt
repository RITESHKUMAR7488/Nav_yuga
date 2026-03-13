// main/java/com/example/mahayuga/feature/navyuga/presentation/discover/DiscoverViewModel.kt
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

data class TrendingItem(val id: String, val name: String)
data class ReelItem(val id: String, val title: String, val views: String)
data class VideoItem(val id: String, val title: String, val channel: String, val views: String, val time: String)

sealed class DiscoverState {
    object Loading : DiscoverState()
    data class Success(
        val trending: List<TrendingItem>,
        val reels: List<ReelItem>,
        val heroVideo: VideoItem,
        val educationVideos: List<VideoItem>
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
        viewModelScope.launch {
            _uiState.value = DiscoverState.Loading
            delay(1200)

            _uiState.value = DiscoverState.Success(
                trending = listOf(
                    TrendingItem("1", "Embassy"),
                    TrendingItem("2", "Mindspace"),
                    TrendingItem("3", "Brookfield"),
                    TrendingItem("4", "Nexus"),
                    TrendingItem("5", "Bhartiya")
                ),
                reels = listOf(
                    ReelItem("1", "Top 5 REITs for 2026", "1.2M Views"),
                    ReelItem("2", "SM REITs Explained", "800K Views"),
                    ReelItem("3", "Commercial vs Residential", "450K Views"),
                    ReelItem("4", "Tax benefits of REITs", "2.1M Views"),
                    ReelItem("5", "Inside Prop Share Titania", "950K Views"),
                    ReelItem("6", "Market Crash Protection", "3.4M Views")
                ),
                heroVideo = VideoItem("hero", "Mastering Commercial Real Estate", "Navyuga Originals", "5M Views", "New Series"),
                educationVideos = listOf(
                    VideoItem("1", "How to analyze a property prospectus", "Prop Share Edu", "120K views", "2 days ago"),
                    VideoItem("2", "Understanding Dividend Yields in 2026", "Embassy REIT", "45K views", "1 week ago"),
                    VideoItem("3", "The future of co-working spaces", "WeWork India", "89K views", "3 weeks ago"),
                    VideoItem("4", "Regulatory changes in SM REITs", "SEBI Guidelines", "210K views", "1 month ago")
                )
            )
        }
    }
}