package com.example.mahayuga.feature.hub.presentation

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mahayuga.R
import com.example.mahayuga.feature.profile.presentation.ProfileScreen

@Composable
fun HubScreen(
    navController: NavController,
    onLogout: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("Yuga") }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = Color.Black,
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
                    onNavyugaClick = { navController.navigate("navyuga_dashboard") },
                    onOtherClick = { name ->
                        Toast.makeText(context, "$name is Coming Soon!", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                ProfileScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle,
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
fun YugaContent(
    onNavyugaClick: () -> Unit,
    onOtherClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ⚡ CHANGED: Updated Header Text
        Text(
            text = "Welcome to Mahayuga",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(top = 32.dp, bottom = 48.dp)
        )

        // Custom Grid Layout
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(32.dp) // Increased spacing for cleaner look
        ) {
            // Row 1: The 3 Main Yugas (Navyuga, Arthyuga, Grihiyuga)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ⚡ NAVYUGA
                YugaIcon(
                    name = "Navyuga",
                    iconRes = R.drawable.blue,
                    modifier = Modifier.weight(1f),
                    onClick = onNavyugaClick
                )

                // ⚡ ARTHYUGA
                YugaIcon(
                    name = "Arthyuga",
                    iconRes = R.drawable.gold,
                    modifier = Modifier.weight(1f),
                    onClick = { onOtherClick("Arthyuga") }
                )

                // ⚡ GRIHIYUGA
                YugaIcon(
                    name = "Grihiyuga",
                    iconRes = R.drawable.white,
                    modifier = Modifier.weight(1f),
                    onClick = { onOtherClick("Grihiyuga") }
                )
            }

            // Row 2: 3 Coming Soon Items
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                repeat(3) {
                    PlaceholderIcon(Modifier.weight(1f)) { onOtherClick("Module ${it + 4}") }
                }
            }

            // Row 3: 2 Coming Soon Items (Centered)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(0.66f)
                ) {
                    repeat(2) {
                        PlaceholderIcon(Modifier.weight(1f)) { onOtherClick("Module ${it + 7}") }
                    }
                }
            }
        }
    }
}

// ⚡ CHANGED: Renamed to YugaIcon and removed Card/Border styling
@Composable
fun YugaIcon(
    name: String,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        // Just the Icon, no Box/Background
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = name,
            tint = Color.Unspecified, // Keeps the original drawable colors
            modifier = Modifier.size(100.dp) // ⚡ CHANGED: Bigger Icon Size
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White // Always white on black background
        )
    }
}

@Composable
fun PlaceholderIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        // Simple Lock/Placeholder icon for coming soon
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Coming Soon",
            tint = Color.Gray.copy(alpha = 0.5f),
            modifier = Modifier.size(60.dp) // Slightly smaller than main icons
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Soon",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun HubBottomBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.Black,
        contentColor = Color.White,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == "Yuga",
            onClick = { onTabSelected("Yuga") },
            icon = {
                Icon(
                    imageVector = if (selectedTab == "Yuga") Icons.Filled.Apps else Icons.Outlined.Apps,
                    contentDescription = "Yuga"
                )
            },
            label = { Text("Yuga") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                indicatorColor = Color.White.copy(alpha = 0.1f),
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
                indicatorColor = Color.White.copy(alpha = 0.1f),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}