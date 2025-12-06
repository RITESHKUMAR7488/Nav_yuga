package com.example.navyuga.feature.arthyuga.presentation.home

import androidx.lifecycle.ViewModel
import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.arthyuga.domain.model.PropertyModel
import com.example.navyuga.feature.arthyuga.domain.model.TenantStory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    // State for Stories
    private val _stories = MutableStateFlow<List<TenantStory>>(emptyList())
    val stories: StateFlow<List<TenantStory>> = _stories

    // State for Properties
    private val _properties = MutableStateFlow<UiState<List<PropertyModel>>>(UiState.Loading)
    val properties: StateFlow<UiState<List<PropertyModel>>> = _properties

    // State for Tabs
    private val _selectedTab = MutableStateFlow("Available")
    val selectedTab: StateFlow<String> = _selectedTab

    init {
        loadDummyData()
    }

    fun selectTab(tab: String) {
        _selectedTab.value = tab
        loadDummyData() // Reload filter based on tab
    }

    private fun loadDummyData() {
        // 1. Populate Stories
        _stories.value = listOf(
            TenantStory("Reliance", "https://upload.wikimedia.org/wikipedia/en/thumb/4/4c/Reliance_Digital_logo.svg/1200px-Reliance_Digital_logo.svg.png"),
            TenantStory("Tanishq", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/28/Tanishq_Logo.svg/2560px-Tanishq_Logo.svg.png"),
            TenantStory("Starbucks", "https://upload.wikimedia.org/wikipedia/en/thumb/d/d3/Starbucks_Corporation_Logo_2011.svg/1200px-Starbucks_Corporation_Logo_2011.svg.png"),
            TenantStory("Zudio", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Zudio_logo.jpg/800px-Zudio_logo.jpg"),
            TenantStory("HDFC", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2a/HDFC_Bank_Logo.svg/2560px-HDFC_Bank_Logo.svg.png")
        )

        // 2. Populate Properties (Simulated Filter)
        val allProperties = listOf(
            PropertyModel("1", "Reliance Hub", "Kolkata, Park Street", "₹ 5,000", 8.5, 65, "", "Available"),
            PropertyModel("2", "Tanishq Plaza", "Bangalore, Indiranagar", "₹ 10,000", 9.2, 80, "", "Available"),
            PropertyModel("3", "Starbucks Cyber Hub", "Gurugram, DLF", "₹ 15,000", 7.5, 100, "", "Funded"),
            PropertyModel("4", "Domino's Point", "Mumbai, Bandra", "₹ 5,000", 14.5, 100, "", "Exited")
        )

        // Filter based on selected tab
        val filtered = allProperties.filter { it.status == _selectedTab.value }
        _properties.value = UiState.Success(filtered)
    }
}