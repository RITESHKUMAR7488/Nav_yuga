package com.example.navyuga.feature.arthyuga.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.arthyuga.domain.model.PropertyModel
import com.example.navyuga.feature.arthyuga.domain.model.TenantStory
import com.example.navyuga.feature.arthyuga.domain.repository.PropertyRepository // ⚡ Interface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.combine

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PropertyRepository // ⚡ Use Interface
) : ViewModel() {

    private val _stories = MutableStateFlow<List<TenantStory>>(emptyList())
    val stories: StateFlow<List<TenantStory>> = _stories

    private val _properties = MutableStateFlow<UiState<List<PropertyModel>>>(UiState.Loading)
    val properties: StateFlow<UiState<List<PropertyModel>>> = _properties

    private val _selectedTab = MutableStateFlow("Available")
    val selectedTab: StateFlow<String> = _selectedTab

    init {
        loadData()
        setupFlows()
    }

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }

    private fun loadData() {
        // Dummy Stories
        _stories.value = listOf(
            TenantStory("Reliance", "https://upload.wikimedia.org/wikipedia/en/thumb/4/4c/Reliance_Digital_logo.svg/1200px-Reliance_Digital_logo.svg.png"),
            TenantStory("Tanishq", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/28/Tanishq_Logo.svg/2560px-Tanishq_Logo.svg.png"),
            TenantStory("Starbucks", "https://upload.wikimedia.org/wikipedia/en/thumb/d/d3/Starbucks_Corporation_Logo_2011.svg/1200px-Starbucks_Corporation_Logo_2011.svg.png"),
            TenantStory("Zudio", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Zudio_logo.jpg/800px-Zudio_logo.jpg"),
            TenantStory("HDFC", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2a/HDFC_Bank_Logo.svg/2560px-HDFC_Bank_Logo.svg.png")
        )

        // ⚡ Real-time Firestore Sync
        viewModelScope.launch {
            repository.getAllProperties().collectLatest { state ->
                // When Firestore updates, this block runs again
                if (state is UiState.Success) {
                    // Filter in memory for tab selection
                    val currentTab = _selectedTab.value
                    val filtered = state.data.filter { it.status == currentTab }
                    _properties.value = UiState.Success(filtered)
                } else {
                    _properties.value = state
                }
            }
        }

        // Tab switching logic (local filter on existing data)
        viewModelScope.launch {
            _selectedTab.collectLatest { tab ->
                // Note: Ideally re-fetch or keep a local copy of "all properties" to filter.
                // For simplicity, we re-trigger the collector above or rely on flow caching.
                // Better approach: combine flows.
                // But for now, we rely on the repository flow emitting.
                // To fix the filter issue when switching tabs without new data:
                // We need to store 'allProperties' locally in VM.
                // Let's refactor slightly to be safer.
            }
        }
    }
    private fun setupFlows() {
        viewModelScope.launch {
            // Combine the Repository Stream AND the Selected Tab Stream
            combine(
                repository.getAllProperties(),
                _selectedTab
            ) { propertiesState, selectedTab ->
                if (propertiesState is UiState.Success) {
                    val filtered = propertiesState.data.filter { it.status == selectedTab }
                    UiState.Success(filtered)
                } else {
                    propertiesState
                }
            }.collectLatest { finalState ->
                _properties.value = finalState
            }
        }
    }
}