package com.example.navyuga.feature.arthyuga.domain.model

data class TenantStory(
    val name: String,
    val logoUrl: String
)

data class PropertyModel(
    val id: String,
    val title: String,
    val location: String,
    val minInvest: String,
    val roi: Double,
    val fundedPercent: Int,
    val imageUrl: String,
    val status: String = "Available" // Available, Funded, Exited
)