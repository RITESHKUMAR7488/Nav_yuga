package com.example.navyuga.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navyuga.feature.auth.presentation.LoginScreen
import com.example.navyuga.feature.auth.presentation.RegisterScreen
import com.example.navyuga.feature.hub.presentation.HubScreen
import com.example.navyuga.feature.arthyuga.presentation.ArthYugaDashboard
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.navyuga.feature.arthyuga.presentation.detail.PropertyDetailScreen
import com.example.navyuga.feature.arthyuga.presentation.search.SearchScreen

@Composable
fun AppNavigation(
    startDestination: String,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        // Auth Module
        composable("login") {
            LoginScreen(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )
        }
        composable("register") { RegisterScreen(navController) }

        // Hub Module
        composable("super_app_hub") { HubScreen(navController) }

        // ArthYuga Module - Linked!
        composable("arthyuga_dashboard") { ArthYugaDashboard(navController) } // ⚡ Pass NavController!

        composable(
            "property_detail/{propertyId}",
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: "1"
            PropertyDetailScreen(propertyId = propertyId, navController = navController)
        }
        // Admin
        composable("admin_dashboard") { PlaceholderScreen("Admin Dashboard") }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = MaterialTheme.colorScheme.onBackground)
    }
}