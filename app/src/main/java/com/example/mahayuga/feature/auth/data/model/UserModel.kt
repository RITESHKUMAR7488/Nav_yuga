package com.example.mahayuga.feature.auth.data.model

import com.google.firebase.firestore.PropertyName

data class UserModel(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val dob: String = "",
    val role: String = "user",

    // Financial Overview
    val totalInvestment: Long = 0,
    val currentValue: Long = 0,

    // ⚡ ADDED: Persist specific portfolio details here
    val totalArea: Double = 0.0, // e.g., 50.5 Sqft owned
    val totalRent: Long = 0,     // e.g., ₹5000/month income

    val isActive: Boolean = true,

    @get:PropertyName("isApproved")
    val isApproved: Boolean = false,

    val likedProperties: List<String> = emptyList(),
    val investedProperties: List<String> = emptyList()
)