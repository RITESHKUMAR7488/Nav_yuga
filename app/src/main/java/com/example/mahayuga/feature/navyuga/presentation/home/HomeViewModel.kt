package com.example.mahayuga.feature.navyuga.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.navyuga.domain.repository.PropertyRepository
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
    val properties: List<PropertyModel> = emptyList(),
    val stories: List<StoryState> = emptyList(),
    val selectedFilter: String = "Funding", // Added Filter State
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

    // Cache user data to re-apply filters without re-fetching
    private var lastUserName: String = ""
    private var lastLikedIds: Set<String> = emptySet()
    private var lastSeenIds: Set<String> = emptySet()

    init {
        loadRealData()
    }

    private fun loadRealData() {
        val userId = auth.currentUser?.uid ?: return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                propertyRepository.getAllProperties().collect { state ->
                    if (state is com.example.mahayuga.core.common.UiState.Success) {
                        allPropertiesCache = state.data
                        listenToUserData(userId)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun listenToUserData(userId: String) {
        firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener

                lastUserName = snapshot.getString("name") ?: "User"
                lastLikedIds = (snapshot.get("likedProperties") as? List<String>)?.toSet() ?: emptySet()
                lastSeenIds = (snapshot.get("seenStories") as? List<String>)?.toSet() ?: emptySet()

                updateUiWithUserData()
            }
    }

    // Updated to use cached data and apply filter
    private fun updateUiWithUserData() {
        // 1. Filter Properties based on selected status
        val filteredProperties = if (_uiState.value.selectedFilter == "All") {
            allPropertiesCache
        } else {
            allPropertiesCache.filter { it.status.equals(_uiState.value.selectedFilter, ignoreCase = true) }
        }

        // 2. Update Properties with Like Status
        val finalProperties = filteredProperties.map { property ->
            property.copy(isLiked = lastLikedIds.contains(property.id))
        }

        // 3. Create and Sort Stories (Stories usually come from all properties or a specific set, using all here)
        val stories = allPropertiesCache.map { prop ->
            StoryState(
                id = prop.id,
                imageUrl = if (prop.imageUrls.isNotEmpty()) prop.imageUrls[0] else "",
                title = prop.title.take(10),
                isSeen = lastSeenIds.contains(prop.id)
            )
        }.sortedBy { it.isSeen }

        _uiState.update {
            it.copy(
                isLoading = false,
                userName = lastUserName,
                properties = finalProperties,
                stories = stories
            )
        }
    }

    // --- Actions ---

    fun updateFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
        updateUiWithUserData()
    }

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