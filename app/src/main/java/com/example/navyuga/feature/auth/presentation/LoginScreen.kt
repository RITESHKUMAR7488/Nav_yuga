package com.example.navyuga.feature.auth.presentation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.navyuga.R
import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.auth.presentation.components.NavyugaGradientButton
import com.example.navyuga.feature.auth.presentation.components.NavyugaTextField
import com.example.navyuga.ui.theme.*

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    onThemeToggle: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            val user = (loginState as UiState.Success).data
            if (user.role == "admin") {
                navController.navigate("admin_dashboard") {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                navController.navigate("super_app_hub") {
                    popUpTo("login") { inclusive = true }
                }
            }
        } else if (loginState is UiState.Failure) {
            Toast.makeText(context, (loginState as UiState.Failure).message, Toast.LENGTH_LONG).show()
        }
    }

    // Scaffold ensures content is not cut off by system bars
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {

            // Theme Toggle Button (Top Right)
            IconButton(
                onClick = onThemeToggle,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                // You can swap the icon based on state if you pass isDarkTheme here
                Icon(
                    painter = painterResource(id = R.drawable.ic_moon),
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = androidx.compose.foundation.shape.CircleShape)
                        .padding(16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Login to manage your portfolio",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                NavyugaTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    icon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                NavyugaTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    icon = Icons.Default.Lock,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                NavyugaGradientButton(
                    text = "Secure Login",
                    isLoading = loginState is UiState.Loading,
                    onClick = { viewModel.login(email, password) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    Text(
                        text = "New to Navyuga? ",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Create Account",
                        color = BrandBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { navController.navigate("register") }
                    )
                }
            }
        }
    }
}