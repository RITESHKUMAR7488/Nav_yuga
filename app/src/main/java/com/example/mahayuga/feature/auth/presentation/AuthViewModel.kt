package com.example.mahayuga.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.core.data.local.PreferenceManager
import com.example.mahayuga.feature.auth.data.model.UserModel
import com.example.mahayuga.feature.auth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val preferenceManager: PreferenceManager,
    private val auth: FirebaseAuth // ⚡ Inject FirebaseAuth
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<UserModel>>(UiState.Idle)
    val loginState: StateFlow<UiState<UserModel>> = _loginState

    private val _registerState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val registerState: StateFlow<UiState<String>> = _registerState

    val currentUser: StateFlow<UiState<UserModel>> = repository.getCurrentUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            repository.loginUser(email, pass).collect { state ->
                _loginState.value = state
                if (state is UiState.Success) {
                    val user = state.data
                    preferenceManager.saveLoginState(true)
                    preferenceManager.saveUserRole(user.role)
                }
            }
        }
    }

    fun register(name: String, email: String, pass: String, dob: String, phone: String) {
        viewModelScope.launch {
            val user = UserModel(
                name = name,
                email = email,
                dob = dob,
                phone = phone,
                role = "user",
                isApproved = false
            )
            repository.registerUser(user, pass).collect { state ->
                _registerState.value = state
            }
        }
    }

    // ⚡ NEW: Logout function to clear prefs
    fun logout() {
        auth.signOut()
        preferenceManager.saveLoginState(false)
        preferenceManager.clear() // Optional: Clears all prefs to be safe
    }
}