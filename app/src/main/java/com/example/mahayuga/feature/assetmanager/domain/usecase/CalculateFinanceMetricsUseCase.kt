// main/java/com/example/mahayuga/feature/assetmanager/domain/usecase/CalculateFinanceMetricsUseCase.kt
package com.example.mahayuga.feature.assetmanager.domain.usecase

import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.assetmanager.presentation.finance.AssetFinanceModel
import javax.inject.Inject

data class FinanceMetricsResult(
    val totalRevenue: Double,
    val totalNdi: Double,
    val avgPortfolioYield: Double,
    val assets: List<AssetFinanceModel>
)

class CalculateFinanceMetricsUseCase @Inject constructor() {

    operator fun invoke(properties: List<PropertyModel>): FinanceMetricsResult {
        var globalRevenue = 0.0
        var globalNdi = 0.0
        var yieldAccumulator = 0.0

        val financeModels = properties.map { prop ->
            // 1. Parse Raw Data
            val value = parseCurrency(prop.totalValuation)
            val rentMonthly = parseDouble(prop.monthlyRent)
            val taxAnnual = parseDouble(prop.annualPropertyTax)

            // 2. Apply BRICX Formulas
            // Expenses = (Tax / 12) + (Maintenance 10% Estimate)
            val monthlyTax = taxAnnual / 12
            val maintEstimate = rentMonthly * 0.10
            val totalExpenses = monthlyTax + maintEstimate

            // Reserves (5% of Gross Rent for Capital Reserve)
            val reserves = rentMonthly * 0.05

            // Formula: NDI = Gross - Expenses - Reserves
            val ndi = (rentMonthly - totalExpenses - reserves).coerceAtLeast(0.0)

            // Formula: Yield = (NDI * 12) / Value * 100
            val annualizedNdi = ndi * 12
            val yield = if (value > 0) (annualizedNdi / value) * 100 else 0.0

            // Aggregation
            globalRevenue += rentMonthly
            globalNdi += ndi
            yieldAccumulator += yield

            AssetFinanceModel(
                property = prop,
                grossRent = rentMonthly,
                expenses = totalExpenses,
                reserves = reserves,
                ndi = ndi,
                yield = yield
            )
        }

        val avgYield = if (properties.isNotEmpty()) yieldAccumulator / properties.size else 0.0

        return FinanceMetricsResult(
            totalRevenue = globalRevenue,
            totalNdi = globalNdi,
            avgPortfolioYield = avgYield,
            assets = financeModels
        )
    }

    private fun parseCurrency(value: String): Double {
        val clean = value.replace("₹", "").replace(",", "").trim().lowercase()
        return when {
            clean.contains("cr") -> clean.replace("cr", "").toDoubleOrNull()?.times(10000000) ?: 0.0
            clean.contains("l") -> clean.replace("lakhs", "").replace("lakh", "").replace("l", "")
                .toDoubleOrNull()?.times(100000) ?: 0.0
            else -> clean.toDoubleOrNull() ?: 0.0
        }
    }

    private fun parseDouble(value: String): Double {
        return value.replace(",", "").replace("₹", "").trim().toDoubleOrNull() ?: 0.0
    }
}