package com.example.mahayuga.feature.navyuga.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.navyuga.domain.repository.PropertyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: PropertyRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _rawFilteredResults = MutableStateFlow<List<PropertyModel>>(emptyList())
    private val _likedPropertyIds = MutableStateFlow<Set<String>>(emptySet())
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)

    val searchResults: StateFlow<UiState<List<PropertyModel>>> = combine(
        _uiState,
        _rawFilteredResults,
        _likedPropertyIds
    ) { uiState, properties, likedIds ->
        when (uiState) {
            is UiState.Loading -> UiState.Loading
            is UiState.Failure -> UiState.Failure(uiState.message)
            else -> {
                if (properties.isEmpty() && uiState is UiState.Success) {
                    // 7. Changed error message
                    UiState.Failure("Coming Soon")
                } else if (properties.isEmpty()) {
                    UiState.Idle
                } else {
                    val mappedProperties = properties.map { property ->
                        property.copy(isLiked = likedIds.contains(property.id))
                    }
                    UiState.Success(mappedProperties)
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Idle)

    val countries = listOf("India", "USA", "UK", "UAE")
    val cities = listOf("All Cities", "Kolkata", "Bangalore", "Gurugram", "Mumbai", "Delhi")

    init {
        listenToUserLikes()
    }

    private fun listenToUserLikes() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val likedIds =
                    (snapshot.get("likedProperties") as? List<String>)?.toSet() ?: emptySet()
                _likedPropertyIds.value = likedIds
            }
    }

    fun performSearch(country: String, city: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                repository.getAllProperties().collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            val allProperties = state.data

                            val filteredList = allProperties.filter { property ->
                                val isFunding = property.status.equals("Funding", ignoreCase = true)

                                val matchesCity = city == "All Cities" ||
                                        property.city.equals(city, ignoreCase = true) ||
                                        property.location.contains(city, ignoreCase = true)

                                val matchesCountry =
                                    property.country.equals(country, ignoreCase = true) ||
                                            country == "India"

                                isFunding && matchesCity && matchesCountry
                            }

                            _rawFilteredResults.value = filteredList
                            _uiState.value = UiState.Success(Unit)
                        }

                        is UiState.Failure -> {
                            _uiState.value = UiState.Failure(state.message)
                        }

                        is UiState.Loading -> {
                            _uiState.value = UiState.Loading
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Failure(e.message ?: "Search failed")
            }
        }
    }

    fun toggleLike(propertyId: String, currentLikeState: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(userId)

        if (currentLikeState) {
            userRef.update("likedProperties", FieldValue.arrayRemove(propertyId))
        } else {
            userRef.set(
                mapOf("likedProperties" to FieldValue.arrayUnion(propertyId)),
                SetOptions.merge()
            )
        }
    }
}