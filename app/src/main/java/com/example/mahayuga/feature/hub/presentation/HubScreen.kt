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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.mahayuga.feature.profile.presentation.ProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val BackgroundBlack = Color(0xFF050505)
private val CardDarkSurface = Color(0xFF0A0F14)
private val HubGrey = Color(0xFF424242)

@Composable
fun HubScreen(
    navController: NavController,
    onLogout: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToHelp: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf("Yuga") }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val biometricAuth = remember { BiometricAuthenticator(context) }

    Scaffold(
        containerColor = BackgroundBlack,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            HubBottomBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            if (selectedTab == "Yuga") {
                YugaContent(
                    onNavyugaClick = {
                        val activity = context as? FragmentActivity
                        if (activity != null) {
                            biometricAuth.authenticate(
                                activity = activity,
                                title = "Unlock Navyuga",
                                onSuccess = { navController.navigate("navyuga_splash") },
                                onError = { errorMsg ->
                                    Toast.makeText(
                                        context,
                                        errorMsg,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        } else {
                            navController.navigate("navyuga_splash")
                        }
                    }
                )
            } else {
                HubProfileScreen(
                    onLogout = {
                        profileViewModel.logout()
                        onLogout()
                    },
                    onSettings = onNavigateToSettings,
                    onAbout = { /* Handle About */ }
                )
            }
        }
    }
}

@Composable
fun HubProfileScreen(
    onLogout: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(24.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(32.dp))

        HubProfileOptionItem(Icons.Outlined.Info, "About Mahayuga", onClick = onAbout)
        HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 12.dp))

        HubProfileOptionItem(Icons.Outlined.Settings, "Settings", onClick = onSettings)
        HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 12.dp))

        HubProfileOptionItem(Icons.AutoMirrored.Filled.ExitToApp, "Log out", onClick = onLogout, textColor = Color.White)
    }
}

@Composable
fun HubProfileOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    textColor: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.Gray)
    }
}

@Composable
fun YugaContent(onNavyugaClick: () -> Unit) {
    val scope = rememberCoroutineScope()
    var cardsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { scope.launch { delay(300); cardsVisible = true } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        // 1. HEADER ROW (Center Text, Right Icon)
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Title (Center)
            Text(
                text = "MAHAYUGA",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Color.White
                ),
                modifier = Modifier.align(Alignment.Center)
            )

            // Notification Icon (Right)
            IconButton(
                onClick = { /* Notification Action */ },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // 2. CENTERED CARDS (VERTICAL COLUMN)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = cardsVisible,
                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically { 100 }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally // ⚡ Centered items
                ) {
                    HubCard(
                        title = "NAVYUGA",
                        subtitle = "OWNERSHIP FOR ALL",
                        iconRes = R.drawable.navyuga,
                        isActive = true,
                        onClick = onNavyugaClick,
                        modifier = Modifier.fillMaxWidth(0.70f) // ⚡ Reduced Width to 85%
                    )
                    HubCard(
                        title = "ARTHYUGA",
                        subtitle = "COMING SOON",
                        iconRes = R.drawable.arthyuga,
                        isActive = true,
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(0.70f) // ⚡ Reduced Width to 85%
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
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

    val borderBrush = if (isActive) Brush.verticalGradient(
        listOf(
            Color.Transparent,
            Color.Gray.copy(alpha = 0.2f)
        )
    ) else Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))

    Card(
        modifier = modifier
            .height(220.dp)
            .clickable(enabled = isActive, onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(borderBrush)) {
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
                        .size(100.dp)
                        .alpha(if (isActive) 1f else 0.3f),
                    colorFilter = if (!isActive) ColorFilter.tint(Color.Gray) else null
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(4.dp)); Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun HubBottomBar(selectedTab: String, onTabSelected: (String) -> Unit) {
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
                    imageVector = Icons.Default.Dashboard,
                    contentDescription = "Yugas",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Yugas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                indicatorColor = HubGrey,
                unselectedIconColor = Color.White,
                unselectedTextColor = Color.White
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
                indicatorColor = HubGrey,
                unselectedIconColor = Color.White,
                unselectedTextColor = Color.White
            )
        )
    }
}