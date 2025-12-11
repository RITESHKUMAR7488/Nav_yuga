package com.example.navyuga.feature.arthyuga.domain.model

// ⚡ TenantStory is simple, no changes needed usually
data class TenantStory(
    val name: String = "",
    val logoUrl: String = ""
)

// ⚡ PropertyModel - Fixed: Added 'location' back
data class PropertyModel(
    val id: String = "",
    val title: String = "",
    val location: String = "", // <--- ADDED THIS BACK to fix the error
    val minInvest: String = "", // Minimum ticket size
    val roi: Double = 0.0,
    val fundedPercent: Int = 0,
    val imageUrls: List<String> = emptyList(),
    val status: String = "Available",

    // --- NEW FIELDS ADDED FOR DETAIL SCREEN ---
    val description: String = "",
    val totalValuation: String = "", // The "Total Investment" price of the property
    val rentReturn: String = "",     // e.g. "₹50,000 / month"
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "India",
    val isLiked: Boolean = false
) {
    // Helper to get a safe single image for the detail header
    val mainImage: String
        get() = if (imageUrls.isNotEmpty()) imageUrls[0] else ""

    // Helper to get a full location string
    val fullLocation: String
        get() = if (address.isNotEmpty()) "$address, $city, $state" else location
}