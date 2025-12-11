package com.example.navyuga.feature.roi.presentation

data class RoiState(
    // Mode Selection
    val isBuyerMode: Boolean = true, // true = Buyer, false = Seller

    // Step Control
    val currentStep: Int = 0, // 0 = Mode Selection, 1..5 = Flow

    // Step 1: Property Info
    val propertyName: String = "",
    val propertyAddress: String = "",
    val buildingAge: String = "",
    val propertyType: String = "Retail", // Retail, Office, Warehouse
    val saleableArea: String = "", // Mandatory
    val floor: String = "",
    val carPark: String = "",

    // Step 2: Lease Info
    val tenantName: String = "",
    val periodOfOccupation: String = "", // In Years
    val rentStartDate: Long? = null,
    val lockInPeriod: String = "",
    val escalationPercent: String = "",
    val escalationYears: String = "",
    val monthlyRent: String = "", // Mandatory
    val securityDeposit: String = "",

    // Step 3: Expenses
    val propertyTaxMonthly: String = "",
    val maintenanceCost: String = "",
    val isMaintenanceByLandlord: Boolean = false, // Toggle: Landlord vs Tenant

    // Step 4: Acquisition (Buyer) OR Target ROI (Seller)
    val acquisitionCost: String = "", // Mandatory for Buyer
    val targetRoi: String = "",       // Mandatory for Seller
    val legalCharges: String = "",
    val electricityCharges: String = "",
    val dgCharges: String = "",
    val fireFightingCharges: String = "",

    // Calculated Results
    val calculatedRoi: Double = 0.0,          // Result for Buyer
    val calculatedSellingPrice: Double = 0.0, // Result for Seller (Base Price)

    val totalInvestment: Double = 0.0,
    val netAnnualIncome: Double = 0.0,
    val grossAnnualRent: Double = 0.0,
    val totalPropertyTaxAnnually: Double = 0.0,
    val registryCost: Double = 0.0, // 8%
    val gstAmount: Double = 0.0,     // 18% of Rent (Display only)
    val totalOtherCharges: Double = 0.0
)