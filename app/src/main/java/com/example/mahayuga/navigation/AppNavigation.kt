package com.example.mahayuga.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.admin.presentation.*
import com.example.mahayuga.feature.auth.data.model.UserModel
import com.example.mahayuga.feature.auth.presentation.AuthViewModel
import com.example.mahayuga.feature.auth.presentation.LoginScreen
import com.example.mahayuga.feature.auth.presentation.RegisterScreen
import com.example.mahayuga.feature.auth.presentation.WelcomeScreen
import com.example.mahayuga.feature.hub.presentation.HubScreen
import com.example.mahayuga.feature.navyuga.presentation.NavYugaDashboard
import com.example.mahayuga.feature.navyuga.presentation.detail.PropertyDetailScreen
import com.example.mahayuga.feature.navyuga.presentation.search.SearchResultsScreen
import com.example.mahayuga.feature.navyuga.presentation.search.SearchScreen
import com.example.mahayuga.feature.navyuga.presentation.splash.NavyugaSplashScreen
import com.example.mahayuga.feature.profile.presentation.AccountDetailsScreen
import com.example.mahayuga.feature.profile.presentation.HelpCenterScreen
import com.example.mahayuga.feature.profile.presentation.LikedPropertiesScreen
import com.example.mahayuga.feature.profile.presentation.ProfileMenuScreen
import com.example.mahayuga.feature.profile.presentation.SecurityPrivacyScreen
import com.example.mahayuga.feature.profile.presentation.SettingsScreen
import com.example.mahayuga.feature.profile.presentation.WalletScreen
import com.example.mahayuga.feature.profile.presentation.AboutNavyugaScreen
import com.example.mahayuga.feature.roi.presentation.RoiScreen

@Composable
fun AppNavigation(
    startDestination: String,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        // --- WELCOME & AUTH ---
        composable("welcome") { WelcomeScreen(navController = navController) }
        composable("login") { LoginScreen(navController = navController) }
        composable("register") { RegisterScreen(navController = navController) }

        // --- HUB ---
        composable("super_app_hub") {
            HubScreen(
                navController = navController,
                onLogout = { navController.navigate("welcome") { popUpTo(0) { inclusive = true } } },
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onNavigateToSettings = { navController.navigate("settings_screen") },
                onNavigateToSecurity = { navController.navigate("security_privacy") },
                onNavigateToHelp = { navController.navigate("help_center") }
            )
        }

        // --- NAVYUGA DASHBOARD ---
        composable("navyuga_dashboard") {
            NavYugaDashboard(
                rootNavController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onLogout = { navController.navigate("welcome") { popUpTo(0) { inclusive = true } } },
                onNavigateToSettings = { navController.navigate("settings_screen") },
                onNavigateToSecurity = { navController.navigate("security_privacy") },
                onNavigateToHelp = { navController.navigate("help_center") },
                onNavigateToMenu = { navController.navigate("profile_menu") }
            )
        }

        // --- PROFILE MENU (FULL PAGE) ---
        composable("profile_menu") {
            ProfileMenuScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToLiked = { navController.navigate("liked_properties") },
                onNavigateToAccount = { navController.navigate("account_details") },
                onNavigateToSettings = { navController.navigate("settings_screen") },
                onNavigateToSecurity = { navController.navigate("security_privacy") },
                onNavigateToHelp = { navController.navigate("help_center") },
                onNavigateToWallet = { navController.navigate("wallet_screen") },
                onNavigateToAbout = { navController.navigate("about_navyuga") }
            )
        }

        // --- PROFILE FEATURES ---
        composable("account_details") {
            AccountDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onAccountDeleted = { navController.navigate("welcome") { popUpTo(0) { inclusive = true } } }
            )
        }

        composable("settings_screen") {
            SettingsScreen(onBackClick = { navController.popBackStack() })
        }

        composable("security_privacy") {
            SecurityPrivacyScreen(onBackClick = { navController.popBackStack() })
        }

        composable("help_center") {
            HelpCenterScreen(onBackClick = { navController.popBackStack() })
        }

        composable("liked_properties") {
            LikedPropertiesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id -> navController.navigate("property_detail/$id") }
            )
        }

        composable("wallet_screen") {
            WalletScreen(onBackClick = { navController.popBackStack() })
        }

        composable("about_navyuga") {
            AboutNavyugaScreen(onBackClick = { navController.popBackStack() })
        }

        // --- PROPERTY DETAILS & SEARCH ---
        composable("property_detail/{propertyId}", arguments = listOf(navArgument("propertyId") { type = NavType.StringType })) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            PropertyDetailScreen(propertyId = propertyId, onNavigateBack = { navController.popBackStack() })
        }

        composable("roi_calculator") { RoiScreen(onBackClick = { navController.popBackStack() }) }

        composable("search_results/{country}/{city}", arguments = listOf(navArgument("country") { type = NavType.StringType }, navArgument("city") { type = NavType.StringType })) { backStackEntry ->
            val country = backStackEntry.arguments?.getString("country") ?: "India"
            val city = backStackEntry.arguments?.getString("city") ?: "All Cities"
            SearchResultsScreen(
                country = country,
                city = city,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id -> navController.navigate("property_detail/$id") },
                onRoiClick = { navController.navigate("roi_calculator") }
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
                    // ⚡ FIX: Call ViewModel logout to clear prefs
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate("welcome") { popUpTo(0) { inclusive = true } }
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

        composable("admin_edit_property/{propertyId}", arguments = listOf(navArgument("propertyId") { type = NavType.StringType })) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            EditPropertyScreen(navController = navController, propertyId = propertyId)
        }

        // Investment Flow
        navigation(startDestination = "admin_register_investment", route = "investment_flow") {
            composable("admin_register_investment") { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry("investment_flow") }
                val sharedViewModel: AdminViewModel = hiltViewModel(parentEntry)
                AdminSelectUserScreen(navController, viewModel = sharedViewModel)
            }
            composable("admin_inv_select_property") { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry("investment_flow") }
                val sharedViewModel: AdminViewModel = hiltViewModel(parentEntry)
                AdminSelectPropertyScreen(navController, viewModel = sharedViewModel)
            }
            composable("admin_inv_form") { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry("investment_flow") }
                val sharedViewModel: AdminViewModel = hiltViewModel(parentEntry)
                AdminInvestmentFormScreen(navController, viewModel = sharedViewModel)
            }
            composable("admin_user_detail/{userId}", arguments = listOf(navArgument("userId") { type = NavType.StringType })) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                AdminUserDetailScreen(navController = navController, userId = userId)
            }
        }

        // --- SPLASH ---
        composable("navyuga_splash") {
            NavyugaSplashScreen(
                onSplashFinished = { navController.navigate("navyuga_dashboard") { popUpTo("navyuga_splash") { inclusive = true } } }
            )
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