package com.example.navyuga.feature.arthyuga.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.arthyuga.data.FakePropertyRepository
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

    private val _searchResults = MutableStateFlow<UiState<List<PropertyModel>>>(UiState.Success(emptyList()))
    val searchResults: StateFlow<UiState<List<PropertyModel>>> = _searchResults

    // Dropdown Data
    val countries = listOf("India", "USA", "UAE")
    val cities = listOf("All Cities", "Kolkata", "Bangalore", "Gurugram", "Mumbai")
    val currencies = listOf("INR", "USD", "AED")

    fun performSearch(country: String, city: String, currency: String) {
        viewModelScope.launch {
            _searchResults.value = UiState.Loading
            // Simulate network delay
            kotlinx.coroutines.delay(500)

            // Logic: We filter by City (since dummy data has city).
            // Country/Currency are just placeholders for the UI logic in this prototype.
            val results = repository.searchProperties("", city)

            if (results.isEmpty()) {
                _searchResults.value = UiState.Failure("No properties found in $city")
            } else {
                _searchResults.value = UiState.Success(results)
            }
        }
    }
}