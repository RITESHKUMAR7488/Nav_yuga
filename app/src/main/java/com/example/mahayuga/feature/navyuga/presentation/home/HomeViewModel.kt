package com.example.mahayuga.feature.navyuga.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.Constants
import com.example.mahayuga.core.domain.repository.SettingsRepository
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.navyuga.domain.repository.PropertyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
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
    // Standard Filters
    val selectedFilter: String = "Funding",
    val searchQuery: String = "",
    // ⚡ Advanced Filters (Multi-Select)
    val activeLocations: Set<String> = emptySet(),
    val activeBudgets: Set<String> = emptySet(),
    val activeManagers: Set<String> = emptySet(),
    val activeTypes: Set<String> = emptySet(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val supportNumber: StateFlow<String> = settingsRepository.getWhatsAppNumber()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Constants.SUPPORT_WHATSAPP_NUMBER
        )

    private var allPropertiesCache: List<PropertyModel> = emptyList()
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

    private fun updateUiWithUserData() {
        val s = _uiState.value

        // 1. Base Filter (Tab)
        var list = if (s.selectedFilter == "All") allPropertiesCache else allPropertiesCache.filter {
            it.status.equals(s.selectedFilter, ignoreCase = true)
        }

        // 2. Search Query
        if (s.searchQuery.isNotBlank()) {
            list = list.filter {
                it.title.contains(s.searchQuery, true) || it.location.contains(s.searchQuery, true)
            }
        }

        // 3. Location Filter
        if (s.activeLocations.isNotEmpty()) {
            list = list.filter { prop ->
                s.activeLocations.any { loc -> prop.city.contains(loc, true) || prop.location.contains(loc, true) }
            }
        }

        // 4. Asset Manager Filter
        if (s.activeManagers.isNotEmpty()) {
            list = list.filter { prop ->
                s.activeManagers.any { mgr -> prop.assetManager.contains(mgr, true) }
            }
        }

        // 5. Type Filter
        if (s.activeTypes.isNotEmpty()) {
            list = list.filter { prop ->
                s.activeTypes.any { type -> prop.type.equals(type, true) }
            }
        }

        // 6. Budget Filter (Price Parsing)
        if (s.activeBudgets.isNotEmpty()) {
            list = list.filter { prop ->
                val price = parsePrice(prop.totalValuation)
                s.activeBudgets.any { range -> checkBudget(price, range) }
            }
        }

        // Map User Data
        val finalProperties = list.map { property ->
            property.copy(isLiked = lastLikedIds.contains(property.id))
        }

        val stories = allPropertiesCache
            .filter { it.status != "Exited" }
            .map { prop ->
                StoryState(
                    id = prop.id,
                    imageUrl = if (prop.imageUrls.isNotEmpty()) prop.imageUrls[0] else "",
                    title = prop.title.take(10),
                    isSeen = lastSeenIds.contains(prop.id)
                )
            }.sortedBy { it.isSeen }

        _uiState.update { it.copy(isLoading = false, userName = lastUserName, properties = finalProperties, stories = stories) }
    }

    // --- Actions ---

    fun updateFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
        updateUiWithUserData()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        updateUiWithUserData()
    }

    fun toggleLocation(loc: String) {
        val current = _uiState.value.activeLocations.toMutableSet()
        if (current.contains(loc)) current.remove(loc) else current.add(loc)
        _uiState.update { it.copy(activeLocations = current) }
        updateUiWithUserData()
    }

    fun toggleManager(mgr: String) {
        val current = _uiState.value.activeManagers.toMutableSet()
        if (current.contains(mgr)) current.remove(mgr) else current.add(mgr)
        _uiState.update { it.copy(activeManagers = current) }
        updateUiWithUserData()
    }

    fun toggleType(type: String) {
        val current = _uiState.value.activeTypes.toMutableSet()
        if (current.contains(type)) current.remove(type) else current.add(type)
        _uiState.update { it.copy(activeTypes = current) }
        updateUiWithUserData()
    }

    fun toggleBudget(range: String) {
        val current = _uiState.value.activeBudgets.toMutableSet()
        if (current.contains(range)) current.remove(range) else current.add(range)
        _uiState.update { it.copy(activeBudgets = current) }
        updateUiWithUserData()
    }

    fun clearAllFilters() {
        _uiState.update {
            it.copy(
                activeLocations = emptySet(),
                activeBudgets = emptySet(),
                activeManagers = emptySet(),
                activeTypes = emptySet()
            )
        }
        updateUiWithUserData()
    }

    fun toggleLike(propertyId: String, currentLikeState: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(userId)
        if (currentLikeState) {
            userRef.update("likedProperties", com.google.firebase.firestore.FieldValue.arrayRemove(propertyId))
        } else {
            userRef.set(mapOf("likedProperties" to com.google.firebase.firestore.FieldValue.arrayUnion(propertyId)), SetOptions.merge())
        }
    }

    // --- Helpers ---

    private fun parsePrice(priceStr: String): Double {
        val clean = priceStr.replace("₹", "").replace(",", "").trim().lowercase()
        return when {
            clean.contains("cr") -> clean.replace("cr", "").trim().toDoubleOrNull()?.times(10000000) ?: 0.0
            clean.contains("lakh") -> clean.replace("lakhs", "").replace("lakh", "").trim().toDoubleOrNull()?.times(100000) ?: 0.0
            clean.contains("l") -> clean.replace("l", "").trim().toDoubleOrNull()?.times(100000) ?: 0.0
            else -> clean.toDoubleOrNull() ?: 0.0
        }
    }

    private fun checkBudget(price: Double, range: String): Boolean {
        return when (range) {
            "Upto 50L" -> price <= 5000000
            "50L - 2 Cr" -> price > 5000000 && price <= 20000000
            "Above 2 Cr" -> price > 20000000
            else -> false
        }
    }
}