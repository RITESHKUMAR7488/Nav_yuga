package com.example.navyuga.feature.auth.data.model

data class UserModel(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val role: String = "user", // "user" or "admin"
    val totalInvestment: Long = 0,
    val currentValue: Long = 0,
    val isActive: Boolean = true // ⚡ Added for Block/Unblock status
)