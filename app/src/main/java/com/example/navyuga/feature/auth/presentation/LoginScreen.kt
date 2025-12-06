package com.example.navyuga.feature.auth.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Collecting StateFlow safely in Compose
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is UiState.Success -> {
                val user = state.data
                if (user.role == "admin") {
                    navController.navigate("admin_dashboard")
                } else {
                    navController.navigate("super_app_hub")
                }
            }
            is UiState.Failure -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo (Placeholder for now, use Resource ID from your assets)
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Ensure you have this or similar
            contentDescription = "Logo",
            modifier = Modifier
                .size(100.dp)
                .background(MidnightSurface, shape = androidx.compose.foundation.shape.CircleShape)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium,
            color = TextWhiteHigh,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Login to manage your portfolio",
            style = MaterialTheme.typography.bodyMedium,
            color = TextWhiteMedium
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
            Text(text = "New to Navyuga? ", color = TextWhiteMedium)
            Text(
                text = "Create Account",
                color = BrandBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.navigate("register") }
            )
        }
    }

}
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    NavyugaTheme {
        // We use a fake NavController for the preview
        LoginScreen(navController = androidx.navigation.compose.rememberNavController())
    }
}