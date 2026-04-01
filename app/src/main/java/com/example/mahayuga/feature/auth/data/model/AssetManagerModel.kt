package com.example.mahayuga.feature.auth.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class AssetManagerModel(
    val uid: String = "",
    val email: String = "", // Work email (Account)

    // --- B. Primary Contact ---
    val contactName: String = "",
    val designation: String = "",
    val department: String = "", // Added
    val mobile: String = "",
    val whatsappNumber: String = "", // Added
    val preferredCommunication: String = "Email", // Email, Call, WhatsApp

    // --- C. Entity Basics ---
    val entityLegalName: String = "",
    val brandName: String = "", // Added
    val entityType: String = "", // LLP, Pvt Ltd, etc.
    val country: String = "India",
    val website: String = "", // Added
    val yearsInOperation: String = "", // Added

    // Addresses
    val registeredAddress: AddressModel = AddressModel(),
    val isOperatingSameAsRegistered: Boolean = true, // Added Checkbox logic
    val operatingAddress: AddressModel = AddressModel(),

    // Portfolio Scope
    val aumRange: String = "",
    val primaryAssetClasses: List<String> = emptyList(), // Commercial, Residential, etc.
    val operatingCities: List<String> = emptyList(), // Multi-select

    // --- D. Business Identifiers ---
    val pan: String = "",
    val gstin: String = "", // Added
    val cinOrLlpin: String = "", // Added
    val sebiRegistrationNo: String = "",

    // --- E. Bank Details ---
    val bankAccount: BankDetailsModel = BankDetailsModel(),

    // --- F. Consent + Declarations ---
    val isAuthorizedRepresentative: Boolean = false,
    val hasAgreedToTerms: Boolean = false,
    val hasConsentedToDigitalKyc: Boolean = false, // Added
    val hasNoSanctionsDeclared: Boolean = false,   // Added

    val accountStatus: String = "PENDING",
    @ServerTimestamp val createdAt: Date? = null
)

data class AddressModel(
    val line1: String = "",
    val line2: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = ""
)

data class BankDetailsModel(
    val accountName: String = "",
    val accountNumber: String = "",
    val ifsc: String = "",
    val bankName: String = "",
    val branchName: String = "", // Added
    val cancelledChequeUrl: String = ""
)