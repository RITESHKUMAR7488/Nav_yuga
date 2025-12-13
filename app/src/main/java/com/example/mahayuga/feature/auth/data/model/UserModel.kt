package com.example.mahayuga.feature.auth.data.model

data class UserModel(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val role: String = "user",
    val totalInvestment: Long = 0,
    val currentValue: Long = 0,
    val isActive: Boolean = true,
    val likedProperties: List<String> = emptyList()
)