// main/java/com/example/mahayuga/feature/assetmanager/presentation/listings/AmListingsViewModel.kt
package com.example.mahayuga.feature.assetmanager.presentation.listings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
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

data class ListingsState(
    val isLoading: Boolean = true,
    val pendingProperties: List<PropertyModel> = emptyList(),
    val liveProperties: List<PropertyModel> = emptyList(),
    val rejectedProperties: List<PropertyModel> = emptyList()
)

@HiltViewModel
class AmListingsViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableStateFlow(ListingsState())
    val state: StateFlow<ListingsState> = _state.asStateFlow()

    init {
        loadMyListings()
    }

    private fun loadMyListings() {
        // ⚡ Coroutine launched here for background fetching and real-time Flow collection
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            var amName = ""

            try {
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    // ⚡ Suspending function to retrieve profile linearly without callbacks
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

            // ⚡ collectLatest handles real-time property updates efficiently
            propertyRepository.getAllProperties().collectLatest { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        val myProps = uiState.data.filter {
                            it.assetManager.equals(
                                amName,
                                ignoreCase = true
                            )
                        }

                        _state.value = ListingsState(
                            isLoading = false,
                            pendingProperties = myProps.filter { it.approvalStatus == "PENDING" },
                            liveProperties = myProps.filter { it.approvalStatus == "APPROVED" },
                            rejectedProperties = myProps.filter { it.approvalStatus == "REJECTED" }
                        )
                    }

                    is UiState.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is UiState.Failure -> _state.value = _state.value.copy(isLoading = false)
                    else -> {}
                }
            }
        }
    }
}