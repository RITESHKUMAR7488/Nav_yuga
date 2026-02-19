// main/java/com/example/mahayuga/feature/assetmanager/presentation/investors/InvestorViewModel.kt
package com.example.mahayuga.feature.assetmanager.presentation.investors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.admin.data.model.InvestmentModel
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.navyuga.domain.repository.PropertyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

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
    val fundraisingVelocity: List<Float> = emptyList(), // Placeholder for trend
    val investorList: List<InvestorModel> = emptyList()
)

@HiltViewModel
class InvestorViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableStateFlow(InvestorState())
    val state: StateFlow<InvestorState> = _state.asStateFlow()

    init {
        loadRealInvestorData()
    }

    private fun loadRealInvestorData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            var amName = ""

            // 1. Get Asset Manager Identity
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

            // 2. Fetch My Properties
            propertyRepository.getAllProperties().collectLatest { uiState ->
                if (uiState is UiState.Success) {
                    // Filter properties that belong to this AM
                    val myProperties = uiState.data.filter { it.assetManager.equals(amName, true) }
                    val myPropertyIds = myProperties.map { it.id }

                    if (myPropertyIds.isNotEmpty()) {
                        fetchInvestments(myPropertyIds)
                    } else {
                        _state.value = InvestorState(isLoading = false)
                    }
                }
            }
        }
    }

    private suspend fun fetchInvestments(propertyIds: List<String>) {
        try {
            // 3. Fetch Investments specific to these properties
            // Firestore 'in' queries are limited to 10 items, so we fetch all and filter (for MVP)
            // For production with >10 properties, you'd structure this differently.
            val snapshot = firestore.collection("investments").get().await()
            val allInvestments = snapshot.toObjects(InvestmentModel::class.java)

            // Filter only investments for MY properties
            val myInvestments = allInvestments.filter { it.propertyId in propertyIds }

            processInvestorData(myInvestments)

        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private fun processInvestorData(investments: List<InvestmentModel>) {
        // Group by User ID to create "Investor Profiles"
        val grouped = investments.groupBy { it.userId }

        val investorModels = grouped.map { (userId, invList) ->
            val totalInvested = invList.sumOf { it.amount.toDouble() }
            val name = invList.firstOrNull()?.userName ?: "Unknown Investor"

            // Logic for Type
            val type = when {
                totalInvested > 10000000 -> "Institutional" // > 1 Cr
                totalInvested > 2500000 -> "HNI"            // > 25 L
                else -> "Retail"
            }

            // Logic for Tags
            val tags = mutableListOf<String>()
            if (totalInvested > 5000000) tags.add("WHALE")
            if (invList.size > 1) tags.add("LOYAL") // Invested multiple times

            InvestorModel(
                id = userId,
                name = name,
                type = type,
                totalInvested = totalInvested,
                joinDate = "Active", // simplified
                reinvestCount = invList.size,
                riskScore = if (invList.size > 2) 10 else 50, // More investments = Lower churn risk
                tags = tags
            )
        }.sortedByDescending { it.totalInvested }

        // Metrics Calculation
        val totalCapital = investorModels.sumOf { it.totalInvested }
        val avgTicket = if (investorModels.isNotEmpty()) totalCapital / investorModels.size else 0.0

        // Whale Risk: % of capital held by top 5 investors
        val top5Capital = investorModels.take(5).sumOf { it.totalInvested }
        val concentration = if (totalCapital > 0) (top5Capital / totalCapital) * 100 else 0.0
        val isWhaleRisk = concentration > 40.0

        _state.value = InvestorState(
            isLoading = false,
            totalInvestors = investorModels.size,
            averageTicketSize = avgTicket,
            whaleConcentrationPercent = concentration,
            isWhaleRiskHigh = isWhaleRisk,
            fundraisingVelocity = listOf(10f, 20f, 15f, 40f, 35f, 50f), // Dummy trend for graph UI
            investorList = investorModels
        )
    }
}