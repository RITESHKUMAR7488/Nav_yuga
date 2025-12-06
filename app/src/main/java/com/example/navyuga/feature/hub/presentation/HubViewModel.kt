package com.example.navyuga.feature.hub.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.School
import androidx.lifecycle.ViewModel
import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.hub.data.model.SuperAppModule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HubViewModel @Inject constructor() : ViewModel() {

    private val _modules = MutableStateFlow<UiState<List<SuperAppModule>>>(UiState.Loading)
    val modules: StateFlow<UiState<List<SuperAppModule>>> = _modules

    init {
        loadModules()
    }

    private fun loadModules() {
        // ⚡ Simulate loading configuration
        val appList = listOf(
            SuperAppModule(
                id = "arthyuga",
                title = "ArthYuga",
                description = "Fractional Real Estate & Fintech",
                icon = Icons.Default.Apartment, // Building Icon
                isEnabled = true
            ),
            SuperAppModule(
                id = "vidyayuga",
                title = "VidyaYuga",
                description = "Next-Gen Education System",
                icon = Icons.Default.School,
                isEnabled = false // Coming Soon
            )
        )
        _modules.value = UiState.Success(appList)
    }
}