package com.example.navyuga.feature.arthyuga.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.navyuga.feature.arthyuga.presentation.home.HomeScreen
import com.example.navyuga.feature.arthyuga.presentation.search.SearchScreen
import com.example.navyuga.navigation.PlaceholderScreen
import com.example.navyuga.ui.theme.*

@Composable
fun ArthYugaDashboard(rootNavController: NavController) {
    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem("Home", "ay_home", Icons.Default.Home),
        BottomNavItem("Search", "ay_search", Icons.Default.Search),
        BottomNavItem("Invest", "ay_invest", Icons.Default.AttachMoney),
        BottomNavItem("Reels", "ay_reels", Icons.Default.Movie),
        BottomNavItem("Profile", "ay_profile", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                // ⚡ HomeScreen needs to accept this lambda
                HomeScreen(onPropertyClick = { id ->
                    rootNavController.navigate("property_detail/$id")
                })
            }
            composable("ay_search") {
                SearchScreen(navController = rootNavController)
            }
            composable("ay_invest") { PlaceholderScreen("Invest (Coming Soon)") }
            composable("ay_reels") { PlaceholderScreen("Reels (Coming Soon)") }
            composable("ay_profile") { PlaceholderScreen("Profile (Coming Soon)") }
        }
    }
}

data class BottomNavItem(val label: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Preview
@Composable
fun ArthYugaDashboardPreview() {
    NavyugaTheme {
        // ⚡ FIX: Pass a fake controller for preview
        ArthYugaDashboard(rootNavController = rememberNavController())
    }
}