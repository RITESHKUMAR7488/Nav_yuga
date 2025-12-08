package com.example.navyuga.feature.arthyuga.domain.model

// ⚡ TenantStory is simple, no changes needed usually
data class TenantStory(
    val name: String = "",
    val logoUrl: String = ""
)

// ⚡ PropertyModel needs default values for Firestore deserialization
data class PropertyModel(
    val id: String = "",
    val title: String = "",
    val location: String = "",
    val minInvest: String = "",
    val roi: Double = 0.0,
    val fundedPercent: Int = 0,
    val imageUrls: List<String> = emptyList(),
    val status: String = "Available"
)