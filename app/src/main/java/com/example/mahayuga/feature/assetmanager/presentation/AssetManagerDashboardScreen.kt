// main/java/com/example/mahayuga/feature/assetmanager/presentation/AssetManagerDashboardScreen.kt
package com.example.mahayuga.feature.assetmanager.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mahayuga.core.common.PlaceholderScreen
import com.example.mahayuga.feature.assetmanager.presentation.benchmark.BenchmarkingScreen
import com.example.mahayuga.feature.assetmanager.presentation.compliance.ComplianceScreen
import com.example.mahayuga.feature.auth.presentation.AuthViewModel
import com.example.mahayuga.feature.assetmanager.presentation.investors.InvestorScreen
import com.example.mahayuga.feature.assetmanager.presentation.ops.AssetOperationsScreen
import com.example.mahayuga.feature.assetmanager.presentation.finance.FinanceScreen
import com.example.mahayuga.feature.assetmanager.presentation.risk.RiskScreen
import com.example.mahayuga.feature.assetmanager.presentation.fundraising.FundraisingScreen
import com.example.mahayuga.feature.assetmanager.presentation.listings.AmListingsScreen
import com.example.mahayuga.feature.assetmanager.presentation.posts.AmPostsScreen
import com.example.mahayuga.feature.assetmanager.presentation.posts.AmPostDetailScreen
import com.example.mahayuga.feature.assetmanager.presentation.posts.AmPostsViewModel

// --- THEME ---
private val AmBackground = Color(0xFF061123)
private val AmSurface = Color(0xFF111c30)
private val AmAccent = Color(0xFF38a882)

// --- METALLIC SINGULAR THEME ---
private val MetallicStart = Color(0xFF232D3F)
private val MetallicEnd = Color(0xFF161C27)
private val MetallicBorder = Color(0xFF37475A)

@Composable
fun AssetManagerDashboardScreen(
    rootNavController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val amNavController = rememberNavController()
    // Hoisted ViewModel so data persists when switching bottom nav tabs
    val amPostsViewModel: AmPostsViewModel = hiltViewModel()

    val navItems = listOf(
        AmNavItem("Dashboard", "am_grid", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
        AmNavItem("Assets", "am_ops", Icons.Filled.Apartment, Icons.Outlined.Apartment),
        AmNavItem("Post", "am_post", Icons.Filled.PostAdd, Icons.Outlined.PostAdd),
        // Safe Core Icons
        AmNavItem("Compliance", "am_compliance", Icons.Default.Lock, Icons.Default.Lock),
        AmNavItem("Profile", "am_menu", Icons.Filled.Person, Icons.Outlined.Person)
    )

    Scaffold(
        containerColor = AmBackground,
        bottomBar = {
            NavigationBar(
                containerColor = AmSurface,
                contentColor = Color.White
            ) {
                val navBackStackEntry by amNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route ||
                            (item.route == "am_grid" && currentRoute == "am_command") ||
                            (item.route == "am_post" && currentRoute?.startsWith("am_post_detail") == true)

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
            startDestination = "am_grid",
            modifier = Modifier.padding(padding)
        ) {
            composable("am_grid") {
                AssetManagerGridHub(onNavigateToModule = { route -> amNavController.navigate(route) })
            }
            composable("am_command") { PortfolioCommandCentre() }
            composable("am_ops") { AssetOperationsScreen() }
            composable("am_investors") { InvestorScreen() }
            composable("am_finance") { FinanceScreen() }

            // COMPLIANCE SCREEN
            composable("am_compliance") {
                ComplianceScreen(
                    onNavigateBack = { amNavController.popBackStack() },
                    onComplianceClick = { complianceId ->
                        // Handle specific compliance item click if needed
                    }
                )
            }

            // --- POSTS SECTION ---
            composable("am_post") {
                AmPostsScreen(
                    viewModel = amPostsViewModel,
                    onPostClick = { postId ->
                        amNavController.navigate("am_post_detail/$postId")
                    },
                    onCreateVideoClick = {
                        rootNavController.navigate("add_property")
                    }
                )
            }

            composable(
                route = "am_post_detail/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                AmPostDetailScreen(
                    viewModel = amPostsViewModel,
                    initialPostId = postId,
                    onNavigateBack = { amNavController.popBackStack() }
                )
            }

            // ⚡ FIX: Calling the new AmProfileScreen with all required parameters
            composable("am_menu") {
                com.example.mahayuga.feature.assetmanager.presentation.profile.AmProfileScreen(
                    onNavigateBack = { amNavController.popBackStack() },
                    onNavigateToListings = { amNavController.navigate("am_listings") },
                    onLogout = {
                        authViewModel.logout()
                        rootNavController.navigate("welcome") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable("am_listings") { AmListingsScreen(onBackClick = { amNavController.popBackStack() }) }
            composable("am_risk") { RiskScreen() }
            composable("am_fundraising") { FundraisingScreen() }
            composable("am_benchmark") { BenchmarkingScreen() }
            composable("am_ai") { PlaceholderScreen("Panel 8: AI Insights\n(Coming Tier 3)") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetManagerGridHub(onNavigateToModule: (String) -> Unit) {
    val modules = listOf(
        DashboardGridItem("Portfolio Command Centre", Icons.Rounded.PieChart, "am_command"),
        DashboardGridItem(
            "Capital & Investor Intelligence",
            Icons.Rounded.Insights,
            "am_investors"
        ),
        DashboardGridItem(
            "Property Operations & Maintenance",
            Icons.Rounded.HomeRepairService,
            "am_ops"
        ),
        DashboardGridItem(
            "Income, Cashflow & Distribution",
            Icons.Rounded.AccountBalance,
            "am_finance"
        ),
        DashboardGridItem("Risk, Compliance & Trust Layer", Icons.Rounded.Security, "am_risk"),
        DashboardGridItem(
            "Fundraising & Liquidity Intelligence",
            Icons.Rounded.WaterfallChart,
            "am_fundraising"
        ),
        DashboardGridItem("Performance Benchmarking", Icons.Rounded.Leaderboard, "am_benchmark"),
        DashboardGridItem("AI Insights & Decision Engine", Icons.Rounded.AutoAwesome, "am_ai")
    )

    val metallicBrush = Brush.linearGradient(
        colors = listOf(MetallicStart, MetallicEnd)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.Dashboard,
                    contentDescription = "Dashboard",
                    tint = AmAccent,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Dashboard",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Rounded.Search, contentDescription = "Search", tint = Color.White)

                BadgedBox(
                    badge = { Badge(containerColor = Color.Red) { Text("3") } }
                ) {
                    Icon(
                        Icons.Rounded.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White
                    )
                }

                BadgedBox(
                    badge = { Badge(containerColor = AmAccent) { Text("1") } }
                ) {
                    Icon(
                        Icons.Rounded.MailOutline,
                        contentDescription = "Messages",
                        tint = Color.White
                    )
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(modules) { module ->
                var isPressed by remember { mutableStateOf(false) }
                val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "scale")

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .scale(scale)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, MetallicBorder, RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onNavigateToModule(module.route)
                        }
                        .pointerInput(Unit) {
                            while (true) {
                                awaitPointerEventScope {
                                    awaitFirstDown(false)
                                    isPressed = true
                                    waitForUpOrCancellation()
                                    isPressed = false
                                }
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(metallicBrush)
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.05f),
                                modifier = Modifier
                                    .size(40.dp)
                                    .border(
                                        0.5.dp,
                                        Color.White.copy(alpha = 0.1f),
                                        RoundedCornerShape(10.dp)
                                    )
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        module.icon,
                                        contentDescription = null,
                                        tint = AmAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Text(
                                text = module.title,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

data class DashboardGridItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

data class AmNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)