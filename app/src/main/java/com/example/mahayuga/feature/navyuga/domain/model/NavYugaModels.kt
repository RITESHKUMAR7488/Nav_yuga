package com.example.mahayuga.feature.navyuga.domain.model

data class TenantStory(
    val name: String = "",
    val logoUrl: String = ""
)

data class PropertyModel(
    val id: String = "",
    // ⚡ NEW: Auto-generated Human Readable ID
    val assetId: String = "",
    val title: String = "",
    val location: String = "",
    val status: String = "Available", // "Funding", "Funded", "Exited"

    // Financial Overview
    val totalValuation: String = "",
    val minInvest: String = "",
    val roi: Double = 0.0,
    val fundedPercent: Int = 0,
    val totalFunding: String = "0",

    // ⚡ NEW: Investment Structure Fields
    val legalWrapper: String = "SPV", // SPV, Trust, LLP, Fund
    val totalUnits: String = "",      // e.g. "5000"
    val liquidityRules: String = "",  // e.g. "3 Years Lock-in"

    val rentReturn: String = "",

    // Exited Properties
    val exitPrice: String = "",
    val totalProfit: String = "",

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