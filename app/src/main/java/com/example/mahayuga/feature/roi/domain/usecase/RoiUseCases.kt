// main/java/com/example/mahayuga/feature/roi/domain/usecase/RoiUseCases.kt
package com.example.mahayuga.feature.roi.domain.usecase

import com.example.mahayuga.feature.roi.presentation.CashFlowRow
import javax.inject.Inject

data class RoiCalculationInput(
    val isBuyerMode: Boolean,
    val monthlyRent: Double,
    val propertyTaxMonthly: Double,
    val maintenanceCost: Double,
    val isMaintenanceByLandlord: Boolean,
    val legalCharges: Double,
    val electricityCharges: Double,
    val dgCharges: Double,
    val fireFightingCharges: Double,
    val registryPercent: Double,
    val acquisitionCost: Double,
    val targetRoiVal: Double
)

data class RoiCalculationResult(
    val calculatedRoi: Double = 0.0,
    val calculatedSellingPrice: Double = 0.0,
    val totalInvestment: Double = 0.0,
    val netAnnualIncome: Double = 0.0,
    val grossAnnualRent: Double = 0.0,
    val totalPropertyTaxAnnually: Double = 0.0,
    val registryCost: Double = 0.0,
    val gstAmount: Double = 0.0,
    val totalOtherCharges: Double = 0.0
)

class CalculateRoiUseCase @Inject constructor() {
    operator fun invoke(input: RoiCalculationInput): RoiCalculationResult {
        val grossAnnualRent = input.monthlyRent * 12
        val annualTax = input.propertyTaxMonthly * 12
        val annualMaint = if (input.isMaintenanceByLandlord) input.maintenanceCost * 12 else 0.0
        val netAnnualIncome = grossAnnualRent - annualTax - annualMaint

        val otherCharges = input.legalCharges + input.electricityCharges +
                input.dgCharges + input.fireFightingCharges

        if (input.isBuyerMode) {
            val registry = input.acquisitionCost * (input.registryPercent / 100)
            val totalInvestment = input.acquisitionCost + registry + otherCharges
            val roi = if (totalInvestment > 0) (netAnnualIncome / totalInvestment) * 100 else 0.0

            return RoiCalculationResult(
                calculatedRoi = roi,
                totalInvestment = totalInvestment,
                netAnnualIncome = netAnnualIncome,
                grossAnnualRent = grossAnnualRent,
                totalPropertyTaxAnnually = annualTax,
                registryCost = registry,
                gstAmount = input.monthlyRent * 0.18,
                totalOtherCharges = otherCharges
            )
        } else {
            if (input.targetRoiVal > 0) {
                val requiredTotalInvestment = netAnnualIncome / (input.targetRoiVal / 100)
                val baseSellingPrice =
                    (requiredTotalInvestment - otherCharges) / (1 + (input.registryPercent / 100))
                val registry = baseSellingPrice * (input.registryPercent / 100)

                return RoiCalculationResult(
                    calculatedSellingPrice = baseSellingPrice,
                    calculatedRoi = input.targetRoiVal,
                    totalInvestment = requiredTotalInvestment,
                    netAnnualIncome = netAnnualIncome,
                    grossAnnualRent = grossAnnualRent,
                    totalPropertyTaxAnnually = annualTax,
                    registryCost = registry,
                    gstAmount = input.monthlyRent * 0.18,
                    totalOtherCharges = otherCharges
                )
            }
            return RoiCalculationResult()
        }
    }
}

class GenerateCashFlowUseCase @Inject constructor() {
    operator fun invoke(
        years: Int,
        startRent: Double,
        escPercent: Double,
        escFreq: Int,
        annualExpenses: Double
    ): List<CashFlowRow> {
        val cashFlowList = ArrayList<CashFlowRow>()
        var currentMonthlyRent = startRent

        for (year in 1..years) {
            if (year > 1 && (year - 1) % escFreq == 0) {
                currentMonthlyRent += currentMonthlyRent * (escPercent / 100)
            }
            val annualRent = currentMonthlyRent * 12
            cashFlowList.add(
                CashFlowRow(
                    year,
                    annualRent,
                    annualExpenses,
                    annualRent - annualExpenses
                )
            )
        }
        return cashFlowList
    }
}

class CalculateCounterOfferUseCase @Inject constructor() {
    operator fun invoke(
        netAnnualIncome: Double,
        totalOtherCharges: Double,
        registryPercent: Double,
        desiredRoi: Double
    ): Double {
        if (desiredRoi <= 0) return 0.0
        return ((netAnnualIncome / (desiredRoi / 100)) - totalOtherCharges) / (1 + (registryPercent / 100))
    }
}