package com.example.navyuga.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.navyuga.feature.admin.presentation.AddPropertyScreen
import com.example.navyuga.feature.admin.presentation.AdminDashboardScreen
import com.example.navyuga.feature.admin.presentation.CreateUserScreen
import com.example.navyuga.feature.admin.presentation.ManagePropertiesScreen
import com.example.navyuga.feature.admin.presentation.ManageUsersScreen
//import com.example.navyuga.feature.admin.presentation.RegisterInvestmentScreen
import com.example.navyuga.feature.arthyuga.presentation.ArthYugaDashboard
import com.example.navyuga.feature.arthyuga.presentation.detail.PropertyDetailScreen
import com.example.navyuga.feature.auth.presentation.LoginScreen
import com.example.navyuga.feature.auth.presentation.RegisterScreen
import com.example.navyuga.feature.hub.presentation.HubScreen

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
            LoginScreen(navController = navController, isDarkTheme = isDarkTheme, onThemeToggle = onThemeToggle)
        }
        composable("register") { RegisterScreen(navController) }

        // Hub
        composable("super_app_hub") { HubScreen(navController) }

        // ArthYuga
        composable("arthyuga_dashboard") {
            ArthYugaDashboard(
                rootNavController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onLogout = {
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // ⚡ FIX IS HERE: propertyId is NOT passed anymore
        composable(
            "property_detail/{propertyId}",
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) {
            // The ViewModel extracts propertyId automatically using SavedStateHandle
            PropertyDetailScreen(navController = navController)
        }

        // --- ADMIN MODULE ---

        composable("admin_dashboard") {
            AdminDashboardScreen(
                navController = navController,
                onLogout = {
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable("admin_create_user") { CreateUserScreen(navController) }

//        composable("admin_register_investment") { RegisterInvestmentScreen(navController) }

        composable("admin_manage_properties") { ManagePropertiesScreen(navController) }
        composable("admin_manage_users") { ManageUsersScreen(navController) }

        // Placeholder for Add Property
        composable("admin_add_property") {
            AddPropertyScreen( navController)
        }
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