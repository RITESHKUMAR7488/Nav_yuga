// main/java/com/example/mahayuga/feature/assetmanager/presentation/profile/AmProfileViewModel.kt
package com.example.mahayuga.feature.assetmanager.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.auth.data.model.AssetManagerModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class AmProfileData(
    val id: String = "",
    val brandName: String = "Embassy REIT",
    val contactName: String = "Asset Manager",
    val legalEntityName: String = "Embassy Office Parks",
    val entityType: String = "REIT/InvIT",
    val email: String = "contact@embassy.com",
    val mobile: String = "+91 -",
    val operatingCities: String = "Bengaluru, Mumbai",
    val yearsInOperation: String = "10+ Years",
    val aum: String = "₹48,800 Cr",
    val sebiRegNo: String = "Registered under SEBI REIT",

    // Financials matching the design
    val currentPrice: Double = 410.50,
    val priceChange: Double = 0.62,
    val openPrice: Double = 408.55,
    val lastPrice: Double = 407.95,
    val marketCap: String = "₹33,850 Cr",
    val dividendYield: String = "6.7%",
    val high52: String = "₹435.20",
    val low52: String = "₹366.00",
    val totalHoldings: String = "42.6 MSF",
    val underDev: String = "+8.1 MSF",
    val occupancy: String = "86%"
)

@HiltViewModel
class AmProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AmProfileData>>(UiState.Loading)
    val uiState: StateFlow<UiState<AmProfileData>> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                val doc = firestore.collection("asset_managers").document(uid).get().await()
                val am = doc.toObject(AssetManagerModel::class.java)

                if (am != null) {
                    val data = AmProfileData(
                        id = uid,
                        brandName = am.brandName.ifEmpty { am.entityLegalName }
                            .ifEmpty { "Navyuga Partner" },
                        contactName = am.contactName.ifEmpty { "Asset Manager" },
                        legalEntityName = am.entityLegalName.ifEmpty { "Not Provided" },
                        entityType = am.entityType.ifEmpty { "Company" },
                        email = am.email,
                        mobile = am.mobile,
                        operatingCities = am.operatingCities.joinToString(", ").ifEmpty { "India" },
                        yearsInOperation = am.yearsInOperation.ifEmpty { "N/A" },
                        aum = am.aumRange.ifEmpty { "N/A" },
                        sebiRegNo = am.sebiRegistrationNo.ifEmpty { "Pending SEBI Registration" }
                    )
                    _uiState.value = UiState.Success(data)
                } else {
                    // Fallback to dummy data to match UI exactly if testing without a real account
                    _uiState.value = UiState.Success(AmProfileData())
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Success(AmProfileData())
            }
        }
    }
}