package com.example.mahayuga.feature.assetmanager.presentation.ops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.navyuga.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

// Wrapper model to hold calculated Ops data without changing your DB schema
data class AssetOpsModel(
    val property: PropertyModel,
    val daysToVacancy: Long,
    val isHighRisk: Boolean, // < 90 Days
    val maintenanceSpendYtd: Double,
    val capexForecast: Double, // Formula: Avg * Growth Factor
    val transparencyScore: Int // 0-100
)

data class OpsState(
    val isLoading: Boolean = true,
    val assets: List<AssetOpsModel> = emptyList(),
    val totalMaintenanceSpend: Double = 0.0,
    val highRiskCount: Int = 0
)

@HiltViewModel
class AssetOpsViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OpsState())
    val state: StateFlow<OpsState> = _state.asStateFlow()

    init {
        loadOpsData()
    }

    private fun loadOpsData() {
        viewModelScope.launch {
            propertyRepository.getAllProperties().collectLatest { uiState ->
                when (uiState) {
                    is UiState.Success -> processAssets(uiState.data)
                    is UiState.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is UiState.Failure -> _state.value = _state.value.copy(isLoading = false)
                    else -> {}
                }
            }
        }
    }

    // ⚡ COROUTINE USAGE: Background thread for calculations
    private suspend fun processAssets(properties: List<PropertyModel>) {
        withContext(Dispatchers.Default) {
            val today = Calendar.getInstance()
            // Reset hours to start of day for accurate day diff
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)

            var riskCounter = 0
            var totalMaint = 0.0

            val opsAssets = properties.map { prop ->
                // 1. Simulate Lease End Date (Compatibility Fix for API < 26)
                val tenure = prop.occupationPeriod.toIntOrNull() ?: 5

                // Simulate a lease start date
                val leaseStart = Calendar.getInstance()
                leaseStart.timeInMillis = today.timeInMillis
                leaseStart.add(Calendar.YEAR, -tenure)
                leaseStart.add(Calendar.DAY_OF_YEAR, Random.nextInt(60, 400)) // Random offset

                val leaseEnd = Calendar.getInstance()
                leaseEnd.timeInMillis = leaseStart.timeInMillis
                leaseEnd.add(Calendar.YEAR, tenure)

                // Calculate Diff in Days
                val diffMillis = leaseEnd.timeInMillis - today.timeInMillis
                val daysLeft = TimeUnit.MILLISECONDS.toDays(diffMillis).coerceAtLeast(0)

                // Risk Tagging: High Risk if < 90 Days
                val isRisk = daysLeft < 90
                if (isRisk) riskCounter++

                // 2. Maintenance Logic (Simulated Vendor Invoices)
                // Formula: Maintenance Cost = Σ(Vendor.Invoice)
                val simulatedMaint = (prop.totalValuation.replace(",", "").replace("₹", "").take(3).toDoubleOrNull() ?: 10.0) * 1000
                totalMaint += simulatedMaint

                // Formula: Capex Forecast = Avg(Last 12 Months) * Growth_Factor (1.1)
                val forecast = simulatedMaint * 1.1

                AssetOpsModel(
                    property = prop,
                    daysToVacancy = daysLeft,
                    isHighRisk = isRisk,
                    maintenanceSpendYtd = simulatedMaint,
                    capexForecast = forecast,
                    transparencyScore = Random.nextInt(70, 100) // Dummy transparency
                )
            }.sortedBy { it.daysToVacancy } // Show urgent items first

            _state.value = OpsState(
                isLoading = false,
                assets = opsAssets,
                totalMaintenanceSpend = totalMaint,
                highRiskCount = riskCounter
            )
        }
    }
}