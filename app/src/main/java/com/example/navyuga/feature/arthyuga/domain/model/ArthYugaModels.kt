package com.example.navyuga.feature.arthyuga.domain.model

data class TenantStory(
    val name: String = "",
    val logoUrl: String = ""
)

data class PropertyModel(
    val id: String = "",
    val title: String = "",
    val location: String = "",
    val status: String = "Available",

    // Financial Overview
    val totalValuation: String = "",
    val minInvest: String = "",
    val roi: Double = 0.0,
    val fundedPercent: Int = 0,

    // ⚡ THIS IS THE MISSING FIELD FIX ⚡
    val rentReturn: String = "",

    // Images
    val imageUrls: List<String> = emptyList(),

    // Property Information
    val type: String = "Office",
    val age: String = "",
    val area: String = "",
    val floor: String = "",
    val carPark: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "India",

    // Lease Information
    val tenantName: String = "",
    val occupationPeriod: String = "",
    val escalation: String = "",

    // Financial Analysis
    val monthlyRent: String = "",
    val grossAnnualRent: String = "",
    val annualPropertyTax: String = "",

    val description: String = "",
    val isLiked: Boolean = false
) {
    val mainImage: String
        get() = if (imageUrls.isNotEmpty()) imageUrls[0] else ""

    val fullLocation: String
        get() = if (address.isNotEmpty()) "$address, $city, $state" else location
}