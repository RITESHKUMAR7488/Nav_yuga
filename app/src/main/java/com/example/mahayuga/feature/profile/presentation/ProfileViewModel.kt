package com.example.mahayuga.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.core.data.local.PreferenceManager
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.navyuga.domain.repository.PropertyRepository
import com.example.mahayuga.feature.auth.domain.repository.AuthRepository
import com.example.mahayuga.feature.profile.data.model.ProfileStat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val propertyRepository: PropertyRepository,
    private val preferenceManager: PreferenceManager,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _currentUser = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)
    val currentUser = _currentUser

    private val _allProperties = propertyRepository.getAllProperties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    val ownedProperties: StateFlow<List<PropertyModel>> = combine(
        _currentUser,
        _allProperties
    ) { userState, propsState ->
        if (userState is UiState.Success && propsState is UiState.Success) {
            val investedIds = userState.data.investedProperties
            propsState.data.filter { it.id in investedIds }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedProperties: StateFlow<List<PropertyModel>> = combine(
        _currentUser,
        _allProperties
    ) { userState, propsState ->
        if (userState is UiState.Success && propsState is UiState.Success) {
            val likedIds = userState.data.likedProperties
            propsState.data
                .filter { it.id in likedIds }
                .map { it.copy(isLiked = true) }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _stats = MutableStateFlow<List<ProfileStat>>(emptyList())
    val stats: StateFlow<List<ProfileStat>> = _stats

    init {
        calculateStats()
    }

    private fun calculateStats() {
        viewModelScope.launch {
            combine(_currentUser, ownedProperties) { userState, owned ->
                if (userState is UiState.Success) {
                    val user = userState.data

                    val totalInv = user.totalInvestment.toDouble()
                    val currentVal = user.currentValue.toDouble()
                    val totalArea = user.totalArea
                    val totalRent = user.totalRent.toDouble()

                    val count = owned.size
                    val propProgress = if (count > 0) (count / 5f).coerceIn(0f, 1f) else 0f

                    // ⚡ AUTHENTICATED ROI LOGIC ⚡
                    val roiDisplay: String
                    val roiProgress: Float

                    if (totalInv > 0) {
                        // 1. Capital Gains (Growth): (CurrentVal - Invested) / Invested
                        // Initially this is 0%
                        val capitalGains = ((currentVal - totalInv) / totalInv) * 100

                        // 2. Rental Yield (Income): (Annual Rent / Invested)
                        // This uses the REAL rent data we saved in the transaction
                        val annualRent = totalRent * 12
                        val rentalYield = (annualRent / totalInv) * 100

                        // 3. Total ROI = Growth + Yield
                        val totalRoi = capitalGains + rentalYield

                        roiDisplay = String.format("%.1f%%", totalRoi)
                        // Scale progress: 15% is a great ROI, so we use that as "100% progress"
                        roiProgress = (totalRoi / 15.0).toFloat().coerceIn(0f, 1f)
                    } else {
                        roiDisplay = "0%"
                        roiProgress = 0f
                    }

                    // Format Currency
                    fun formatK(amount: Double): String {
                        return if (amount >= 10000000) "₹${String.format("%.2f", amount/10000000)}Cr"
                        else if (amount >= 100000) "₹${String.format("%.1f", amount/100000)}L"
                        else if (amount >= 1000) "₹${(amount/1000).toInt()}k"
                        else "₹${amount.toInt()}"
                    }

                    val areaDisplay = if (totalArea >= 1.0) "${String.format("%.1f", totalArea)} Sqft" else "0 Sqft"

                    // Grid with Brand Blue (0xFF2979FF)
                    listOf(
                        ProfileStat("Properties", count.toString(), propProgress, 0xFF2979FF),
                        ProfileStat("Invested", formatK(totalInv), 1f, 0xFF2979FF),
                        ProfileStat("Total Area", areaDisplay, 1f, 0xFF2979FF),
                        ProfileStat("Avg. ROI", roiDisplay, roiProgress, 0xFF2979FF), // Now shows ~6.5%
                        ProfileStat("Monthly Rent", formatK(totalRent), 1f, 0xFF2979FF),
                        ProfileStat("Wallet", "₹0", 0f, 0xFF2979FF)
                    )
                } else {
                    listOf(
                        ProfileStat("Properties", "0", 0f, 0xFF2979FF),
                        ProfileStat("Invested", "₹0", 0f, 0xFF2979FF),
                        ProfileStat("Total Area", "0 Sqft", 0f, 0xFF2979FF),
                        ProfileStat("Avg. ROI", "0%", 0f, 0xFF2979FF),
                        ProfileStat("Monthly Rent", "₹0", 0f, 0xFF2979FF),
                        ProfileStat("Wallet", "₹0", 0f, 0xFF2979FF)
                    )
                }
            }.collect { newStats ->
                _stats.value = newStats
            }
        }
    }

    fun toggleLike(propertyId: String, currentLikeState: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(userId)
        if (currentLikeState) {
            userRef.update("likedProperties", FieldValue.arrayRemove(propertyId))
        } else {
            userRef.set(
                mapOf("likedProperties" to FieldValue.arrayUnion(propertyId)),
                SetOptions.merge()
            )
        }
    }

    fun logout() {
        auth.signOut()
        preferenceManager.saveLoginState(false)
    }
}