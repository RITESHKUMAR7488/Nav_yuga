// main/java/com/example/mahayuga/feature/navyuga/presentation/detail/PropertyDetailViewModel.kt
package com.example.mahayuga.feature.navyuga.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.navyuga.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PropertyDetailState {
    object Loading : PropertyDetailState()
    data class Success(val property: PropertyModel) : PropertyDetailState()
    data class Error(val message: String) : PropertyDetailState()
}

@HiltViewModel
class PropertyDetailViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<PropertyDetailState>(PropertyDetailState.Loading)
    val state: StateFlow<PropertyDetailState> = _state

    init {
        val propertyId = savedStateHandle.get<String>("propertyId") ?: "dummy_id"
        loadProperty(propertyId)
    }

    private fun loadProperty(propertyId: String) {
        // ⚡ COROUTINE LAUNCH: Explained below
        viewModelScope.launch {
            try {
                _state.value = PropertyDetailState.Loading

                val property = propertyRepository.getPropertyById(propertyId).firstOrNull()

                if (property != null) {
                    _state.value = PropertyDetailState.Success(property)
                } else {
                    val allPropertiesState = propertyRepository.getAllProperties().first()

                    if (allPropertiesState is UiState.Success && allPropertiesState.data.isNotEmpty()) {
                        _state.value = PropertyDetailState.Success(allPropertiesState.data.first())
                    } else {
                        // ⚡ DUMMY FALLBACK TRIGGERED
                        _state.value = PropertyDetailState.Success(getDummyProperty(propertyId))
                    }
                }
            } catch (e: Exception) {
                // Even on error, let's show the dummy data for UI testing purposes
                _state.value = PropertyDetailState.Success(getDummyProperty(propertyId))
            }
        }
    }

    // --- DUMMY DATA GENERATOR ---
    private fun getDummyProperty(id: String): PropertyModel {
        return PropertyModel(
            id = id,
            assetId = "NVG-001",
            title = "Premium Grade-A Tech Park",
            location = "Cyber City, Gurugram",
            address = "DLF Cyber City, Phase 2",
            city = "Gurugram",
            state = "Haryana",
            status = "Available",
            totalValuation = "50,00,00,000", // ₹50 Cr
            minInvest = "10,00,00", // ₹10 Lakhs
            roi = 12.5,
            fundedPercent = 65,
            totalUnits = "5000",
            area = "25,000",
            floor = "12th Floor",
            carPark = "50 Slots",
            tenantName = "Global Tech Solutions Inc.",
            occupationPeriod = "5",
            escalation = "15% every 3 years",
            monthlyRent = "41,66,666",
            grossAnnualRent = "5,00,00,000",
            annualPropertyTax = "10,00,000",
            description = "A premium Grade-A office space located in the heart of the IT hub. Fully furnished with modern amenities, currently leased to a multinational tech company. Excellent connectivity to metro and major highways.",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&q=80&w=1000",
                "https://images.unsplash.com/photo-1416331108676-a22ccb276e35?auto=format&fit=crop&q=80&w=1000",
                "https://images.unsplash.com/photo-1572025442646-866d16c84a54?auto=format&fit=crop&q=80&w=1000"
            ),
            assetManager = "Navyuga Assets",
            isTrending = true
        )
    }
}