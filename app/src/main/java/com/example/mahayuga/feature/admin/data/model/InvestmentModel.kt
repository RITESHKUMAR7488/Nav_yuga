package com.example.mahayuga.feature.admin.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class InvestmentModel(
    val id: String = "",
    val userId: String = "",
    val userName: String = "", // Denormalized for display
    val propertyId: String = "",
    val propertyTitle: String = "", // Denormalized for display
    val amount: Long = 0,
    val paymentMode: String = "CASH", // "CASH", "CHEQUE", "ONLINE"
    val paymentReference: String = "", // Cheque No or Transaction ID
    @ServerTimestamp val createdAt: Date? = null
)