// main/java/com/example/mahayuga/feature/assetmanager/presentation/AssetManagerDashboardScreen.kt
package com.example.mahayuga.feature.assetmanager.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mahayuga.core.common.PlaceholderScreen
import com.example.mahayuga.feature.assetmanager.presentation.benchmark.BenchmarkingScreen
import com.example.mahayuga.feature.auth.presentation.AuthViewModel
import com.example.mahayuga.feature.assetmanager.presentation.investors.InvestorScreen
import com.example.mahayuga.feature.assetmanager.presentation.ops.AssetOperationsScreen
import com.example.mahayuga.feature.assetmanager.presentation.finance.FinanceScreen
import com.example.mahayuga.feature.assetmanager.presentation.risk.RiskScreen
import com.example.mahayuga.feature.assetmanager.presentation.fundraising.FundraisingScreen
import com.example.mahayuga.feature.assetmanager.presentation.listings.AmListingsScreen // ⚡ NEW IMPORT

// --- THEME ---
private val AmBackground = Color(0xFF061123)
private val AmSurface = Color(0xFF111c30)
private val AmAccent = Color(0xFF38a882)

@Composable
fun AssetManagerDashboardScreen(
    rootNavController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val amNavController = rememberNavController()

    val navItems = listOf(
        AmNavItem("Command", "am_command", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
        AmNavItem("Assets", "am_ops", Icons.Filled.Apartment, Icons.Outlined.Apartment),
        AmNavItem("Investors", "am_investors", Icons.Filled.Group, Icons.Outlined.Group),
        AmNavItem("Finance", "am_finance", Icons.Filled.Payments, Icons.Outlined.Payments),
        AmNavItem("More", "am_menu", Icons.Filled.Menu, Icons.Outlined.Menu)
    )

    Scaffold(
        containerColor = AmBackground,
        bottomBar = {
            NavigationBar(
                containerColor = AmSurface,
                contentColor = Color.White
            ) {
                val currentRoute =
                    amNavController.currentBackStackEntryFlow.collectAsState(initial = null).value?.destination?.route

                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            amNavController.navigate(item.route) {
                                popUpTo(amNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AmAccent,
                            selectedTextColor = AmAccent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = amNavController,
            startDestination = "am_command",
            modifier = Modifier.padding(padding)
        ) {
            composable("am_command") { PortfolioCommandCentre() }
            composable("am_ops") { AssetOperationsScreen() }
            composable("am_investors") { InvestorScreen() }
            composable("am_finance") { FinanceScreen() }
            composable("am_menu") {
                AmMenuScreen(
                    rootNavController = rootNavController,
                    onNavigate = { route -> amNavController.navigate(route) },
                    onLogout = {
                        authViewModel.logout()
                        rootNavController.navigate("welcome") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            // ⚡ PHASE 2 ROUTE ADDED HERE
            composable("am_listings") {
                AmListingsScreen(onBackClick = { amNavController.popBackStack() })
            }
            composable("am_risk") { RiskScreen() }
            composable("am_fundraising") { FundraisingScreen() }
            composable("am_benchmark") { BenchmarkingScreen() }
            composable("am_ai") { PlaceholderScreen("Panel 8: AI Insights\n(Coming Tier 3)") }
        }
    }
}

@Composable
fun AmMenuScreen(
    rootNavController: NavController,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Advanced Tools", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Spacer(Modifier.height(24.dp))

        AmMenuItem("List New Property", "Submit asset for admin review") {
            rootNavController.navigate("add_property")
        }
        // ⚡ PHASE 2 NAVIGATION LINKED
        AmMenuItem("My Listings", "Track pending and live properties") {
            onNavigate("am_listings")
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = Color.White.copy(0.1f))
        Spacer(Modifier.height(16.dp))

        AmMenuItem("Risk & Compliance", "Panel 5") { onNavigate("am_risk") }
        AmMenuItem("Fundraising & Liquidity", "Panel 6") { onNavigate("am_fundraising") }
        AmMenuItem("Benchmarking", "Panel 7") { onNavigate("am_benchmark") }
        AmMenuItem("AI Insights", "Panel 8") { onNavigate("am_ai") }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red.copy(0.2f),
                contentColor = Color.Red
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.ExitToApp, null)
            Spacer(Modifier.width(8.dp))
            Text("Logout")
        }
    }
}

@Composable
fun AmMenuItem(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = AmSurface)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}

data class AmNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)