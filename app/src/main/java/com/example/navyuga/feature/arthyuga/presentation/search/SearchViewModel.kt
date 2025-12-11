package com.example.navyuga.feature.arthyuga.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.arthyuga.data.FakePropertyRepository
import com.example.navyuga.feature.arthyuga.domain.model.Property
import com.example.navyuga.feature.arthyuga.domain.model.PropertyModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: FakePropertyRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<UiState<List<Property>>>(UiState.Success(emptyList()))
    val searchResults: StateFlow<UiState<List<Property>>> = _searchResults

    val countries = listOf("India", "USA", "UAE")
    val cities = listOf("All Cities", "Kolkata", "Bangalore", "Gurugram", "Mumbai")
    val currencies = listOf("INR", "USD", "AED")

    fun performSearch(country: String, city: String, currency: String) {
        viewModelScope.launch {
            _searchResults.value = UiState.Loading
            kotlinx.coroutines.delay(500)

            val rawResults = repository.searchProperties("", city)

            if (rawResults.isEmpty()) {
                _searchResults.value = UiState.Failure("No properties found in $city")
            } else {
                val uiResults = rawResults.map { it.toUiModel() }
                _searchResults.value = UiState.Success(uiResults)
            }
        }
    }

    // FIXED: Updated to include rent and roi
    private fun PropertyModel.toUiModel(): Property {
        return Property(
            id = this.id,
            title = this.title,
            location = this.location,
            price = "₹${this.minInvest}",
            rent = "₹15,000", // Default/Placeholder for now
            roi = "12%",      // Default/Placeholder for now
            imageUrl = this.imageUrls.firstOrNull() ?: "",
            isLiked = false
        )
    }
}