package com.example.navyuga.feature.arthyuga.presentation.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navyuga.feature.arthyuga.domain.model.PropertyModel // Import correct model
import com.example.navyuga.feature.arthyuga.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ⚡ State now uses PropertyModel
data class PropertyDetailState(
    val isLoading: Boolean = false,
    val property: PropertyModel? = null,
    val error: String? = null
)

@HiltViewModel
class PropertyDetailViewModel @Inject constructor(
    private val repository: PropertyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Retrieve ID as String to match your Model
    private val propertyId: String = savedStateHandle.get<String>("propertyId") ?: ""

    private val _state = MutableStateFlow(PropertyDetailState())
    val state: StateFlow<PropertyDetailState> = _state.asStateFlow()

    init {
        loadProperty()
    }

    fun loadProperty() {
        if (propertyId.isEmpty()) {
            _state.update { it.copy(error = "Invalid Property ID") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Your repository returns Flow<PropertyModel?> directly in some versions,
            // or Flow<UiState> in others. Based on your uploaded interface:
            // fun getPropertyById(id: String): Flow<PropertyModel?>

            try {
                repository.getPropertyById(propertyId).collect { fetchedProperty ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            property = fetchedProperty
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}