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

    // COROUTINE USAGE: MutableStateFlow holds the current state.
    // It emits updates to collectors (the UI) whenever the value changes.
    private val _uiState = MutableStateFlow(RoiState())
    val uiState: StateFlow<RoiState> = _uiState.asStateFlow()

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

    fun updateAcquisition(cost: String? = null, legal: String? = null, elec: String? = null, dg: String? = null, fire: String? = null) {
        _uiState.update { it.copy(
            acquisitionCost = cost ?: it.acquisitionCost,
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
                calculateRoi()
            }
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
        }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }

    fun canProceed(state: RoiState): Boolean {
        return when (state.currentStep) {
            1 -> state.saleableArea.isNotBlank() // Only Area is mandatory
            2 -> state.monthlyRent.isNotBlank() && state.periodOfOccupation.isNotBlank()
            3 -> true // Expenses can be 0
            4 -> state.acquisitionCost.isNotBlank() // Cost is mandatory
            else -> false
        }
    }

    // --- The Core Math Logic ---

    private fun calculateRoi() {
        val s = _uiState.value

        // 1. Income
        val monthlyRent = s.monthlyRent.toDoubleOrNull() ?: 0.0
        val grossAnnualRent = monthlyRent * 12

        // 2. Outflows (Deductions)
        val monthlyTax = s.propertyTaxMonthly.toDoubleOrNull() ?: 0.0
        val annualTax = monthlyTax * 12

        val monthlyMaint = s.maintenanceCost.toDoubleOrNull() ?: 0.0
        val annualMaint = if (s.isMaintenanceByLandlord) monthlyMaint * 12 else 0.0

        val netAnnualIncome = grossAnnualRent - annualTax - annualMaint

        // 3. Investment Cost
        val acquisitionBase = s.acquisitionCost.toDoubleOrNull() ?: 0.0
        val registry = acquisitionBase * 0.08 // 8% Registry

        val legal = s.legalCharges.toDoubleOrNull() ?: 0.0
        val elec = s.electricityCharges.toDoubleOrNull() ?: 0.0
        val dg = s.dgCharges.toDoubleOrNull() ?: 0.0
        val fire = s.fireFightingCharges.toDoubleOrNull() ?: 0.0

        val totalInvestment = acquisitionBase + registry + legal + elec + dg + fire

        // 4. ROI
        val roi = if (totalInvestment > 0) (netAnnualIncome / totalInvestment) * 100 else 0.0

        // 5. GST (Pass-through display only)
        val gst = monthlyRent * 0.18

        _uiState.update { it.copy(
            calculatedRoi = roi,
            totalInvestment = totalInvestment,
            netAnnualIncome = netAnnualIncome,
            grossAnnualRent = grossAnnualRent,
            totalPropertyTaxAnnually = annualTax,
            registryCost = registry,
            gstAmount = gst
        )}
    }
}