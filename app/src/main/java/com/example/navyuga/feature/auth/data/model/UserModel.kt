package com.example.navyuga.feature.auth.data.model

data class UserModel(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val role: String = "user",
    val totalInvestment: Long = 0,
    val currentValue: Long = 0,
    val isActive: Boolean = true,
    // ⚡ ADDED: This matches your Firestore data now
    val likedProperties: List<String> = emptyList()
)