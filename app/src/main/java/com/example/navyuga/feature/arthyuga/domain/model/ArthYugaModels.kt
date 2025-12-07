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
    val roi: Double,          // ⚡ ROI is here
    val fundedPercent: Int,
    val imageUrls: List<String>, // ⚡ Changed to List for Carousel
    val status: String = "Available"
)