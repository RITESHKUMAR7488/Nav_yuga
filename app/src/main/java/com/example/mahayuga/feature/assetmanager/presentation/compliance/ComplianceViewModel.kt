package com.example.mahayuga.feature.assetmanager.presentation.compliance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- Domain Models ---
enum class ComplianceStatus(val label: String) {
    COMPLETED("Completed"),
    PENDING("Action Required"),
    OVERDUE("Overdue"),
    IN_REVIEW("In Review")
}

data class ComplianceItem(
    val id: String,
    val title: String,
    val description: String,
    val status: ComplianceStatus,
    val dueDate: String,
    val progress: Float
)

// --- State Management ---
sealed class ComplianceUiState {
    object Loading : ComplianceUiState()
    data class Success(val compliances: List<ComplianceItem>) : ComplianceUiState()
    data class Error(val message: String) : ComplianceUiState()
}

class ComplianceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ComplianceUiState>(ComplianceUiState.Loading)
    val uiState: StateFlow<ComplianceUiState> = _uiState.asStateFlow()

    init {
        fetchComplianceData()
    }

    private fun fetchComplianceData() {
        // Coroutine used here to handle asynchronous data fetching without blocking the main thread.
        viewModelScope.launch {
            _uiState.update { ComplianceUiState.Loading }
            try {
                // Simulating network delay for fetching SEBI/Bricx compliance parameters
                delay(1200)

                val mockData = listOf(
                    ComplianceItem(
                        id = "1",
                        title = "SEBI Quarterly Filing",
                        description = "Submit Q3 financial disclosures and asset performance metrics.",
                        status = ComplianceStatus.PENDING,
                        dueDate = "Oct 15, 2026",
                        progress = 0.4f
                    ),
                    ComplianceItem(
                        id = "2",
                        title = "Bricx Internal Audit",
                        description = "Complete mandatory platform security and risk assessment.",
                        status = ComplianceStatus.COMPLETED,
                        dueDate = "Sep 30, 2026",
                        progress = 1.0f
                    ),
                    ComplianceItem(
                        id = "3",
                        title = "Investor KYC Renewal",
                        description = "Update verification documents for 14 Tier-1 investors.",
                        status = ComplianceStatus.OVERDUE,
                        dueDate = "Oct 01, 2026",
                        progress = 0.1f
                    ),
                    ComplianceItem(
                        id = "4",
                        title = "Tax Compliance & TDS",
                        description = "Filing of tax deducted at source for property yields.",
                        status = ComplianceStatus.IN_REVIEW,
                        dueDate = "Oct 20, 2026",
                        progress = 0.85f
                    )
                )
                _uiState.update { ComplianceUiState.Success(mockData) }
            } catch (e: Exception) {
                _uiState.update { ComplianceUiState.Error("Failed to load compliance data. Please check your connection.") }
            }
        }
    }

    fun refreshData() {
        fetchComplianceData()
    }
}