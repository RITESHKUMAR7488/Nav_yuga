package com.example.mahayuga.feature.assetmanager.presentation.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.assetmanager.domain.model.AmPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AmPostsViewModel @Inject constructor() : ViewModel() {

    private val _postsState = MutableStateFlow<UiState<List<AmPost>>>(UiState.Loading)
    val postsState: StateFlow<UiState<List<AmPost>>> = _postsState.asStateFlow()

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        // COROUTINE USAGE: Launching in viewModelScope to keep UI responsive
        viewModelScope.launch {
            _postsState.value = UiState.Loading

            // Simulating network latency for real-time loading feedback
            delay(1200)

            val dummyData = List(12) { index ->
                AmPost(
                    id = index.toString(),
                    thumbnailUrl = "https://picsum.photos/seed/$index/400/600",
                    videoUrl = "", // Placeholder for actual video
                    description = "Exciting new investment opportunity unlocked at Bricx! Check out the complete site tour and ROI breakdown. #RealEstate #Bricx #Invest",
                    likesCount = (100..5000).random(),
                    commentsCount = (10..500).random(),
                    isLikedByMe = index % 3 == 0
                )
            }
            _postsState.value = UiState.Success(dummyData)
        }
    }

    fun toggleLike(postId: String) {
        val currentState = _postsState.value
        if (currentState is UiState.Success) {
            val updatedList = currentState.data.map { post ->
                if (post.id == postId) {
                    val newLikeStatus = !post.isLikedByMe
                    post.copy(
                        isLikedByMe = newLikeStatus,
                        likesCount = if (newLikeStatus) post.likesCount + 1 else post.likesCount - 1
                    )
                } else {
                    post
                }
            }
            _postsState.value = UiState.Success(updatedList)
        }
    }
}