package com.example.navyuga.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navyuga.core.common.UiState
import com.example.navyuga.core.data.local.PreferenceManager
import com.example.navyuga.feature.auth.data.model.UserModel
import com.example.navyuga.feature.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            val user = UserModel(name = name, email = email, role = "user")
            repository.registerUser(user, pass).collect { state ->
                _registerState.value = state
                if (state is UiState.Success) {
                    preferenceManager.saveLoginState(true)
                }
            }
        }
    }
}