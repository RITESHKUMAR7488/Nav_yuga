package com.example.mahayuga.feature.auth.presentation

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.core.data.local.PreferenceManager
import com.example.mahayuga.feature.auth.data.model.UserModel
import com.example.mahayuga.feature.auth.presentation.components.GptTextField
import com.example.mahayuga.navigation.AssetManagerDestinations
import javax.inject.Inject

private val NavyBackground = Color(0xFF0F172A)
private val GptTextWhite = Color(0xFFFFFFFF)
private val GptTextGrey = Color(0xFFC5C5D2)
private val GptBrandGreen = Color(0xFF10A37F)
private val CardBg = Color(0xFF1E293B)

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    // ⚡ Inject PreferenceManager via Hilt in parent or pass manually?
    // Usually handled in VM, but for simplicity in Compose we can grab it if needed
    // Ideally, the ViewModel should handle the saving of preference.
) {
    // Note: In a pure arch, VM handles prefs. For this UI logic, we'll access it via the Activity or Hilt if possible.
    // For now, we will assume we can get it or perform the logic inside a LaunchedEffect using the VM's scope or similar.
    // To keep it clean, we will trigger navigation based on VM state, but since we need to save to Prefs *before* nav,
    // we will rely on the `preferenceManager` which is injected into the VM.
    // However, `AuthViewModel` in your code has `preferenceManager`. We should expose a method there.

    val loginState by viewModel.loginState.collectAsState()
    var showWorkspaceDialog by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf<UserModel?>(null) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // ⚡ INTERCEPTOR LOGIC
    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            val user = (loginState as UiState.Success<UserModel>).data
            currentUser = user

            when (user.role) {
                "asset_manager" -> {
                    // STOP: Show Dialog
                    showWorkspaceDialog = true
                }
                "admin" -> {
                    // Save Mode & Nav
                    viewModel.saveSessionMode("ADMIN")
                    navController.navigate("admin_dashboard") { popUpTo("welcome") { inclusive = true } }
                }
                else -> {
                    // Investor
                    viewModel.saveSessionMode("INVESTOR")
                    navController.navigate("navyuga_dashboard") { popUpTo("welcome") { inclusive = true } }
                }
            }
        }
    }

    val isLoading = loginState is UiState.Loading
    val errorMessage = (loginState as? UiState.Failure)?.message

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Log in",
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
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GptTextWhite,
                contentColor = NavyBackground
            ),
            shape = MaterialTheme.shapes.medium,
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = NavyBackground, modifier = Modifier.size(24.dp))
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

    // ⚡ WORKSPACE SELECTION DIALOG
    if (showWorkspaceDialog && currentUser != null) {
        Dialog(onDismissRequest = { /* Prevent Dismiss */ }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Select Workspace",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "You have access to both an Investor Portfolio and an Asset Manager Dashboard.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GptTextGrey,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Option A: Personal
                    Button(
                        onClick = {
                            viewModel.saveSessionMode("INVESTOR")
                            navController.navigate("navyuga_dashboard") { popUpTo("welcome") { inclusive = true } }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF)), // Blue
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Person, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Enter as Investor")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Option B: Work
                    Button(
                        onClick = {
                            viewModel.saveSessionMode("AM_WORK")
                            navController.navigate(AssetManagerDestinations.DASHBOARD) { popUpTo("welcome") { inclusive = true } }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38a882)), // Teal
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Business, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Enter as Asset Manager")
                    }
                }
            }
        }
    }
}