package com.example.mahayuga.feature.navyuga.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.Constants
import com.example.mahayuga.core.domain.repository.SettingsRepository // Import this
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.navyuga.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PropertyDetailState(
    val isLoading: Boolean = false,
    val property: PropertyModel? = null,
    val error: String? = null
)

@HiltViewModel
class PropertyDetailViewModel @Inject constructor(
    private val repository: PropertyRepository,
    private val settingsRepository: SettingsRepository, // ⚡ Inject Settings Repo
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val propertyId: String = savedStateHandle.get<String>("propertyId") ?: ""

    private val _state = MutableStateFlow(PropertyDetailState())
    val state: StateFlow<PropertyDetailState> = _state.asStateFlow()

    // ⚡ REAL-TIME WHATSAPP NUMBER
    // This flow will start with the default constant, then update if Firestore changes.
    val supportNumber: StateFlow<String> = settingsRepository.getWhatsAppNumber()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Constants.SUPPORT_WHATSAPP_NUMBER
        )

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
            try {
                repository.getPropertyById(propertyId).collect { fetchedProperty ->
                    _state.update {
                        it.copy(isLoading = false, property = fetchedProperty)
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}