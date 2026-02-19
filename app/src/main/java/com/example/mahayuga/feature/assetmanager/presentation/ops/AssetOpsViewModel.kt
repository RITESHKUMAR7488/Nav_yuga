// main/java/com/example/mahayuga/feature/assetmanager/presentation/ops/AssetOpsViewModel.kt
package com.example.mahayuga.feature.assetmanager.presentation.ops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.navyuga.domain.repository.PropertyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

data class AssetOpsModel(
    val property: PropertyModel,
    val daysToVacancy: Long,
    val isHighRisk: Boolean,
    val maintenanceSpendYtd: Double,
    val capexForecast: Double,
    val transparencyScore: Int
)

data class OpsState(
    val isLoading: Boolean = true,
    val assets: List<AssetOpsModel> = emptyList(),
    val totalMaintenanceSpend: Double = 0.0,
    val highRiskCount: Int = 0
)

@HiltViewModel
class AssetOpsViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableStateFlow(OpsState())
    val state: StateFlow<OpsState> = _state.asStateFlow()

    init {
        loadOpsData()
    }

    private fun loadOpsData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            var amName = ""
            try {
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    val doc = firestore.collection("asset_managers").document(uid).get().await()
                    if (doc.exists()) {
                        val brand = doc.getString("brandName")
                        val legal = doc.getString("entityLegalName")
                        val contact = doc.getString("contactName")
                        amName = when {
                            !brand.isNullOrBlank() -> brand
                            !legal.isNullOrBlank() -> legal
                            !contact.isNullOrBlank() -> contact
                            else -> "PARTNER"
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            propertyRepository.getAllProperties().collectLatest { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        val myProps = uiState.data.filter { it.assetManager.equals(amName, true) }
                        processAssets(myProps)
                    }

                    is UiState.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is UiState.Failure -> _state.value = _state.value.copy(isLoading = false)
                    else -> {}
                }
            }
        }
    }

    private suspend fun processAssets(properties: List<PropertyModel>) {
        withContext(Dispatchers.Default) {
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)

            var riskCounter = 0
            var totalMaint = 0.0

            val opsAssets = properties.map { prop ->
                val tenure = prop.occupationPeriod.toIntOrNull() ?: 5
                val leaseStart = Calendar.getInstance()
                leaseStart.timeInMillis = today.timeInMillis
                leaseStart.add(Calendar.YEAR, -tenure)
                leaseStart.add(Calendar.DAY_OF_YEAR, Random.nextInt(60, 400))

                val leaseEnd = Calendar.getInstance()
                leaseEnd.timeInMillis = leaseStart.timeInMillis
                leaseEnd.add(Calendar.YEAR, tenure)

                val diffMillis = leaseEnd.timeInMillis - today.timeInMillis
                val daysLeft = TimeUnit.MILLISECONDS.toDays(diffMillis).coerceAtLeast(0)

                val isRisk = daysLeft < 90
                if (isRisk) riskCounter++

                val simulatedMaint =
                    (prop.totalValuation.replace(",", "").replace("₹", "").take(3).toDoubleOrNull()
                        ?: 10.0) * 1000
                totalMaint += simulatedMaint

                val forecast = simulatedMaint * 1.1

                AssetOpsModel(
                    property = prop,
                    daysToVacancy = daysLeft,
                    isHighRisk = isRisk,
                    maintenanceSpendYtd = simulatedMaint,
                    capexForecast = forecast,
                    transparencyScore = Random.nextInt(70, 100)
                )
            }.sortedBy { it.daysToVacancy }

            _state.value = OpsState(
                isLoading = false,
                assets = opsAssets,
                totalMaintenanceSpend = totalMaint,
                highRiskCount = riskCounter
            )
        }
    }
}