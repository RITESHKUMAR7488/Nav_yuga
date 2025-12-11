package com.example.navyuga.feature.arthyuga.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.navyuga.feature.arthyuga.presentation.home.HomeScreen
import com.example.navyuga.feature.arthyuga.presentation.search.SearchScreen
import com.example.navyuga.feature.profile.presentation.ProfileScreen
import com.example.navyuga.navigation.PlaceholderScreen

// --- Custom Colors for the Bar ---
private val NavBackground = Color(0xFF0F172A) // Deep Midnight Blue
private val UnselectedIconColor = Color.White.copy(alpha = 0.6f) // Hollow Gray/Dim White
private val SelectedIconColor = Color.White // Pure White
private val IndicatorColor = Color.Transparent // No background pill

@Composable
fun ArthYugaDashboard(
    rootNavController: NavController,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    // Define the "Hollow" (Unselected) vs "Solid" (Selected) icons
    val items = listOf(
        BottomNavItem(
            label = "Home",
            route = "ay_home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            label = "Search",
            route = "ay_search",
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Outlined.Search
        ),
        BottomNavItem(
            label = "Invest",
            route = "ay_invest",
            // 'Paid' is a great icon for Investment (Circle with Dollar)
            selectedIcon = Icons.Filled.Paid,
            unselectedIcon = Icons.Outlined.Paid
        ),
        BottomNavItem(
            label = "Reels",
            route = "ay_reels",
            // 'SlowMotionVideo' looks very similar to the Instagram Reels icon
            selectedIcon = Icons.Filled.SlowMotionVideo,
            unselectedIcon = Icons.Outlined.SlowMotionVideo
        ),
        BottomNavItem(
            label = "Profile",
            route = "ay_profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = NavBackground,
                contentColor = Color.White,
                tonalElevation = 0.dp // Flat look like Instagram
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    val isSelected = currentRoute == item.route

                    NavigationBarItem(
                        icon = {
                            Icon(
                                // MAGIC: Switch between Hollow and Solid here
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier.padding(bottom = 4.dp) // Slight lift
                            )
                        },
                        // Optional: Hide labels for a cleaner "Instagram" look, or keep them
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SelectedIconColor,
                            selectedTextColor = SelectedIconColor,
                            indicatorColor = IndicatorColor, // Important: Makes the "pill" invisible
                            unselectedIconColor = UnselectedIconColor,
                            unselectedTextColor = UnselectedIconColor
                        ),
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "ay_home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("ay_home") {
                HomeScreen(
                    onNavigateToDetail = { id ->
                        rootNavController.navigate("property_detail/$id")
                    },
                    onNavigateBack = {
                        rootNavController.popBackStack()
                    }
                )
            }
            composable("ay_search") {
                SearchScreen(navController = rootNavController)
            }
            composable("ay_invest") { PlaceholderScreen("Invest (Coming Soon)") }
            composable("ay_reels") { PlaceholderScreen("Reels (Coming Soon)") }
            composable("ay_profile") {
                ProfileScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle,
                    onLogout = onLogout
                )
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)