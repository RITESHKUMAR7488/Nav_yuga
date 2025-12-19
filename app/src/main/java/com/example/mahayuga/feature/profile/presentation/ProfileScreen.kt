package com.example.mahayuga.feature.profile.presentation

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Book // Import Book icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.profile.data.model.ProfileStat
import kotlinx.coroutines.launch

// --- COLORS ---
private val DrawerBg = Color(0xFF050505)       // Deep Black for Drawer
private val DrawerCardBg = Color(0xFF121212)   // Card inside Drawer
private val BrandBlue = Color(0xFF2979FF)
private val TextWhite = Color(0xFFFFFFFF)
private val TextGrey = Color(0xFF888888)
private val ProgressGreen = Color(0xFF00E676)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToLiked: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUserState by viewModel.currentUser.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val ownedProperties by viewModel.ownedProperties.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // ⚡ FIX: Open drawer automatically if returning from a screen
    LaunchedEffect(Unit) {
        if (viewModel.shouldOpenDrawerOnReturn) {
            drawerState.open()
            viewModel.shouldOpenDrawerOnReturn = false // Reset flag
        }
    }

    val userName = if (currentUserState is UiState.Success) {
        (currentUserState as UiState.Success).data.name.ifEmpty { "User" }
    } else "User"

    val userEmail = if (currentUserState is UiState.Success) {
        (currentUserState as UiState.Success).data.email
    } else ""

    val userInitials = if (userName.isNotEmpty()) userName.take(2).uppercase() else "YO"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = DrawerBg,
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                // ============================================
                // ⚡ NEW "STAKE-STYLE" DRAWER CONTENT
                // ============================================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                        .systemBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // 1. DRAWER HEADER
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* Navigate to Personal Details */ },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1A2835)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userInitials,
                                color = BrandBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Your account details",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGrey
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = TextGrey,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // 2. PROGRESS CARD
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DrawerCardBg),
                        border = BorderStroke(1.dp, Color(0xFF1F2B36))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(40.dp)) {
                                CircularProgressIndicator(
                                    progress = { 0.5f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = BrandBlue,
                                    trackColor = Color.White.copy(alpha = 0.1f),
                                    strokeWidth = 3.dp
                                )
                                Text("2/4", style = MaterialTheme.typography.labelSmall, color = BrandBlue, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("You're halfway there!", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text("Complete verification", style = MaterialTheme.typography.bodySmall, color = TextGrey)
                            }
                        }
                    }

                    // 3. SETTINGS GROUPS
                    SettingsGroup {
                        // ⚡ FIX: Set flag before navigating to keep drawer open on return
                        DrawerItem(
                            icon = Icons.Outlined.FavoriteBorder,
                            title = "Liked Properties",
                            onClick = {
                                scope.launch { drawerState.close() }
                                viewModel.shouldOpenDrawerOnReturn = true // Set flag
                                onNavigateToLiked()
                            }
                        )
                        DrawerItem(icon = Icons.Outlined.AccountBalanceWallet, title = "Wallet", onClick = { Toast.makeText(context, "Wallet Coming Soon", Toast.LENGTH_SHORT).show() })
                        DrawerItem(icon = Icons.Outlined.Settings, title = "Settings", onClick = { Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show() })
                        DrawerItem(icon = Icons.Outlined.Lock, title = "Security & privacy", onClick = { })
                    }

                    SettingsGroup {
                        DrawerItem(icon = Icons.Outlined.Info, title = "About Navyuga", onClick = { })
                        DrawerItem(icon = Icons.Outlined.HelpOutline, title = "Help center", onClick = { })
                        DrawerItem(icon = Icons.Outlined.Description, title = "Documents", badge = "NEW", onClick = { })
                        // ⚡ ADDED: 10 Step Guide
                        DrawerItem(icon = Icons.Rounded.Book, title = "10 Step Guide", onClick = { Toast.makeText(context, "Guide Coming Soon", Toast.LENGTH_SHORT).show() })
                    }

                    SettingsGroup {
                        DrawerItem(icon = Icons.Outlined.CardGiftcard, title = "Refer a friend", onClick = { })
                        DrawerItem(
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            title = "Log out",
                            textColor = Color(0xFFFF5252),
                            iconColor = Color(0xFFFF5252),
                            showChevron = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                viewModel.logout()
                                onLogout()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    ) {
        // ============================================
        // ORIGINAL DASHBOARD CONTENT (Unchanged)
        // ============================================
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // HEADER
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

                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                        ) {
                            Icon(Icons.Default.Menu, "Menu", modifier = Modifier.size(28.dp))
                        }
                    }
                }

                // STATS
                item {
                    Text(
                        text = "Your Real Estate Portfolio",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )

                    val displayStats = if (stats.size >= 6) stats else List(6) {
                        ProfileStat("Coming Soon", "-", 0f, 0xFF888888)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CompactStatCard(displayStats[0], Modifier.weight(1f))
                            CompactStatCard(displayStats[1], Modifier.weight(1f))
                            CompactStatCard(displayStats[2], Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CompactStatCard(displayStats[3], Modifier.weight(1f))
                            CompactStatCard(displayStats[4], Modifier.weight(1f))
                            CompactStatCard(displayStats[5], Modifier.weight(1f))
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }

                // PROPERTIES
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
                        ProfilePropertyCard(property = property, onLikeClick = {}, onShareClick = {})
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

// --- REUSABLE COMPONENTS ---

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DrawerCardBg)
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            content()
        }
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    title: String,
    badge: String? = null,
    textColor: Color = TextWhite,
    iconColor: Color = TextWhite,
    showChevron: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        if (badge != null) {
            Surface(
                color = Color(0xFF1B5E20),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = badge,
                    color = Color(0xFF69F0AE),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

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