package com.example.mahayuga.feature.hub.presentation

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mahayuga.R
import com.example.mahayuga.core.common.BiometricAuthenticator
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.profile.presentation.ProfileScreen
import com.example.mahayuga.feature.profile.presentation.ProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 🎨 BRAND COLORS MATCHING IMAGE
private val BackgroundBlack = Color(0xFF050505)
private val CardDarkSurface = Color(0xFF0A0F14) // Very dark blue/black for cards
private val NavyugaBlue = Color(0xFF2979FF)
private val InactiveGrey = Color(0xFF424242)

@Composable
fun HubScreen(
    navController: NavController,
    onLogout: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    // ⚡ INJECT PROFILE VM TO GET USER NAME
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf("Yuga") }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Get User Name
    val currentUserState by profileViewModel.currentUser.collectAsState()
    val userName = if (currentUserState is UiState.Success) {
        (currentUserState as UiState.Success).data.name.ifEmpty { "User" }
    } else "User"

    // Initialize the authenticator
    val biometricAuth = remember { BiometricAuthenticator(context) }

    Scaffold(
        containerColor = BackgroundBlack,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            HubBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (selectedTab == "Yuga") {
                YugaContent(
                    userName = userName,
                    onNavyugaClick = {
                        // ⚡ SECURITY CHECK: Biometrics
                        val activity = context as? FragmentActivity
                        if (activity != null) {
                            biometricAuth.authenticate(
                                activity = activity,
                                title = "Unlock Navyuga",
                                subtitle = "Scan your fingerprint to enter",
                                onSuccess = { navController.navigate("navyuga_dashboard") },
                                onError = { errorMsg ->
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            navController.navigate("navyuga_dashboard")
                        }
                    }
                )
            } else {
                ProfileScreen(
                    onNavigateToLiked = { navController.navigate("liked_properties") },
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
fun YugaContent(
    userName: String,
    onNavyugaClick: () -> Unit
) {
    // ⚡ COROUTINE USAGE: 'rememberCoroutineScope' is used here to manage the entrance animation.
    // We launch a coroutine to delay the appearance of the bottom cards slightly for a smooth effect.
    val scope = rememberCoroutineScope()

    var cardsVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            delay(300) // Wait for main logo to settle slightly
            cardsVisible = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // 1. WELCOME TEXT
        Text(
            text = "Welcome, $userName",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        // 2. CENTER MAHAYUGA LOGO (ROTATING)
        RotatingMandala()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "MAHAYUGA",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        // 3. BOTTOM CARDS (Navyuga & Arthyuga)
        // Animated Visibility for Entrance
        androidx.compose.animation.AnimatedVisibility(
            visible = cardsVisible,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically { 100 }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // NAVYUGA CARD (Active)
                HubCard(
                    title = "Navyuga",
                    subtitle = null,
                    iconRes = R.drawable.navyuga, // Using blue resource
                    isActive = true,
                    onClick = onNavyugaClick,
                    modifier = Modifier.weight(1f)
                )

                // ARTHYUGA CARD (Inactive/Coming Soon)
                HubCard(
                    title = "Arthyuga",
                    subtitle = "COMING SOON",
                    iconRes = R.drawable.arthyuga, // Using white resource tinting it grey
                    isActive = false,
                    onClick = { },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun RotatingMandala() {
    // Infinite rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "mandala_spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Box(contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.mahayuga),
            contentDescription = "Mahayuga",
            modifier = Modifier
                .size(280.dp)
                .rotate(angle)
                .alpha(0.9f)
        )
        // Center Dot/Core
//        Box(
//            modifier = Modifier
//                .size(20.dp)
//                .background(Color.White, androidx.compose.foundation.shape.CircleShape)
//        )
    }
}

@Composable
fun HubCard(
    title: String,
    subtitle: String?,
    @DrawableRes iconRes: Int,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isActive) CardDarkSurface else Color(0xFF080808)
    val contentColor = if (isActive) Color.White else Color.Gray
    val iconTint = if (isActive) Color.Unspecified else Color.DarkGray

    // Glow effect for active card
    val borderBrush = if (isActive) {
        Brush.verticalGradient(listOf(Color.Transparent, NavyugaBlue.copy(alpha = 0.3f)))
    } else {
        Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
    }

    Card(
        modifier = modifier
            .height(180.dp)
            .clickable(enabled = isActive, onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(borderBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .alpha(if (isActive) 1f else 0.3f),
                    colorFilter = if (!isActive) ColorFilter.tint(Color.Gray) else null
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                )

                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = InactiveGrey
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun HubBottomBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = BackgroundBlack,
        contentColor = Color.White,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == "Yuga",
            onClick = { onTabSelected("Yuga") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), // Using app icon geometric shape
                    contentDescription = "Yugas",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Yugas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                indicatorColor = Color.Transparent, // No pill background
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = selectedTab == "Profile",
            onClick = { onTabSelected("Profile") },
            icon = {
                Icon(
                    imageVector = if (selectedTab == "Profile") Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}