package com.example.mahayuga.feature.roi.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RoiViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(RoiState())
    val uiState: StateFlow<RoiState> = _uiState.asStateFlow()

    fun selectMode(isBuyer: Boolean) {
        _uiState.update { it.copy(isBuyerMode = isBuyer, currentStep = 1) }
    }

    fun updatePropertyInfo(name: String? = null, address: String? = null, age: String? = null, type: String? = null, area: String? = null, floor: String? = null, carPark: String? = null) {
        _uiState.update { it.copy(propertyName = name ?: it.propertyName, propertyAddress = address ?: it.propertyAddress, buildingAge = age ?: it.buildingAge, propertyType = type ?: it.propertyType, saleableArea = area ?: it.saleableArea, floor = floor ?: it.floor, carPark = carPark ?: it.carPark)}
    }

    fun updateLeaseInfo(tenant: String? = null, occupation: String? = null, startDate: Long? = null, lockIn: String? = null, escPercent: String? = null, escYears: String? = null, rent: String? = null, deposit: String? = null) {
        _uiState.update { it.copy(tenantName = tenant ?: it.tenantName, periodOfOccupation = occupation ?: it.periodOfOccupation, rentStartDate = startDate ?: it.rentStartDate, lockInPeriod = lockIn ?: it.lockInPeriod, escalationPercent = escPercent ?: it.escalationPercent, escalationYears = escYears ?: it.escalationYears, monthlyRent = rent ?: it.monthlyRent, securityDeposit = deposit ?: it.securityDeposit)}
    }

    fun updateExpenses(tax: String? = null, maint: String? = null, byLandlord: Boolean? = null) {
        _uiState.update { it.copy(propertyTaxMonthly = tax ?: it.propertyTaxMonthly, maintenanceCost = maint ?: it.maintenanceCost, isMaintenanceByLandlord = byLandlord ?: it.isMaintenanceByLandlord)}
    }

    fun updateFinancials(cost: String? = null, targetRoi: String? = null, legal: String? = null, elec: String? = null, dg: String? = null, fire: String? = null) {
        _uiState.update { it.copy(acquisitionCost = cost ?: it.acquisitionCost, targetRoi = targetRoi ?: it.targetRoi, legalCharges = legal ?: it.legalCharges, electricityCharges = elec ?: it.electricityCharges, dgCharges = dg ?: it.dgCharges, fireFightingCharges = fire ?: it.fireFightingCharges)}
    }

    fun nextStep() {
        val currentState = _uiState.value
        if (canProceed(currentState)) {
            if (currentState.currentStep == 4) calculateResults()
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
        }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 0) _uiState.update { it.copy(currentStep = it.currentStep - 1) }
    }

    fun canProceed(state: RoiState): Boolean {
        return when (state.currentStep) {
            0 -> true
            1 -> state.saleableArea.isNotBlank()
            2 -> state.monthlyRent.isNotBlank() && state.periodOfOccupation.isNotBlank()
            3 -> state.propertyTaxMonthly.isNotBlank() && state.maintenanceCost.isNotBlank()
            4 -> if (state.isBuyerMode) state.acquisitionCost.isNotBlank() else state.targetRoi.isNotBlank()
            else -> false
        }
    }

    private fun calculateResults() {
        val s = _uiState.value
        val monthlyRent = s.monthlyRent.toDoubleOrNull() ?: 0.0
        val grossAnnualRent = monthlyRent * 12
        val monthlyTax = s.propertyTaxMonthly.toDoubleOrNull() ?: 0.0
        val annualTax = monthlyTax * 12
        val monthlyMaint = s.maintenanceCost.toDoubleOrNull() ?: 0.0
        val annualMaint = if (s.isMaintenanceByLandlord) monthlyMaint * 12 else 0.0
        val netAnnualIncome = grossAnnualRent - annualTax - annualMaint
        val otherCharges = (s.legalCharges.toDoubleOrNull() ?: 0.0) + (s.electricityCharges.toDoubleOrNull() ?: 0.0) + (s.dgCharges.toDoubleOrNull() ?: 0.0) + (s.fireFightingCharges.toDoubleOrNull() ?: 0.0)

        if (s.isBuyerMode) {
            val acquisitionBase = s.acquisitionCost.toDoubleOrNull() ?: 0.0
            val registry = acquisitionBase * 0.08
            val totalInvestment = acquisitionBase + registry + otherCharges
            val roi = if (totalInvestment > 0) (netAnnualIncome / totalInvestment) * 100 else 0.0
            _uiState.update { it.copy(calculatedRoi = roi, totalInvestment = totalInvestment, netAnnualIncome = netAnnualIncome, grossAnnualRent = grossAnnualRent, totalPropertyTaxAnnually = annualTax, registryCost = registry, gstAmount = monthlyRent * 0.18, totalOtherCharges = otherCharges)}
        } else {
            val targetRoiVal = s.targetRoi.toDoubleOrNull() ?: 0.0
            if (targetRoiVal > 0) {
                val requiredTotalInvestment = netAnnualIncome / (targetRoiVal / 100)
                val baseSellingPrice = (requiredTotalInvestment - otherCharges) / 1.08
                val registry = baseSellingPrice * 0.08
                _uiState.update { it.copy(calculatedSellingPrice = baseSellingPrice, calculatedRoi = targetRoiVal, totalInvestment = requiredTotalInvestment, netAnnualIncome = netAnnualIncome, grossAnnualRent = grossAnnualRent, totalPropertyTaxAnnually = annualTax, registryCost = registry, gstAmount = monthlyRent * 0.18, totalOtherCharges = otherCharges)}
            }
        }
    }

    fun generateCashFlow() {
        val s = _uiState.value
        val years = s.periodOfOccupation.toIntOrNull() ?: 10
        val startRent = s.monthlyRent.toDoubleOrNull() ?: 0.0
        val escPercent = s.escalationPercent.toDoubleOrNull() ?: 0.0
        val escFreq = s.escalationYears.toIntOrNull() ?: 100
        val expenses = (s.propertyTaxMonthly.toDoubleOrNull() ?: 0.0) * 12 + if (s.isMaintenanceByLandlord) (s.maintenanceCost.toDoubleOrNull() ?: 0.0) * 12 else 0.0

        val cashFlowList = ArrayList<CashFlowRow>()
        var currentMonthlyRent = startRent

        for (year in 1..years) {
            if (year > 1 && (year - 1) % escFreq == 0) currentMonthlyRent += currentMonthlyRent * (escPercent / 100)
            val annualRent = currentMonthlyRent * 12
            cashFlowList.add(CashFlowRow(year, annualRent, expenses, annualRent - expenses))
        }
        _uiState.update { it.copy(cashFlows = cashFlowList) }
    }

    fun calculateCounterOffer(desiredRoi: Double) {
        val s = _uiState.value
        if (desiredRoi <= 0) return
        val counterPrice = ((s.netAnnualIncome / (desiredRoi / 100)) - s.totalOtherCharges) / 1.08
        _uiState.update { it.copy(counterOfferPrice = counterPrice, counterOfferRoi = desiredRoi) }
    }
}