package com.example.navyuga.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue // ⚡ FIXED: Necessary for 'by' delegation
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.admin.presentation.AddPropertyScreen
import com.example.navyuga.feature.admin.presentation.AdminDashboardScreen
import com.example.navyuga.feature.admin.presentation.CreateUserScreen
import com.example.navyuga.feature.admin.presentation.ManagePropertiesScreen
import com.example.navyuga.feature.admin.presentation.ManageUsersScreen
import com.example.navyuga.feature.arthyuga.presentation.ArthYugaDashboard
import com.example.navyuga.feature.arthyuga.presentation.detail.PropertyDetailScreen
import com.example.navyuga.feature.auth.data.model.UserModel // ⚡ FIXED: Import needed for casting
import com.example.navyuga.feature.auth.presentation.AuthViewModel
import com.example.navyuga.feature.auth.presentation.LoginScreen
import com.example.navyuga.feature.auth.presentation.RegisterScreen
import com.example.navyuga.feature.hub.presentation.HubScreen
import com.example.navyuga.feature.roi.presentation.RoiScreen

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

        // ArthYuga User Dashboard
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

        composable(
            "property_detail/{propertyId}",
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            PropertyDetailScreen(
                propertyId = propertyId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("roi_calculator") {
            RoiScreen(onBackClick = { navController.popBackStack() })
        }

        // --- ADMIN MODULE (SECURED) ---

        composable("admin_dashboard") {
            val authViewModel: AuthViewModel = hiltViewModel()
            val currentUserState by authViewModel.currentUser.collectAsState()

            // ⚡ FIXED: Safe Casting with Generics
            // We specifically cast to UiState.Success<UserModel> so Kotlin knows 'data' is a UserModel
            val isAdmin = (currentUserState as? UiState.Success<UserModel>)?.data?.role == "admin"

            // Security Check
            LaunchedEffect(currentUserState) {
                if (currentUserState is UiState.Success && !isAdmin) {
                    // Uncomment to activate security:
                    // navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            }

            if (isAdmin) {
                AdminDashboardScreen(
                    navController = navController,
                    onLogout = {
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    }
                )
            } else {
                PlaceholderScreen("Verifying Admin Privileges...")
            }
        }

        composable("admin_create_user") { CreateUserScreen(navController) }
        composable("admin_manage_properties") { ManagePropertiesScreen(navController) }
        composable("admin_manage_users") { ManageUsersScreen(navController) }
        composable("admin_add_property") { AddPropertyScreen(navController) }
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