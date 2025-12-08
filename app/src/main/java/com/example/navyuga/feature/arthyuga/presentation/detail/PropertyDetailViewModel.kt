package com.example.navyuga.feature.arthyuga.presentation.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navyuga.feature.arthyuga.domain.model.PropertyModel
import com.example.navyuga.feature.arthyuga.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyDetailViewModel @Inject constructor(
    private val repository: PropertyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val propertyId: String = checkNotNull(savedStateHandle["propertyId"])

    private val _property = MutableStateFlow<PropertyModel?>(null)
    val property: StateFlow<PropertyModel?> = _property

    // ⚡ Add a separate loading state to debug UI issues
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        Log.d("DetailVM", "Init with ID: $propertyId")
        loadProperty()
    }

    private fun loadProperty() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getPropertyById(propertyId).collect { fetchedProperty ->
                _property.value = fetchedProperty
                _isLoading.value = false // Stop loading whether found or null

                if (fetchedProperty == null) {
                    Log.e("DetailVM", "Property came back NULL")
                }
            }
        }
    }
}