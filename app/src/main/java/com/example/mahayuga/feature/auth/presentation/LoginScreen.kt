// main/java/com/example/mahayuga/feature/auth/presentation/LoginScreen.kt
package com.example.mahayuga.feature.auth.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mahayuga.core.common.*
import com.example.mahayuga.feature.auth.data.model.UserModel
import com.example.mahayuga.navigation.AssetManagerDestinations
import com.example.mahayuga.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current // ⚡ Added context for Toasts

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isLoading = loginState is com.example.mahayuga.core.common.UiState.Loading

    // ⚡ Handles both Success Navigation AND Failure Errors now
    LaunchedEffect(loginState) {
        when (loginState) {
            is UiState.Success -> {
                val user = (loginState as UiState.Success<UserModel>).data
                when (user.role) {
                    "asset_manager" -> {
                        navController.navigate(AssetManagerDestinations.DASHBOARD) {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                    "admin" -> {
                        navController.navigate("admin_dashboard") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                    else -> {
                        navController.navigate("broker_selection") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                }
            }
            is UiState.Failure -> {
                // If wrong password, show the error!
                val errorMessage = (loginState as UiState.Failure).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = BricxBackground,
        topBar = {
            BricxTopAppBar(title = "BricX", onNavigateBack = { navController.popBackStack() })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Log in",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BricxTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(32.dp))

            BricxTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email address",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(16.dp))

            BricxTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                isPassword = true,
                onImeAction = { viewModel.login(email, password) }
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(color = BricxBrandTeal)
            } else {
                BricxPrimaryButton(
                    text = "Continue",
                    onClick = { viewModel.login(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?", color = BricxTextSecondary)
                TextButton(onClick = { navController.navigate("register") }) {
                    Text("Sign up", color = BricxBrandTeal, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}