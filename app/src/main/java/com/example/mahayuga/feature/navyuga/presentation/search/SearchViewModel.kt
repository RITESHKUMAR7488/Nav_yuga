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

    // 1. Raw results from the initial API call (Country/City)
    private val _rawFilteredResults = MutableStateFlow<List<PropertyModel>>(emptyList())

    // 2. Local UI Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _activeBudgets = MutableStateFlow<Set<String>>(emptySet())
    val activeBudgets: StateFlow<Set<String>> = _activeBudgets

    private val _activeManagers = MutableStateFlow<Set<String>>(emptySet())
    val activeManagers: StateFlow<Set<String>> = _activeManagers

    private val _activeTypes = MutableStateFlow<Set<String>>(emptySet())
    val activeTypes: StateFlow<Set<String>> = _activeTypes

    private val _likedPropertyIds = MutableStateFlow<Set<String>>(emptySet())
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)

    // 3. Combined Logic: Raw Results + Local Filters
    // ⚡ FIX: Using array overload because we have >5 flows
    val searchResults: StateFlow<UiState<List<PropertyModel>>> = combine(
        _uiState,
        _rawFilteredResults,
        _likedPropertyIds,
        _searchQuery,
        _activeBudgets,
        _activeManagers,
        _activeTypes
    ) { args ->
        // Manual casting is required when combining >5 flows
        val uiState = args[0] as UiState<Unit>
        val rawProperties = args[1] as List<PropertyModel>
        val likedIds = args[2] as Set<String>
        val query = args[3] as String
        val budgets = args[4] as Set<String>
        val managers = args[5] as Set<String>
        val types = args[6] as Set<String>

        when (uiState) {
            is UiState.Loading -> UiState.Loading
            is UiState.Failure -> UiState.Failure(uiState.message)
            else -> {
                // --- APPLY FILTERS ---
                var filteredList = rawProperties

                // A. Search Query
                if (query.isNotBlank()) {
                    filteredList = filteredList.filter {
                        it.title.contains(query, ignoreCase = true) ||
                                it.location.contains(query, ignoreCase = true)
                    }
                }

                // B. Budgets
                if (budgets.isNotEmpty()) {
                    filteredList = filteredList.filter { prop ->
                        val price = parsePrice(prop.totalValuation)
                        budgets.any { range -> checkBudget(price, range) }
                    }
                }

                // C. Managers
                if (managers.isNotEmpty()) {
                    filteredList = filteredList.filter { prop ->
                        managers.any { mgr -> prop.assetManager.contains(mgr, ignoreCase = true) }
                    }
                }

                // D. Types
                if (types.isNotEmpty()) {
                    filteredList = filteredList.filter { prop ->
                        types.any { type -> prop.type.equals(type, ignoreCase = true) }
                    }
                }

                // --- FINAL MAPPING ---
                if (filteredList.isEmpty() && uiState is UiState.Success && rawProperties.isNotEmpty()) {
                    // Results existed but filters hid them (Show empty list, not "Coming Soon")
                    UiState.Success(emptyList())
                } else if (filteredList.isEmpty() && uiState is UiState.Success) {
                    // No results loaded initially
                    UiState.Failure("Coming Soon")
                } else if (filteredList.isEmpty()) {
                    UiState.Idle
                } else {
                    val mappedProperties = filteredList.map { property ->
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
                        is UiState.Failure -> _uiState.value = UiState.Failure(state.message)
                        is UiState.Loading -> _uiState.value = UiState.Loading
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Failure(e.message ?: "Search failed")
            }
        }
    }

    // --- FILTER ACTIONS ---
    fun updateSearchQuery(query: String) { _searchQuery.value = query }

    fun toggleBudget(range: String) {
        val current = _activeBudgets.value.toMutableSet()
        if (current.contains(range)) current.remove(range) else current.add(range)
        _activeBudgets.value = current
    }

    fun toggleManager(mgr: String) {
        val current = _activeManagers.value.toMutableSet()
        if (current.contains(mgr)) current.remove(mgr) else current.add(mgr)
        _activeManagers.value = current
    }

    fun toggleType(type: String) {
        val current = _activeTypes.value.toMutableSet()
        if (current.contains(type)) current.remove(type) else current.add(type)
        _activeTypes.value = current
    }

    fun clearAllFilters() {
        _activeBudgets.value = emptySet()
        _activeManagers.value = emptySet()
        _activeTypes.value = emptySet()
    }

    // --- HELPERS ---
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