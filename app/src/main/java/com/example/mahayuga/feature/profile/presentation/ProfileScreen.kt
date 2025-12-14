package com.example.mahayuga.feature.profile.presentation

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.profile.data.model.ProfileStat
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToLiked: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUserState by viewModel.currentUser.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val ownedProperties by viewModel.ownedProperties.collectAsState()

    // Drawer State
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // ⚡ COROUTINE USAGE: 'rememberCoroutineScope' creates a CoroutineScope bound to this Composable's lifecycle.
    // We need this because 'drawerState.open()' and 'drawerState.close()' are suspend functions
    // (animations) that must be called from a coroutine, not the main UI thread directly.
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val userName = if (currentUserState is UiState.Success) {
        (currentUserState as UiState.Success).data.name.ifEmpty { "User" }
    } else "User"

    val userEmail = if (currentUserState is UiState.Success) {
        (currentUserState as UiState.Success).data.email.ifEmpty { "user@example.com" }
    } else "Loading..."

    // ⚡ WRAP CONTENT IN MODAL NAVIGATION DRAWER
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(300.dp)
            ) {
                // --- DRAWER HEADER ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        // ⚡ CHANGED: Replaced Gradient with Solid Brand Blue
                        .background(color = Color(0xFF2979FF)),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // Profile Picture Placeholder
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // --- DRAWER ITEMS ---
                NavigationDrawerItem(
                    label = { Text("Liked Properties") },
                    icon = { Icon(Icons.Rounded.Favorite, contentDescription = null) },
                    selected = false,
                    onClick = {
                        // ⚡ COROUTINE USAGE: Launching a coroutine to close the drawer smoothly.
                        // Without 'scope.launch', we cannot call the suspend function 'close()'.
                        scope.launch { drawerState.close() }
                        onNavigateToLiked()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Wallet") },
                    icon = { Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        Toast.makeText(context, "Wallet Coming Soon", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("10 Step Guide") },
                    icon = { Icon(Icons.Rounded.Book, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        Toast.makeText(context, "Guide Coming Soon", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Log Out") },
                    icon = { Icon(Icons.Rounded.Logout, contentDescription = null) },
                    selected = false,
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedTextColor = MaterialTheme.colorScheme.error,
                        unselectedIconColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.logout()
                        onLogout()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        // --- MAIN SCREEN CONTENT ---
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // 1. HEADER WITH MENU BUTTON
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 12.dp, top = 24.dp, bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Hello,",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        // ⚡ Menu Button to Open Drawer
                        IconButton(
                            onClick = {
                                // ⚡ COROUTINE USAGE: Launching a coroutine to open the drawer.
                                // The animation requires suspension, which is why we use a scope.
                                scope.launch { drawerState.open() }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // 2. STATS GRID (3x2 Layout)
                item {
                    Text(
                        text = "Your Real Estate Portfolio",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )

                    // Ensure we have 6 items or use placeholders
                    val displayStats = if (stats.size >= 6) stats else List(6) {
                        ProfileStat("Coming Soon", "-", 0f, 0xFF888888)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Row 1
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CompactStatCard(displayStats[0], Modifier.weight(1f)) // Properties
                            CompactStatCard(displayStats[1], Modifier.weight(1f)) // ROI
                            CompactStatCard(displayStats[2], Modifier.weight(1f)) // Rent
                        }
                        // Row 2
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CompactStatCard(displayStats[3], Modifier.weight(1f)) // Area
                            CompactStatCard(displayStats[4], Modifier.weight(1f)) // Coming Soon
                            CompactStatCard(displayStats[5], Modifier.weight(1f)) // Coming Soon
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }

                // 3. YOUR PROPERTY SECTION (Only if properties exist)
                if (ownedProperties.isNotEmpty()) {
                    item {
                        Text(
                            text = "Your Property",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                        )
                    }

                    items(ownedProperties) { property ->
                        ProfilePropertyCard(
                            property = property,
                            onLikeClick = { /* Logic handled in main VM usually */ },
                            onShareClick = { /* Share Logic */ }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

// ⚡ Refactored Card to fit 3 in a row
@Composable
fun CompactStatCard(stat: ProfileStat, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, Color(0xFF2A3441))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(50.dp).padding(bottom = 8.dp)
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF2A3441),
                    strokeWidth = 4.dp,
                    trackColor = Color.Transparent,
                )
                CircularProgressIndicator(
                    progress = { stat.progress },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(stat.colorHex),
                    strokeWidth = 4.dp,
                    strokeCap = StrokeCap.Round,
                    trackColor = Color.Transparent
                )
            }
            Text(
                text = stat.title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = stat.value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun ProfilePropertyCard(
    property: PropertyModel,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val scale by animateFloatAsState(if (property.isLiked) 1.2f else 1.0f, label = "like")

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = property.mainImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.Gray)
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(property.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    Text(property.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp).height(200.dp).clip(RoundedCornerShape(16.dp)).background(Color.LightGray)) {
                AsyncImage(model = property.mainImage, contentDescription = "Property Image", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
            Row(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp, start = 16.dp, end = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                ProfilePropertyStat("Price", "₹${property.minInvest}")
                ProfilePropertyStat("Return", property.rentReturn.ifEmpty { "₹15k" })
                ProfilePropertyStat("ROI", "${property.roi}%", true)
            }
        }
    }
}

@Composable
fun ProfilePropertyStat(label: String, value: String, isHighlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = if (isHighlight) Color(0xFF4ADE80) else MaterialTheme.colorScheme.onSurface)
    }
}