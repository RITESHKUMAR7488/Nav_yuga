package com.example.mahayuga.feature.assetmanager.domain.model

data class AmPost(
    val id: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val description: String,
    val likesCount: Int,
    val commentsCount: Int,
    val isLikedByMe: Boolean = false
)