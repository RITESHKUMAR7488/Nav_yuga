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
    private val firestore: FirebaseFirestore // ⚡ ADDED: Required for database updates
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
            // Placeholder logic for ownership
            emptyList<PropertyModel>()
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
        // ⚡ COROUTINE USAGE: Launching a coroutine to collect flow updates.
        // This is good code because it reacts to changes in user or property data
        // in real-time without blocking the UI thread.
        viewModelScope.launch {
            combine(_currentUser, ownedProperties) { userState, owned ->
                if (userState is UiState.Success) {
                    val user = userState.data
                    val totalInv = user.totalInvestment.toDouble()
                    val currentVal = user.currentValue.toDouble()

                    val count = owned.size
                    val propProgress = if (count > 0) (count / 5f).coerceIn(0f, 1f) else 0f

                    val roiVal = if (totalInv > 0) ((currentVal - totalInv) / totalInv) * 100 else 0.0
                    val roiDisplay = String.format("%.1f%%", roiVal)
                    val roiProgress = if (roiVal > 0) (roiVal / 20f).toFloat().coerceIn(0f, 1f) else 0f

                    // 6 Stats for the 3x2 Grid
                    listOf(
                        ProfileStat("Properties", count.toString(), propProgress, 0xFF2979FF),
                        ProfileStat("Total ROI", roiDisplay, roiProgress, 0xFF2979FF),
                        ProfileStat("Total Rent", "₹0", 0f, 0xFF2979FF),
                        ProfileStat("Total Area", "0 Sqft", 0f, 0xFF2979FF),
                        // Placeholders
                        ProfileStat("Coming Soon", "-", 0f, 0xFF888888),
                        ProfileStat("Coming Soon", "-", 0f, 0xFF888888)
                    )
                } else {
                    listOf(
                        ProfileStat("Properties", "0", 0f, 0xFF2979FF),
                        ProfileStat("Total ROI", "0%", 0f, 0xFF2979FF),
                        ProfileStat("Total Rent", "₹0", 0f, 0xFF2979FF),
                        ProfileStat("Total Area", "0 Sqft", 0f, 0xFF2979FF),
                        ProfileStat("Coming Soon", "-", 0f, 0xFF888888),
                        ProfileStat("Coming Soon", "-", 0f, 0xFF888888)
                    )
                }
            }.collect { newStats ->
                _stats.value = newStats
            }
        }
    }

    // ⚡ ADDED: Function to handle Like/Unlike logic
    fun toggleLike(propertyId: String, currentLikeState: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(userId)

        if (currentLikeState) {
            // Remove from liked
            userRef.update("likedProperties", FieldValue.arrayRemove(propertyId))
        } else {
            // Add to liked
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