package com.example.mahayuga.feature.profile.data.model

data class ProfileStat(
    val title: String,
    val value: String,
    val progress: Float, // 0.0f to 1.0f for the ring
    val colorHex: Long   // Color value (e.g., 0xFF00E5FF)
)

data class DocumentModel(
    val id: String,
    val title: String,
    val status: String, // "Verified", "Pending", "Rejected"
    val imageUrl: String? = null
)

// API Response Models for Image Upload
data class ImageUploadResponse(
    val status_code: Int,
    val image: ImageData
)

data class ImageData(
    val url: String,
    val display_url: String
)