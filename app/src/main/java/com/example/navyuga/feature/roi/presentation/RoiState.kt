package com.example.navyuga.feature.roi.presentation

data class RoiState(
    // Step Control
    val currentStep: Int = 1, // 1 to 5 (5 is result)

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

    // Step 4: Acquisition
    val acquisitionCost: String = "", // Mandatory
    val legalCharges: String = "",
    val electricityCharges: String = "",
    val dgCharges: String = "",
    val fireFightingCharges: String = "",

    // Calculated Results
    val calculatedRoi: Double = 0.0,
    val totalInvestment: Double = 0.0,
    val netAnnualIncome: Double = 0.0,
    val grossAnnualRent: Double = 0.0,
    val totalPropertyTaxAnnually: Double = 0.0,
    val registryCost: Double = 0.0, // 8% of Acquisition
    val gstAmount: Double = 0.0 // 18% of Rent (Display only)
)