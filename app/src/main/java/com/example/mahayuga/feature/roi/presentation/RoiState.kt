package com.example.mahayuga.feature.roi.presentation

data class RoiState(
    val isBuyerMode: Boolean = true,
    val currentStep: Int = 0,
    // Property
    val propertyName: String = "",
    val propertyAddress: String = "",
    val buildingAge: String = "",
    val propertyType: String = "Retail",
    val saleableArea: String = "",
    val floor: String = "",
    val carPark: String = "",
    // Lease
    val tenantName: String = "",
    val periodOfOccupation: String = "",
    val rentStartDate: Long? = null,
    val lockInPeriod: String = "",
    val escalationPercent: String = "",
    val escalationYears: String = "",
    val monthlyRent: String = "",
    val securityDeposit: String = "",
    // Expenses
    val propertyTaxMonthly: String = "",
    val maintenanceCost: String = "",
    val isMaintenanceByLandlord: Boolean = false,
    // Financials
    val acquisitionCost: String = "",
    val targetRoi: String = "",
    val registryInput: String = "", // ⚡ NEW: Manual Registry Input
    val legalCharges: String = "",
    val electricityCharges: String = "",
    val dgCharges: String = "",
    val fireFightingCharges: String = "",
    // Results
    val calculatedRoi: Double = 0.0,
    val calculatedSellingPrice: Double = 0.0,
    val totalInvestment: Double = 0.0,
    val netAnnualIncome: Double = 0.0,
    val grossAnnualRent: Double = 0.0,
    val totalPropertyTaxAnnually: Double = 0.0,
    val registryCost: Double = 0.0,
    val gstAmount: Double = 0.0,
    val totalOtherCharges: Double = 0.0,
    val cashFlows: List<CashFlowRow> = emptyList(),
    // Counter Offer
    val counterOfferPrice: Double? = null,
    val counterOfferRoi: Double? = null
)

data class CashFlowRow(
    val year: Int,
    val annualRent: Double,
    val expenses: Double,
    val netIncome: Double
)