package com.example.mahayuga.feature.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.auth.data.model.UserModel // Import UserModel
import com.example.mahayuga.feature.auth.presentation.components.GptTextField

// Dark Theme Colors
private val GptBlack = Color(0xFF000000)
private val GptTextWhite = Color(0xFFFFFFFF)
private val GptTextGrey = Color(0xFFC5C5D2)
private val GptBrandGreen = Color(0xFF10A37F)

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            // ⚡ FIXED: Check Role to determine destination
            val user = (loginState as UiState.Success<UserModel>).data

            val destination = if (user.role == "admin") {
                "admin_dashboard"
            } else {
                "super_app_hub"
            }

            navController.navigate(destination) {
                // Clear back stack so they can't go back to login/welcome
                popUpTo("welcome") { inclusive = true }
            }
        }
    }

    val isLoading = loginState is UiState.Loading
    val errorMessage = (loginState as? UiState.Failure)?.message

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GptBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Welcome back",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GptTextWhite
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        GptTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email address",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )

        Spacer(modifier = Modifier.height(16.dp))

        GptTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Continue Button
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GptTextWhite,
                contentColor = GptBlack
            ),
            shape = MaterialTheme.shapes.medium,
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = GptBlack, modifier = Modifier.size(24.dp))
            } else {
                Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Don't have an account?", color = GptTextGrey)
            TextButton(onClick = { navController.navigate("register") }) {
                Text("Sign up", color = GptBrandGreen, fontWeight = FontWeight.Bold)
            }
        }
    }
}