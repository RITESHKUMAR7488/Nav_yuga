package com.example.mahayuga.feature.navyuga.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mahayuga.core.common.BiometricAuthenticator
import com.example.mahayuga.feature.navyuga.presentation.home.HomeScreen
import com.example.mahayuga.feature.navyuga.presentation.search.SearchResultsScreen
import com.example.mahayuga.feature.navyuga.presentation.search.SearchScreen
import com.example.mahayuga.feature.profile.presentation.ProfileScreen
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
    val context = LocalContext.current

    // ⚡ 6. Biometric Check on App Launch
    var isAuthenticated by remember { mutableStateOf(false) }
    val biometricAuth = remember { BiometricAuthenticator(context) }

    LaunchedEffect(Unit) {
        val activity = context as? FragmentActivity
        if (activity != null) {
            biometricAuth.authenticate(
                activity = activity,
                title = "Unlock Navyuga",
                onSuccess = { isAuthenticated = true },
                onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
            )
        } else {
            isAuthenticated = true
        }
    }

    if (!isAuthenticated) {
        // Show blank or lock screen until authenticated
        Scaffold(containerColor = Color.Black) { Box(Modifier.padding(it)) }
        return
    }

    val items = listOf(
        // ⚡ 5. Renamed Home -> Invest
        BottomNavItem("Invest", "ay_home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("Search", "ay_search", Icons.Filled.Search, Icons.Outlined.Search),
        // Keep Trade center
        BottomNavItem("Trade", "ay_trade", Icons.AutoMirrored.Filled.TrendingUp, Icons.AutoMirrored.Outlined.TrendingUp),
        BottomNavItem("Discover", "ay_reels", Icons.Filled.Category, Icons.Outlined.Category),
        BottomNavItem("Profile", "ay_profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    // ⚡ 3. State to trigger scroll up
    var homeScrollTrigger by remember { mutableStateOf(false) }

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
                        val isSelected = currentRoute == item.route ||
                                (item.route == "ay_search" && currentRoute?.startsWith("search_results") == true)

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    // ⚡ 9. Closer Icon/Text (Reduced vertical padding)
                                    modifier = Modifier.size(24.dp)
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
                                if (isSelected) {
                                    // ⚡ 3. Scroll to Top logic
                                    if (item.route == "ay_home") {
                                        homeScrollTrigger = !homeScrollTrigger // Toggle to trigger LaunchedEffect
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
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable("ay_home") {
                HomeScreen(
                    onNavigateToDetail = { id -> rootNavController.navigate("property_detail/$id") },
                    onNavigateBack = { rootNavController.popBackStack() },
                    onRoiClick = { rootNavController.navigate("roi_calculator") },
                    scrollToTopTrigger = homeScrollTrigger // Pass trigger
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