package com.example.navyuga.feature.arthyuga.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navyuga.feature.arthyuga.domain.model.PropertyModel // Changed to PropertyModel for consistency
import com.example.navyuga.feature.arthyuga.domain.repository.PropertyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoryState(
    val id: String,
    val imageUrl: String,
    val title: String,
    val isSeen: Boolean = false
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val properties: List<PropertyModel> = emptyList(), // Changed to PropertyModel
    val stories: List<StoryState> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var allPropertiesCache: List<PropertyModel> = emptyList()

    init {
        loadRealData()
    }

    private fun loadRealData() {
        val userId = auth.currentUser?.uid ?: return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                propertyRepository.getAllProperties().collect { state ->
                    if (state is com.example.navyuga.core.common.UiState.Success) {
                        // The repository already returns List<PropertyModel>, so we use it directly.
                        // We assume PropertyModel has the necessary fields (title, location, minInvest, etc.)
                        // populated by the Repository or Data Source.
                        val fetchedProperties = state.data

                        allPropertiesCache = fetchedProperties
                        listenToUserData(userId)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // 2. Real-time Listener for User Data (Name, Likes, Seen Stories)
    private fun listenToUserData(userId: String) {
        firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener

                val name = snapshot.getString("name") ?: "User"
                val likedIds = (snapshot.get("likedProperties") as? List<String>)?.toSet() ?: emptySet()
                val seenStoryIds = (snapshot.get("seenStories") as? List<String>)?.toSet() ?: emptySet()

                updateUiWithUserData(name, likedIds, seenStoryIds)
            }
    }

    private fun updateUiWithUserData(name: String, likedIds: Set<String>, seenIds: Set<String>) {
        // 1. Update Properties with Like Status
        val updatedProperties = allPropertiesCache.map { property ->
            property.copy(isLiked = likedIds.contains(property.id))
        }

        // 2. Create and Sort Stories
        val stories = updatedProperties.map { prop ->
            StoryState(
                id = prop.id,
                // Access 'mainImage' helper if 'imageUrl' field doesn't exist directly on PropertyModel
                // or safely get the first from list
                imageUrl = if (prop.imageUrls.isNotEmpty()) prop.imageUrls[0] else "",
                title = prop.title.take(10),
                isSeen = seenIds.contains(prop.id)
            )
        }.sortedBy { it.isSeen }

        _uiState.update {
            it.copy(
                isLoading = false,
                userName = name,
                properties = updatedProperties,
                stories = stories
            )
        }
    }

    // --- Actions ---

    fun toggleLike(propertyId: String, currentLikeState: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(userId)

        if (currentLikeState) {
            userRef.update("likedProperties", com.google.firebase.firestore.FieldValue.arrayRemove(propertyId))
        } else {
            userRef.set(
                mapOf("likedProperties" to com.google.firebase.firestore.FieldValue.arrayUnion(propertyId)),
                SetOptions.merge()
            )
        }
    }

    fun markStoryAsSeen(storyId: String) {
        val userId = auth.currentUser?.uid ?: return
        val isAlreadySeen = _uiState.value.stories.find { it.id == storyId }?.isSeen == true
        if (isAlreadySeen) return

        val userRef = firestore.collection("users").document(userId)
        userRef.set(
            mapOf("seenStories" to com.google.firebase.firestore.FieldValue.arrayUnion(storyId)),
            SetOptions.merge()
        )
    }
}