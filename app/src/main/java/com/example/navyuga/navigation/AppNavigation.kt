package com.example.navyuga.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navyuga.feature.arthyuga.presentation.ArthYugaDashboard
import com.example.navyuga.feature.arthyuga.presentation.home.HomeScreen
import com.example.navyuga.feature.auth.presentation.LoginScreen
import com.example.navyuga.feature.auth.presentation.RegisterScreen
import com.example.navyuga.feature.hub.presentation.HubScreen
import com.example.navyuga.ui.theme.MidnightBg
import com.example.navyuga.ui.theme.TextWhiteHigh

@Composable
fun AppNavigation(
    startDestination: String,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        // Auth Module
        composable("login") {
            LoginScreen(navController = navController, onThemeToggle = onThemeToggle)
        }
        composable("register") {
            RegisterScreen(navController)
        }

        // Hub Module
        composable("super_app_hub") {
            HubScreen(navController)
        }

        // ArthYuga Module (Placeholder for now, or link to Dashboard if you added Phase 3)
        composable("arthyuga_dashboard") {
            ArthYugaDashboard(navController)
        }

        // Admin
        composable("admin_dashboard") {
            PlaceholderScreen("Admin Dashboard")
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(MidnightBg),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = TextWhiteHigh)
    }
}