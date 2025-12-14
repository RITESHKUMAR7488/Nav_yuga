package com.example.mahayuga.feature.auth.data.model

import com.google.firebase.firestore.PropertyName

data class UserModel(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val dob: String = "", // Added DOB
    val role: String = "user",
    val totalInvestment: Long = 0,
    val currentValue: Long = 0,
    val isActive: Boolean = true,

    // ⚡ CRITICAL: Maps 'isApproved' correctly for Firestore
    @get:PropertyName("isApproved")
    val isApproved: Boolean = false,

    val likedProperties: List<String> = emptyList()
)