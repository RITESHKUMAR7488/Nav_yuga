// main/java/com/example/mahayuga/feature/navyuga/presentation/NavYugaDashboard.kt
package com.example.mahayuga.feature.navyuga.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

private val NavyBlue = Color(0xFF080F18)
private val BottomNavBg = Color(0xFF00BFA5)
private val UnselectedIconColor = Color.White.copy(alpha = 0.6f)
private val SelectedIconColor = Color.White

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
        BottomNavItem(
            "Watchlist",
            "ay_watchlist",
            Icons.Filled.Bookmark,
            Icons.Outlined.BookmarkBorder
        ),
        BottomNavItem("Portfolio", "ay_portfolio", Icons.Filled.PieChart, Icons.Outlined.PieChart),
        BottomNavItem("Discover", "ay_discover", Icons.Filled.Explore, Icons.Outlined.Explore),
        BottomNavItem("Profile", "ay_profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    var homeScrollTrigger by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBlue)
    ) {
        NavHost(
            navController = navController,
            startDestination = "ay_home",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("ay_home") {
                HomeScreen(
                    onNavigateToSmReitDetail = { id -> rootNavController.navigate("property_detail/$id") },
                    onNavigateToReitDetail = { id -> rootNavController.navigate("trade_asset_detail/$id") },
                    onNavigateToSearch = { },
                    scrollToTopTrigger = homeScrollTrigger
                )
            }

            // ⚡ FIX: Added Navigation Lambdas to Watchlist Screen
            composable("ay_watchlist") {
                WatchlistScreen(
                    onNavigateToSmReitDetail = { id -> rootNavController.navigate("property_detail/$id") },
                    onNavigateToReitDetail = { id -> rootNavController.navigate("trade_asset_detail/$id") }
                )
            }

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

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .shadow(12.dp, RoundedCornerShape(50))
                    .clip(RoundedCornerShape(50))
                    .background(BottomNavBg),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    val isSelected = currentRoute == item.route
                    val color = if (isSelected) SelectedIconColor else UnselectedIconColor

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    if (isSelected) {
                                        if (item.route == "ay_home") {
                                            homeScrollTrigger = !homeScrollTrigger
                                        }
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
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            tint = color,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            color = color,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
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