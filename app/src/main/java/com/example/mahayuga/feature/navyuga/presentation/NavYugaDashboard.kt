package com.example.mahayuga.feature.navyuga.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mahayuga.feature.navyuga.presentation.home.HomeScreen
import com.example.mahayuga.feature.navyuga.presentation.search.SearchResultsScreen
import com.example.mahayuga.feature.navyuga.presentation.search.SearchScreen
import com.example.mahayuga.feature.profile.presentation.ProfileScreen
import com.example.mahayuga.navigation.PlaceholderScreen
import com.example.mahayuga.feature.navyuga.presentation.reels.ReelsScreen
import com.example.mahayuga.feature.navyuga.presentation.trade.TradeScreen

private val NavBackground = Color.Black
private val UnselectedIconColor = Color.White
private val SelectedIconColor = Color(0xFF2979FF)
private val IndicatorColor = Color.Transparent
private val BorderColor = Color.White.copy(alpha = 0.15f)

@Composable
fun NavYugaDashboard(
    rootNavController: NavController,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToMenu: () -> Unit
) {
    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem("Home", "ay_home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("Search", "ay_search", Icons.Filled.Search, Icons.Outlined.Search),

        // ⚡ CHANGED: "Trade" -> "Invest" with TrendingUp Icon (Growth Arrow)
        BottomNavItem(
            "Trade",
            "ay_trade",
            Icons.AutoMirrored.Filled.TrendingUp,
            Icons.AutoMirrored.Outlined.TrendingUp
        ),

        // ⚡ CHANGED: Discovery Icon to Category (Shapes/Grid look)
        BottomNavItem(
            "Discover",
            "ay_reels",
            Icons.Filled.Category,
            Icons.Outlined.Category
        ),

        BottomNavItem("Profile", "ay_profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    Scaffold(
        containerColor = Color.Black,
        bottomBar = {
            Column {
                HorizontalDivider(thickness = 0.5.dp, color = BorderColor)
                NavigationBar(
                    containerColor = NavBackground,
                    contentColor = Color.White,
                    tonalElevation = 0.dp
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    items.forEach { item ->
                        val isSelected =
                            currentRoute == item.route || (item.route == "ay_search" && currentRoute?.startsWith(
                                "search_results"
                            ) == true)
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            },
                            label = { Text(item.label) },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SelectedIconColor,
                                selectedTextColor = SelectedIconColor,
                                indicatorColor = IndicatorColor,
                                unselectedIconColor = UnselectedIconColor,
                                unselectedTextColor = UnselectedIconColor
                            ),
                            onClick = {
                                if (isSelected && currentRoute != item.route) {
                                    navController.popBackStack(item.route, inclusive = false)
                                } else {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
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
                    onNavigateToDetail = { id -> rootNavController.navigate("property_detail/$id") },
                    onNavigateBack = { rootNavController.popBackStack() },
                    onRoiClick = { rootNavController.navigate("roi_calculator") }
                )
            }
            composable("ay_search") {
                SearchScreen(
                    navController = navController,
                    onRoiClick = { rootNavController.navigate("roi_calculator") }
                )
            }
            composable(
                "search_results/{country}/{city}",
                arguments = listOf(
                    navArgument("country") { type = NavType.StringType },
                    navArgument("city") { type = NavType.StringType })
            ) { entry ->
                val country = entry.arguments?.getString("country") ?: "India"
                val city = entry.arguments?.getString("city") ?: "All Cities"
                SearchResultsScreen(
                    country = country,
                    city = city,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { id -> rootNavController.navigate("property_detail/$id") },
                    onRoiClick = { rootNavController.navigate("roi_calculator") })
            }
            composable("ay_trade") { TradeScreen() }
            composable("ay_reels") { ReelsScreen() }
            composable("ay_profile") {
                ProfileScreen(
                    onNavigateToLiked = { rootNavController.navigate("liked_properties") },
                    onNavigateToAccount = { rootNavController.navigate("account_details") },
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToSecurity = onNavigateToSecurity,
                    onNavigateToHelp = onNavigateToHelp,
                    onNavigateToWallet = { rootNavController.navigate("wallet_screen") },
                    onNavigateToMenu = onNavigateToMenu,
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