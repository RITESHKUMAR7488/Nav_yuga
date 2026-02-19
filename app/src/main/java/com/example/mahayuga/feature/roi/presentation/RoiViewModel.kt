// main/java/com/example/mahayuga/feature/roi/presentation/RoiViewModel.kt
package com.example.mahayuga.feature.roi.presentation

import androidx.lifecycle.ViewModel
import com.example.mahayuga.feature.roi.domain.usecase.CalculateCounterOfferUseCase
import com.example.mahayuga.feature.roi.domain.usecase.CalculateRoiUseCase
import com.example.mahayuga.feature.roi.domain.usecase.GenerateCashFlowUseCase
import com.example.mahayuga.feature.roi.domain.usecase.RoiCalculationInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RoiViewModel @Inject constructor(
    private val calculateRoiUseCase: CalculateRoiUseCase,
    private val generateCashFlowUseCase: GenerateCashFlowUseCase,
    private val calculateCounterOfferUseCase: CalculateCounterOfferUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoiState())
    val uiState: StateFlow<RoiState> = _uiState.asStateFlow()

    fun selectMode(isBuyer: Boolean) {
        _uiState.update { it.copy(isBuyerMode = isBuyer, currentStep = 1) }
    }

    fun updatePropertyInfo(
        name: String? = null,
        address: String? = null,
        age: String? = null,
        type: String? = null,
        area: String? = null,
        floor: String? = null,
        carPark: String? = null
    ) {
        _uiState.update {
            it.copy(
                propertyName = name ?: it.propertyName,
                propertyAddress = address ?: it.propertyAddress,
                buildingAge = age ?: it.buildingAge,
                propertyType = type ?: it.propertyType,
                saleableArea = area ?: it.saleableArea,
                floor = floor ?: it.floor,
                carPark = carPark ?: it.carPark
            )
        }
    }

    fun updateLeaseInfo(
        tenant: String? = null,
        occupation: String? = null,
        startDate: Long? = null,
        lockIn: String? = null,
        escPercent: String? = null,
        escYears: String? = null,
        rent: String? = null,
        deposit: String? = null
    ) {
        _uiState.update {
            it.copy(
                tenantName = tenant ?: it.tenantName,
                periodOfOccupation = occupation ?: it.periodOfOccupation,
                rentStartDate = startDate ?: it.rentStartDate,
                lockInPeriod = lockIn ?: it.lockInPeriod,
                escalationPercent = escPercent ?: it.escalationPercent,
                escalationYears = escYears ?: it.escalationYears,
                monthlyRent = rent ?: it.monthlyRent,
                securityDeposit = deposit ?: it.securityDeposit
            )
        }
    }

    fun updateExpenses(tax: String? = null, maint: String? = null, byLandlord: Boolean? = null) {
        _uiState.update {
            it.copy(
                propertyTaxMonthly = tax ?: it.propertyTaxMonthly,
                maintenanceCost = maint ?: it.maintenanceCost,
                isMaintenanceByLandlord = byLandlord ?: it.isMaintenanceByLandlord
            )
        }
    }

    fun updateFinancials(
        cost: String? = null,
        targetRoi: String? = null,
        registry: String? = null,
        legal: String? = null,
        elec: String? = null,
        dg: String? = null,
        fire: String? = null
    ) {
        _uiState.update {
            it.copy(
                acquisitionCost = cost ?: it.acquisitionCost,
                targetRoi = targetRoi ?: it.targetRoi,
                registryInput = registry ?: it.registryInput,
                legalCharges = legal ?: it.legalCharges,
                electricityCharges = elec ?: it.electricityCharges,
                dgCharges = dg ?: it.dgCharges,
                fireFightingCharges = fire ?: it.fireFightingCharges
            )
        }
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
            4 -> {
                val baseCheck =
                    if (state.isBuyerMode) state.acquisitionCost.isNotBlank() else state.targetRoi.isNotBlank()
                baseCheck && state.registryInput.isNotBlank()
            }

            else -> false
        }
    }

    private fun calculateResults() {
        val s = _uiState.value

        // ⚡ MAP STATE TO USE CASE INPUT
        val input = RoiCalculationInput(
            isBuyerMode = s.isBuyerMode,
            monthlyRent = s.monthlyRent.toDoubleOrNull() ?: 0.0,
            propertyTaxMonthly = s.propertyTaxMonthly.toDoubleOrNull() ?: 0.0,
            maintenanceCost = s.maintenanceCost.toDoubleOrNull() ?: 0.0,
            isMaintenanceByLandlord = s.isMaintenanceByLandlord,
            legalCharges = s.legalCharges.toDoubleOrNull() ?: 0.0,
            electricityCharges = s.electricityCharges.toDoubleOrNull() ?: 0.0,
            dgCharges = s.dgCharges.toDoubleOrNull() ?: 0.0,
            fireFightingCharges = s.fireFightingCharges.toDoubleOrNull() ?: 0.0,
            registryPercent = s.registryInput.toDoubleOrNull() ?: 0.0,
            acquisitionCost = s.acquisitionCost.toDoubleOrNull() ?: 0.0,
            targetRoiVal = s.targetRoi.toDoubleOrNull() ?: 0.0
        )

        // ⚡ EXECUTE BUSINESS LOGIC
        val result = calculateRoiUseCase(input)

        // ⚡ UPDATE UI STATE
        _uiState.update {
            it.copy(
                calculatedRoi = result.calculatedRoi,
                calculatedSellingPrice = result.calculatedSellingPrice,
                totalInvestment = result.totalInvestment,
                netAnnualIncome = result.netAnnualIncome,
                grossAnnualRent = result.grossAnnualRent,
                totalPropertyTaxAnnually = result.totalPropertyTaxAnnually,
                registryCost = result.registryCost,
                gstAmount = result.gstAmount,
                totalOtherCharges = result.totalOtherCharges
            )
        }
    }

    fun generateCashFlow() {
        val s = _uiState.value
        val annualExpenses = (s.propertyTaxMonthly.toDoubleOrNull() ?: 0.0) * 12 +
                if (s.isMaintenanceByLandlord) (s.maintenanceCost.toDoubleOrNull()
                    ?: 0.0) * 12 else 0.0

        // ⚡ DELEGATE TO USE CASE
        val flow = generateCashFlowUseCase(
            years = s.periodOfOccupation.toIntOrNull() ?: 10,
            startRent = s.monthlyRent.toDoubleOrNull() ?: 0.0,
            escPercent = s.escalationPercent.toDoubleOrNull() ?: 0.0,
            escFreq = s.escalationYears.toIntOrNull() ?: 100,
            annualExpenses = annualExpenses
        )

        _uiState.update { it.copy(cashFlows = flow) }
    }

    fun calculateCounterOffer(desiredRoi: Double) {
        val s = _uiState.value

        // ⚡ DELEGATE TO USE CASE
        val counterPrice = calculateCounterOfferUseCase(
            netAnnualIncome = s.netAnnualIncome,
            totalOtherCharges = s.totalOtherCharges,
            registryPercent = s.registryInput.toDoubleOrNull() ?: 0.0,
            desiredRoi = desiredRoi
        )

        _uiState.update { it.copy(counterOfferPrice = counterPrice, counterOfferRoi = desiredRoi) }
    }
}