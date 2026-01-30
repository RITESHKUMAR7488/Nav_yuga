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
import com.example.mahayuga.feature.auth.presentation.AuthViewModel

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

    // 5 Primary Tabs
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
                val currentRoute = amNavController.currentBackStackEntryFlow.collectAsState(initial = null).value?.destination?.route

                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            amNavController.navigate(item.route) {
                                popUpTo(amNavController.graph.startDestinationId) { saveState = true }
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
            // 1. Portfolio Command Center (The CEO View)
            composable("am_command") {
                PortfolioCommandCentre() // Reusing your existing component
            }

            // 3. Property Operations
            composable("am_ops") {
                com.example.mahayuga.feature.assetmanager.presentation.ops.AssetOperationsScreen()
            }

            // 2. Capital & Investor Intelligence
            composable("am_investors") {
                PlaceholderScreen("Panel 2: Investor Intelligence\n(Coming Phase 3)")
            }

            // 4. Income & Distribution
            composable("am_finance") {
                PlaceholderScreen("Panel 4: Income Engine\n(Coming Phase 2)")
            }

            // Menu for Remaining Panels (5, 6, 7, 8) + Logout
            composable("am_menu") {
                AmMenuScreen(
                    onLogout = {
                        authViewModel.logout()
                        rootNavController.navigate("welcome") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AmMenuScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Advanced Tools", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Spacer(Modifier.height(24.dp))

        AmMenuItem("Risk & Compliance", "Panel 5") {}
        AmMenuItem("Fundraising & Liquidity", "Panel 6") {}
        AmMenuItem("Benchmarking", "Panel 7") {}
        AmMenuItem("AI Insights", "Panel 8") {}

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(0.2f), contentColor = Color.Red),
            modifier = Modifier.fillMaxWidth().height(50.dp)
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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