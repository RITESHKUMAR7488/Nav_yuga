package com.example.mahayuga.feature.auth.presentation

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mahayuga.core.common.* // IMPORTS
import com.example.mahayuga.feature.auth.data.model.UserModel
import com.example.mahayuga.navigation.AssetManagerDestinations
import com.example.mahayuga.ui.theme.* // IMPORTS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState.collectAsState()
    var showWorkspaceDialog by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf<UserModel?>(null) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // State handling omitted for brevity, same as before...

    val isLoading = loginState is com.example.mahayuga.core.common.UiState.Loading

    Scaffold(
        containerColor = BricxBackground,
        topBar = {
            BricxTopAppBar(title = "BRICX", onNavigateBack = { navController.popBackStack() })
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

            // USING OUR NEW TEXT FIELD COMPONENT
            BricxTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email address",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(16.dp))

            // USING OUR NEW TEXT FIELD COMPONENT
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

            // USING OUR NEW BUTTON COMPONENT
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