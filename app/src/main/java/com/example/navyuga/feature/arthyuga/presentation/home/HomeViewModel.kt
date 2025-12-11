package com.example.navyuga.feature.arthyuga.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navyuga.feature.arthyuga.domain.model.Property
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
    val properties: List<Property> = emptyList(),
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

    private var allPropertiesCache: List<Property> = emptyList()

    init {
        loadRealData()
    }

    private fun loadRealData() {
        val userId = auth.currentUser?.uid ?: return

        _uiState.update { it.copy(isLoading = true) }

        // 1. Fetch Properties (Assuming Repo fetches from 'approved_properties')
        viewModelScope.launch {
            try {
                propertyRepository.getAllProperties().collect { state ->
                    // For now, mapping your Repo data.
                    // Ideally, your Repo should return the Data, and we merge it with User Data below.
                    // This is a simplified merge for the immediate requirement.
                    if (state is com.example.navyuga.core.common.UiState.Success) {
                        val rawProperties = state.data.map {
                            Property(
                                id = it.id,
                                title = it.title,
                                location = it.location,
                                price = "₹${it.minInvest}",
                                rent = "₹15,000", // Placeholder until you add rent to DB
                                roi = "12%",      // Placeholder until you add roi to DB
                                imageUrl = it.imageUrls.firstOrNull() ?: "",
                                isLiked = false
                            )
                        }
                        allPropertiesCache = rawProperties
                        listenToUserData(userId) // Trigger user data merge
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
                // Get Lists safely
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
        // Logic: Create stories, check if seen.
        // Sort: Unseen stories (false) come FIRST, Seen (true) come LAST.
        val stories = updatedProperties.map { prop ->
            StoryState(
                id = prop.id,
                imageUrl = prop.imageUrl,
                title = prop.title.take(10),
                isSeen = seenIds.contains(prop.id)
            )
        }.sortedBy { it.isSeen } // False (Unseen) < True (Seen)

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

        // Optimistic Update (Immediate UI change before Server)
        // (Optional but makes it feel faster. The listener will correct it if it fails)

        if (currentLikeState) {
            // If currently liked, remove it
            userRef.update("likedProperties", com.google.firebase.firestore.FieldValue.arrayRemove(propertyId))
        } else {
            // If not liked, add it
            userRef.set(
                mapOf("likedProperties" to com.google.firebase.firestore.FieldValue.arrayUnion(propertyId)),
                SetOptions.merge()
            )
        }
    }

    fun markStoryAsSeen(storyId: String) {
        val userId = auth.currentUser?.uid ?: return

        // Only update if not already seen to save writes
        val isAlreadySeen = _uiState.value.stories.find { it.id == storyId }?.isSeen == true
        if (isAlreadySeen) return

        val userRef = firestore.collection("users").document(userId)
        userRef.set(
            mapOf("seenStories" to com.google.firebase.firestore.FieldValue.arrayUnion(storyId)),
            SetOptions.merge()
        )
        // The snapshot listener will automatically update the UI and move the story to the back!
    }
}