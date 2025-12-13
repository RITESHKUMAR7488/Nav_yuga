package com.example.mahayuga.feature.navyuga.domain.model

data class Property(
    val id: String,
    val title: String,
    val imageUrl: String,
    // Financials
    val price: String,      // Total Investment
    val rent: String,       // Rent per month / Return
    val roi: String,        // Net ROI percentage
    val totalFunding: String, // Total funding done
    // Description (Nullable as requested)
    val description: String? = null,
    // Location Details
    val address: String,
    val city: String,
    val state: String,
    val country: String,
    val isLiked: Boolean = false
)