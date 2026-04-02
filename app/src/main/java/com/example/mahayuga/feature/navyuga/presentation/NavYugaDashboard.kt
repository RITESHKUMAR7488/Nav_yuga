// main/java/com/example/mahayuga/feature/navyuga/presentation/NavYugaDashboard.kt
package com.example.mahayuga.feature.navyuga.presentation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
private val UnselectedIconColor = Color.White.copy(alpha = 0.6f)
private val SelectedIconColor = Color(0xFF14B8A6)

// Restored your exact opacity and colors
private val FloatingNavBg = Color(0xFF0F1722).copy(alpha = 0.95f)
private val SelectedOvalBg = Color(0xFF000000).copy(alpha = 0.4f)

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

    Box(modifier = Modifier
        .fillMaxSize()
        .background(NavyBlue)) {
        NavHost(
            navController = navController,
            startDestination = "ay_home",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("ay_home") {
                HomeScreen(
                    onNavigateToSmReitDetail = { id -> rootNavController.navigate("property_detail/$id") },
                    onNavigateToReitDetail = { id -> rootNavController.navigate("trade_asset_detail/$id") },
                    onNavigateToSearch = { rootNavController.navigate("search_screen") },
                    onNavigateToNotifications = { rootNavController.navigate("notifications_screen") },
                    onNavigateToMessages = { rootNavController.navigate("messages_screen") },
                    scrollToTopTrigger = homeScrollTrigger
                )
            }
            composable("ay_watchlist") {
                WatchlistScreen(
                    onNavigateToSmReitDetail = { id -> rootNavController.navigate("property_detail/$id") },
                    onNavigateToReitDetail = { id -> rootNavController.navigate("trade_asset_detail/$id") },
                    onNavigateToSearch = { rootNavController.navigate("search_screen") }
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
                    onNavigateToAbout = { rootNavController.navigate("about_navyuga") },
                    onNavigateToMenu = onNavigateToMenu,
                    onLogout = onLogout
                )
            }
        }

        // Restored your exact shape, padding, shadow, and borders
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(50))
                .background(FloatingNavBg, RoundedCornerShape(50))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(50))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    val isSelected = currentRoute == item.route
                    val color = if (isSelected) SelectedIconColor else UnselectedIconColor

                    // Food delivery app style spring animation for icon scaling
                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "NavIconScale"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    if (isSelected) {
                                        if (item.route == "ay_home") homeScrollTrigger =
                                            !homeScrollTrigger
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
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) SelectedOvalBg else Color.Transparent)
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = color,
                                modifier = Modifier
                                    .size(24.dp)
                                    .scale(iconScale) // Applied the spring animation here
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
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