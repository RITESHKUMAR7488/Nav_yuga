package com.example.navyuga.feature.roi.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RoiViewModel @Inject constructor() : ViewModel() {

    // COROUTINE USAGE: MutableStateFlow is a state-holder observable flow that emits the current and new state updates to its collectors.
    // By using .update {}, we ensure atomic, thread-safe updates to the state.
    private val _uiState = MutableStateFlow(RoiState())
    val uiState: StateFlow<RoiState> = _uiState.asStateFlow()

    // --- Mode Selection ---
    fun selectMode(isBuyer: Boolean) {
        _uiState.update { it.copy(isBuyerMode = isBuyer, currentStep = 1) }
    }

    // --- Field Updaters ---

    fun updatePropertyInfo(
        name: String? = null, address: String? = null, age: String? = null,
        type: String? = null, area: String? = null, floor: String? = null, carPark: String? = null
    ) {
        _uiState.update { it.copy(
            propertyName = name ?: it.propertyName,
            propertyAddress = address ?: it.propertyAddress,
            buildingAge = age ?: it.buildingAge,
            propertyType = type ?: it.propertyType,
            saleableArea = area ?: it.saleableArea,
            floor = floor ?: it.floor,
            carPark = carPark ?: it.carPark
        )}
    }

    fun updateLeaseInfo(
        tenant: String? = null, occupation: String? = null, startDate: Long? = null,
        lockIn: String? = null, escPercent: String? = null, escYears: String? = null,
        rent: String? = null, deposit: String? = null
    ) {
        _uiState.update { it.copy(
            tenantName = tenant ?: it.tenantName,
            periodOfOccupation = occupation ?: it.periodOfOccupation,
            rentStartDate = startDate ?: it.rentStartDate,
            lockInPeriod = lockIn ?: it.lockInPeriod,
            escalationPercent = escPercent ?: it.escalationPercent,
            escalationYears = escYears ?: it.escalationYears,
            monthlyRent = rent ?: it.monthlyRent,
            securityDeposit = deposit ?: it.securityDeposit
        )}
    }

    fun updateExpenses(tax: String? = null, maint: String? = null, byLandlord: Boolean? = null) {
        _uiState.update { it.copy(
            propertyTaxMonthly = tax ?: it.propertyTaxMonthly,
            maintenanceCost = maint ?: it.maintenanceCost,
            isMaintenanceByLandlord = byLandlord ?: it.isMaintenanceByLandlord
        )}
    }

    fun updateFinancials(
        cost: String? = null, // Buyer: Acquisition Cost
        targetRoi: String? = null, // Seller: Desired ROI
        legal: String? = null, elec: String? = null, dg: String? = null, fire: String? = null
    ) {
        _uiState.update { it.copy(
            acquisitionCost = cost ?: it.acquisitionCost,
            targetRoi = targetRoi ?: it.targetRoi,
            legalCharges = legal ?: it.legalCharges,
            electricityCharges = elec ?: it.electricityCharges,
            dgCharges = dg ?: it.dgCharges,
            fireFightingCharges = fire ?: it.fireFightingCharges
        )}
    }

    // --- Navigation & Validation ---

    fun nextStep() {
        val currentState = _uiState.value
        if (canProceed(currentState)) {
            if (currentState.currentStep == 4) {
                calculateResults()
            }
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
        }
    }

    fun previousStep() {
        val current = _uiState.value.currentStep
        if (current > 0) {
            _uiState.update { it.copy(currentStep = current - 1) }
        }
    }

    fun canProceed(state: RoiState): Boolean {
        return when (state.currentStep) {
            0 -> true
            1 -> state.saleableArea.isNotBlank() // Only Area is mandatory
            2 -> state.monthlyRent.isNotBlank() && state.periodOfOccupation.isNotBlank()
            3 -> true // Expenses can be 0
            4 -> if (state.isBuyerMode) state.acquisitionCost.isNotBlank() else state.targetRoi.isNotBlank()
            else -> false
        }
    }

    // --- The Core Math Logic ---

    private fun calculateResults() {
        val s = _uiState.value

        // 1. Income (Common)
        val monthlyRent = s.monthlyRent.toDoubleOrNull() ?: 0.0
        val grossAnnualRent = monthlyRent * 12

        // 2. Outflows (Common)
        val monthlyTax = s.propertyTaxMonthly.toDoubleOrNull() ?: 0.0
        val annualTax = monthlyTax * 12

        val monthlyMaint = s.maintenanceCost.toDoubleOrNull() ?: 0.0
        val annualMaint = if (s.isMaintenanceByLandlord) monthlyMaint * 12 else 0.0

        val netAnnualIncome = grossAnnualRent - annualTax - annualMaint

        // Others (Common)
        val legal = s.legalCharges.toDoubleOrNull() ?: 0.0
        val elec = s.electricityCharges.toDoubleOrNull() ?: 0.0
        val dg = s.dgCharges.toDoubleOrNull() ?: 0.0
        val fire = s.fireFightingCharges.toDoubleOrNull() ?: 0.0
        val otherCharges = legal + elec + dg + fire

        if (s.isBuyerMode) {
            // --- BUYER LOGIC: Calculate ROI ---
            val acquisitionBase = s.acquisitionCost.toDoubleOrNull() ?: 0.0
            val registry = acquisitionBase * 0.08 // 8% Registry on Base

            val totalInvestment = acquisitionBase + registry + otherCharges

            val roi = if (totalInvestment > 0) (netAnnualIncome / totalInvestment) * 100 else 0.0

            _uiState.update { it.copy(
                calculatedRoi = roi,
                totalInvestment = totalInvestment,
                netAnnualIncome = netAnnualIncome,
                grossAnnualRent = grossAnnualRent,
                totalPropertyTaxAnnually = annualTax,
                registryCost = registry,
                gstAmount = monthlyRent * 0.18,
                totalOtherCharges = otherCharges
            )}
        } else {
            // --- SELLER LOGIC: Calculate Price ---
            val targetRoiVal = s.targetRoi.toDoubleOrNull() ?: 0.0

            if (targetRoiVal > 0) {
                // Formula: Total Investment = Net Income / (ROI / 100)
                val requiredTotalInvestment = netAnnualIncome / (targetRoiVal / 100)

                // Back calculate Base Price:
                // Total Inv = Base + 8% Base + Others
                // Total Inv - Others = 1.08 * Base
                // Base = (Total Inv - Others) / 1.08

                val baseSellingPrice = (requiredTotalInvestment - otherCharges) / 1.08
                val registry = baseSellingPrice * 0.08

                _uiState.update { it.copy(
                    calculatedSellingPrice = baseSellingPrice,
                    calculatedRoi = targetRoiVal, // Showing the target
                    totalInvestment = requiredTotalInvestment,
                    netAnnualIncome = netAnnualIncome,
                    grossAnnualRent = grossAnnualRent,
                    totalPropertyTaxAnnually = annualTax,
                    registryCost = registry,
                    gstAmount = monthlyRent * 0.18,
                    totalOtherCharges = otherCharges
                )}
            }
        }
    }
}