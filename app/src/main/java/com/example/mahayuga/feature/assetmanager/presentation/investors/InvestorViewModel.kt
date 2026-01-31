package com.example.mahayuga.feature.assetmanager.presentation.investors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

// --- MODELS ---
data class InvestorModel(
    val id: String,
    val name: String,
    val type: String, // Retail, HNI, Institutional
    val totalInvested: Double,
    val joinDate: String,
    val reinvestCount: Int,
    val riskScore: Int, // 0-100 (Churn Risk)
    val tags: List<String>
)

data class InvestorState(
    val isLoading: Boolean = true,
    val totalInvestors: Int = 0,
    val averageTicketSize: Double = 0.0,
    // Formula: Top 5 Holdings / Total AUM
    val whaleConcentrationPercent: Double = 0.0,
    val isWhaleRiskHigh: Boolean = false, // > 40%
    val fundraisingVelocity: List<Float> = emptyList(), // Trend data
    val investorList: List<InvestorModel> = emptyList()
)

@HiltViewModel
class InvestorViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(InvestorState())
    val state: StateFlow<InvestorState> = _state.asStateFlow()

    init {
        loadInvestorData()
    }

    private fun loadInvestorData() {
        viewModelScope.launch {
            // Simulate data fetching
            val mockInvestors = generateMockInvestors()

            // 1. Calculate AUM & Averages
            val totalCapital = mockInvestors.sumOf { it.totalInvested }
            val avgTicket = if (mockInvestors.isNotEmpty()) totalCapital / mockInvestors.size else 0.0

            // 2. Whale Risk Logic (Concentration)
            // Sort by investment size desc
            val sortedInvestors = mockInvestors.sortedByDescending { it.totalInvested }
            val top5Capital = sortedInvestors.take(5).sumOf { it.totalInvested }
            val concentration = if (totalCapital > 0) (top5Capital / totalCapital) * 100 else 0.0

            // Alert if > 40% of capital is held by top 5 people
            val isRisk = concentration > 40.0

            // 3. Fundraising Velocity (Simulated Trend)
            val velocity = listOf(10f, 15f, 12f, 25f, 30f, 28f, 45f, 60f)

            _state.value = InvestorState(
                isLoading = false,
                totalInvestors = mockInvestors.size,
                averageTicketSize = avgTicket,
                whaleConcentrationPercent = concentration,
                isWhaleRiskHigh = isRisk,
                fundraisingVelocity = velocity,
                investorList = sortedInvestors
            )
        }
    }

    private fun generateMockInvestors(): List<InvestorModel> {
        val names = listOf(
            "Aarav Sharma", "Vivaan Gupta", "Aditya Mehta", "Vihaan Patel", "Arjun Reddy",
            "Sai Kumar", "Reyansh Singh", "Krishna Das", "Ishaan Verma", "Shaurya Nair"
        )

        return names.mapIndexed { index, name ->
            // Create a "Whale" for the first 2 entries to test risk logic
            val isWhale = index < 2
            val invested = if (isWhale) Random.nextDouble(50000000.0, 150000000.0) else Random.nextDouble(1000000.0, 5000000.0)
            val type = if (invested > 10000000) "Institutional" else if (invested > 2500000) "HNI" else "Retail"

            val reinvest = Random.nextInt(0, 5)
            val tags = mutableListOf<String>()
            if (isWhale) tags.add("WHALE")
            if (reinvest > 2) tags.add("LOYAL")
            if (Random.nextBoolean()) tags.add("ACTIVE")

            InvestorModel(
                id = "INV-$index",
                name = name,
                type = type,
                totalInvested = invested,
                joinDate = "202${Random.nextInt(3,6)}-0${Random.nextInt(1,9)}",
                reinvestCount = reinvest,
                riskScore = Random.nextInt(10, 90),
                tags = tags
            )
        }
    }
}