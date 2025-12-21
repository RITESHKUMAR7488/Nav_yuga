package com.example.mahayuga.feature.auth.data.model

import com.google.firebase.firestore.PropertyName

data class UserModel(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val dob: String = "",
    // ⚡ ADDED PHONE FIELD
    val phone: String = "",
    val role: String = "user",

    // Financial Overview
    val totalInvestment: Long = 0,
    val currentValue: Long = 0,

    // Portfolio details
    val totalArea: Double = 0.0,
    val totalRent: Long = 0,

    val isActive: Boolean = true,

    @get:PropertyName("isApproved")
    val isApproved: Boolean = false,

    val likedProperties: List<String> = emptyList(),
    val investedProperties: List<String> = emptyList()
)