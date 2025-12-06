package com.example.navyuga.feature.hub.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class SuperAppModule(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isEnabled: Boolean = true
)