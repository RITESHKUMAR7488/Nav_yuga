package com.example.navyuga.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navyuga.core.common.UiState
import com.example.navyuga.core.data.local.PreferenceManager
import com.example.navyuga.feature.arthyuga.domain.model.PropertyModel
import com.example.navyuga.feature.arthyuga.domain.repository.PropertyRepository
import com.example.navyuga.feature.auth.data.model.UserModel
import com.example.navyuga.feature.auth.domain.repository.AuthRepository
import com.example.navyuga.feature.profile.data.model.ProfileStat
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val propertyRepository: PropertyRepository,
    private val preferenceManager: PreferenceManager,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _currentUser = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)
    val currentUser = _currentUser

    private val _allProperties = propertyRepository.getAllProperties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    // Owned Properties Logic
    val ownedProperties: StateFlow<List<PropertyModel>> = combine(
        _currentUser,
        _allProperties
    ) { userState, propsState ->
        if (userState is UiState.Success && propsState is UiState.Success) {
            // Placeholder: When backend has 'ownerId', filter here.
            // Currently returning empty as requested.
            emptyList<PropertyModel>()
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Liked Properties Logic (Maps 'isLiked' = true)
    val likedProperties: StateFlow<List<PropertyModel>> = combine(
        _currentUser,
        _allProperties
    ) { userState, propsState ->
        if (userState is UiState.Success && propsState is UiState.Success) {
            val likedIds = userState.data.likedProperties
            propsState.data
                .filter { it.id in likedIds }
                .map { it.copy(isLiked = true) } // Force Red Heart
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

                    // 1. Properties
                    val count = owned.size
                    // ⚡ Dynamic Progress: 1 property = 20% fill, max at 5 properties
                    val propProgress = if (count > 0) (count / 5f).coerceIn(0f, 1f) else 0f

                    // 2. ROI
                    val roiVal = if (totalInv > 0) ((currentVal - totalInv) / totalInv) * 100 else 0.0
                    val roiDisplay = String.format("%.1f%%", roiVal)
                    // ⚡ Dynamic Progress: 20% ROI = 100% fill
                    val roiProgress = if (roiVal > 0) (roiVal / 20f).toFloat().coerceIn(0f, 1f) else 0f

                    // 3. Rent (Placeholder)
                    val rentProgress = 0f

                    // 4. Area (Placeholder)
                    val areaProgress = 0f

                    listOf(
                        ProfileStat("Properties", count.toString(), propProgress, 0xFF2979FF),
                        ProfileStat("Total ROI", roiDisplay, roiProgress, 0xFF2979FF),
                        ProfileStat("Total Rent", "₹0", rentProgress, 0xFF2979FF),
                        ProfileStat("Total Area", "0 Sqft", areaProgress, 0xFF2979FF)
                    )
                } else {
                    // Default Zero State
                    listOf(
                        ProfileStat("Properties", "0", 0f, 0xFF2979FF),
                        ProfileStat("Total ROI", "0%", 0f, 0xFF2979FF),
                        ProfileStat("Total Rent", "₹0", 0f, 0xFF2979FF),
                        ProfileStat("Total Area", "0 Sqft", 0f, 0xFF2979FF)
                    )
                }
            }.collect { newStats ->
                _stats.value = newStats
            }
        }
    }

    fun logout() {
        auth.signOut()
        preferenceManager.saveLoginState(false)
    }
}