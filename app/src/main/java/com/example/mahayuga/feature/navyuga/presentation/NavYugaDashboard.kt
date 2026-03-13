// main/java/com/example/mahayuga/feature/navyuga/presentation/NavYugaDashboard.kt
package com.example.mahayuga.feature.navyuga.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.mahayuga.feature.navyuga.presentation.portfolio.PortfolioScreen
import com.example.mahayuga.feature.profile.presentation.ProfileScreen
import com.example.mahayuga.feature.navyuga.presentation.discover.DiscoverScreen
import com.example.mahayuga.feature.navyuga.presentation.watchlist.WatchlistScreen

private val NavyBlue = Color(0xFF0F172A)
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

    // ⚡ Phase 1: Replaced Trade and Funds with Watchlist and Discover
    val items = listOf(
        BottomNavItem("Home", "ay_home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("Watchlist", "ay_watchlist", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder),
        BottomNavItem("Portfolio", "ay_portfolio", Icons.Filled.PieChart, Icons.Outlined.PieChart),
        BottomNavItem("Discover", "ay_discover", Icons.Filled.Explore, Icons.Outlined.Explore),
        BottomNavItem("Profile", "ay_profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    var homeScrollTrigger by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = NavyBlue,
        bottomBar = {
            Column {
                HorizontalDivider(thickness = 0.5.dp, color = BorderColor)
                NavigationBar(
                    containerColor = NavyBlue,
                    contentColor = Color.White,
                    tonalElevation = 0.dp
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
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    item.label,
                                    modifier = Modifier.offset(y = (-4).dp)
                                )
                            },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SelectedIconColor,
                                selectedTextColor = SelectedIconColor,
                                indicatorColor = IndicatorColor,
                                unselectedIconColor = UnselectedIconColor,
                                unselectedTextColor = UnselectedIconColor
                            ),
                            onClick = {
                                if (isSelected) {
                                    if (item.route == "ay_home") {
                                        homeScrollTrigger = !homeScrollTrigger
                                    }
                                } else {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
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
            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)
        ) {
            composable("ay_home") {
                HomeScreen(
                    // ⚡ Phase 2: Split navigation targets based on asset type
                    onNavigateToSmReitDetail = { id -> rootNavController.navigate("property_detail/$id") },
                    onNavigateToReitDetail = { id -> rootNavController.navigate("trade_asset_detail/$id") },
                    onNavigateToSearch = { /* Search logic can be attached here later */ },
                    scrollToTopTrigger = homeScrollTrigger
                )
            }

            composable("ay_watchlist") { WatchlistScreen() }
            composable("ay_portfolio") { PortfolioScreen() }
            composable("ay_discover") { DiscoverScreen() }

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