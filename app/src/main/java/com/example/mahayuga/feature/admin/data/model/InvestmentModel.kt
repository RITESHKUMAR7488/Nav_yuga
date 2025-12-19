package com.example.mahayuga.feature.admin.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class InvestmentModel(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val propertyId: String = "",
    val propertyTitle: String = "",
    // ⚡ NEW: Store Asset ID here for easy display
    val assetId: String = "",
    val amount: Long = 0,
    val paymentMode: String = "CASH", // "CASH", "CHEQUE", "ONLINE"
    val paymentReference: String = "", // Cheque No or Transaction ID
    @ServerTimestamp val createdAt: Date? = null
)