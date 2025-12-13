package com.example.mahayuga.feature.navyuga.presentation

import androidx.compose.foundation.layout.Column
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
import com.example.mahayuga.feature.navyuga.presentation.home.HomeScreen
import com.example.mahayuga.feature.navyuga.presentation.search.SearchScreen
import com.example.mahayuga.feature.profile.presentation.ProfileScreen
import com.example.mahayuga.navigation.PlaceholderScreen

// --- Custom Colors for the Instagram Look ---
private val NavBackground = Color.Black // Distinct from app background
private val UnselectedIconColor = Color.White.copy(alpha = 0.6f)
private val SelectedIconColor = Color.White
private val IndicatorColor = Color.Transparent
private val BorderColor = Color.White.copy(alpha = 0.15f) // Faint whitish border

@Composable
fun NavYugaDashboard(
    rootNavController: NavController,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem("Home", "ay_home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("Search", "ay_search", Icons.Filled.Search, Icons.Outlined.Search),
        BottomNavItem("Invest", "ay_invest", Icons.Filled.Paid, Icons.Outlined.Paid),
        BottomNavItem("Reels", "ay_reels", Icons.Filled.SlowMotionVideo, Icons.Outlined.SlowMotionVideo),
        BottomNavItem("Profile", "ay_profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    Scaffold(
        bottomBar = {
            // Column to stack Border + Navbar
            Column {
                HorizontalDivider(thickness = 0.5.dp, color = BorderColor) // The faint border

                NavigationBar(
                    containerColor = NavBackground,
                    contentColor = Color.White,
                    tonalElevation = 0.dp,
                    modifier = Modifier.padding(top = 0.dp)
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    items.forEach { item ->
                        val isSelected = currentRoute == item.route

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    modifier = Modifier.padding(vertical = 4.dp) // Center icon vertically
                                )
                            },
                            // REMOVED LABEL for Instagram look
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SelectedIconColor,
                                selectedTextColor = SelectedIconColor,
                                indicatorColor = IndicatorColor,
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
                    },
                    // WIRED UP ROI BUTTON
                    onRoiClick = {
                        rootNavController.navigate("roi_calculator")
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