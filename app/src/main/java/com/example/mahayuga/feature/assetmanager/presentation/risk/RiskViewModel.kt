package com.example.mahayuga.feature.assetmanager.presentation.risk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ComplianceItem(
    val id: String,
    val title: String, // e.g., "RERA Filing Q3"
    val assetName: String, // "Bandra North Corp"
    val status: String, // "Compliant", "Pending", "Breached"
    val dueDate: String
)

data class RiskAsset(
    val name: String,
    val riskScore: Int, // 0-100 (High is bad)
    val riskType: String // "Vacancy", "Legal", "Liquidity"
)

data class RiskState(
    val overallTrustScore: Int = 100, // 0-100 (High is good)
    val complianceHealth: Float = 0f, // % Compliant
    val criticalAlerts: Int = 0,
    val pendingItems: List<ComplianceItem> = emptyList(),
    val riskHeatmap: List<RiskAsset> = emptyList()
)

@HiltViewModel
class RiskViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(RiskState())
    val state: StateFlow<RiskState> = _state.asStateFlow()

    init {
        loadRiskProfile()
    }

    private fun loadRiskProfile() {
        viewModelScope.launch {
            // Simulated Compliance Data
            val complianceList = listOf(
                ComplianceItem("1", "Property Tax FY25", "Logistics Park I", "Pending", "Due in 5 Days"),
                ComplianceItem("2", "Fire Safety Audit", "Bandra North Corp", "Compliant", "Verified"),
                ComplianceItem("3", "RERA Annual Return", "High Street Opps", "Breached", "Overdue 2 Days"),
                ComplianceItem("4", "Insurance Renewal", "Data Centre Yield", "Compliant", "Valid till Dec")
            )

            // 1. Calculate Compliance Health
            val total = complianceList.size
            val compliantCount = complianceList.count { it.status == "Compliant" }
            val health = if (total > 0) (compliantCount.toFloat() / total) * 100f else 100f

            // 2. Trust Score Formula
            // Base 100, penalize for breaches and pending criticals
            val breaches = complianceList.count { it.status == "Breached" }
            val pending = complianceList.count { it.status == "Pending" }
            val trustScore = (100 - (breaches * 15) - (pending * 2)).coerceAtLeast(0)

            // 3. Risk Heatmap (Simulated from Ops & Finance data)
            val heatmap = listOf(
                RiskAsset("High Street Opps", 85, "Legal"), // High Risk due to RERA breach
                RiskAsset("Logistics Park I", 45, "Liquidity"),
                RiskAsset("Bandra North Corp", 10, "Stable")
            )

            _state.value = RiskState(
                overallTrustScore = trustScore,
                complianceHealth = health,
                criticalAlerts = breaches,
                pendingItems = complianceList.sortedBy { it.status == "Compliant" }, // Show problems first
                riskHeatmap = heatmap.sortedByDescending { it.riskScore }
            )
        }
    }
}