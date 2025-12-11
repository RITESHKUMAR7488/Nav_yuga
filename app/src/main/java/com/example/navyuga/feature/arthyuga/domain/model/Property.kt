package com.example.navyuga.feature.arthyuga.domain.model

data class Property(
    val id: String,
    val title: String,
    val location: String,
    val price: String,      // Total Investment
    val rent: String,       // New: Rent per month
    val roi: String,        // New: ROI percentage
    val imageUrl: String,
    val isLiked: Boolean = false
)