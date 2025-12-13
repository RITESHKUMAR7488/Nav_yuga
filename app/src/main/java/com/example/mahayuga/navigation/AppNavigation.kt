package com.example.mahayuga.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.admin.presentation.*
import com.example.mahayuga.feature.auth.data.model.UserModel
import com.example.mahayuga.feature.auth.presentation.AuthViewModel
import com.example.mahayuga.feature.auth.presentation.LoginScreen
import com.example.mahayuga.feature.auth.presentation.RegisterScreen
import com.example.mahayuga.feature.hub.presentation.HubScreen
import com.example.mahayuga.feature.navyuga.presentation.NavYugaDashboard
import com.example.mahayuga.feature.navyuga.presentation.detail.PropertyDetailScreen
import com.example.mahayuga.feature.navyuga.presentation.search.SearchResultsScreen
import com.example.mahayuga.feature.profile.presentation.LikedPropertiesScreen
import com.example.mahayuga.feature.roi.presentation.RoiScreen

@Composable
fun AppNavigation(
    startDestination: String,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        // ... [Existing Auth & Hub routes remain unchanged] ...

        // Auth Module
        composable("login") {
            LoginScreen(navController = navController, isDarkTheme = isDarkTheme, onThemeToggle = onThemeToggle)
        }
        composable("register") { RegisterScreen(navController) }

        composable("super_app_hub") {
            HubScreen(
                navController = navController,
                onLogout = { navController.navigate("login") { popUpTo(0) { inclusive = true } } },
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )
        }

        // ArthYuga User Dashboard
        composable("navyuga_dashboard") {
            NavYugaDashboard(
                rootNavController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onLogout = {
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // ⚡ UPDATED: Route for Liked Properties with Navigation Callback
        composable("liked_properties") {
            LikedPropertiesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id -> navController.navigate("property_detail/$id") }
            )
        }

        // ... [Rest of the existing routes remain unchanged] ...

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

        composable(
            "search_results/{country}/{city}",
            arguments = listOf(
                navArgument("country") { type = NavType.StringType },
                navArgument("city") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val country = backStackEntry.arguments?.getString("country") ?: "India"
            val city = backStackEntry.arguments?.getString("city") ?: "All Cities"

            SearchResultsScreen(
                country = country,
                city = city,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id -> navController.navigate("property_detail/$id") }
            )
        }

        // --- ADMIN MODULE (SECURED) ---
        composable("admin_dashboard") {
            val authViewModel: AuthViewModel = hiltViewModel()
            val currentUserState by authViewModel.currentUser.collectAsState()
            val isAdmin = (currentUserState as? UiState.Success<UserModel>)?.data?.role == "admin"

            if (isAdmin) {
                AdminDashboardScreen(
                    navController = navController,
                    onLogout = { navController.navigate("login") { popUpTo(0) { inclusive = true } } }
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