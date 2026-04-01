package com.example.mahayuga.feature.assetmanager.presentation.fundraising

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class ActiveFundModel(
    val name: String,
    val targetAmount: Double,
    val raisedAmount: Double,
    val hardCommitment: Double,
    val softCommitment: Double,
    val daysLeft: Int,
    val minTicket: Double
)

data class LiquidityRequest(
    val investorName: String,
    val assetName: String,
    val amount: Double,
    val urgency: String, // High, Medium, Low
    val requestDate: String
)

data class FundraisingState(
    val activeFund: ActiveFundModel? = null,
    val liquidityQueue: List<LiquidityRequest> = emptyList(),
    val totalLiquidityDemand: Double = 0.0,
    val marketSentimentScore: Int = 0 // 0-100 (Bearish to Bullish)
)

@HiltViewModel
class FundraisingViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(FundraisingState())
    val state: StateFlow<FundraisingState> = _state.asStateFlow()

    init {
        loadFundraisingData()
    }

    private fun loadFundraisingData() {
        viewModelScope.launch {
            // Simulated Active Round (Pre-Series A / Fund II)
            val fund = ActiveFundModel(
                name = "Navyuga High Street Fund II",
                targetAmount = 500000000.0, // 50 Cr
                raisedAmount = 320000000.0, // 32 Cr
                hardCommitment = 280000000.0,
                softCommitment = 40000000.0,
                daysLeft = 45,
                minTicket = 2500000.0
            )

            // Simulated Liquidity Requests (Secondary Market)
            val requests = listOf(
                LiquidityRequest("Arjun V.", "Bandra North Corp", 5000000.0, "High", "2 days ago"),
                LiquidityRequest("Meera K.", "Logistics Park I", 12000000.0, "Medium", "5 days ago"),
                LiquidityRequest("Rahul S.", "Data Centre Yield", 2500000.0, "Low", "1 week ago")
            )

            val totalDemand = requests.sumOf { it.amount }

            _state.value = FundraisingState(
                activeFund = fund,
                liquidityQueue = requests,
                totalLiquidityDemand = totalDemand,
                marketSentimentScore = 78 // "Greed" phase
            )
        }
    }
}