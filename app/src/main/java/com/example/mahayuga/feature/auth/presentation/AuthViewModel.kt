package com.example.mahayuga.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.core.data.local.PreferenceManager
import com.example.mahayuga.feature.auth.data.model.UserModel
import com.example.mahayuga.feature.auth.domain.repository.AuthRepository
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
    private val preferenceManager: PreferenceManager
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
                    preferenceManager.saveLoginState(true)
                }
            }
        }
    }

    // ⚡ UPDATE: Accepts DOB and sets default isApproved = false
    fun register(name: String, email: String, pass: String, dob: String) {
        viewModelScope.launch {
            val user = UserModel(
                name = name,
                email = email,
                dob = dob,
                role = "user",
                isApproved = false // Default for new users
            )
            repository.registerUser(user, pass).collect { state ->
                _registerState.value = state
                // Note: We do NOT save login state here because they are pending approval
            }
        }
    }
}