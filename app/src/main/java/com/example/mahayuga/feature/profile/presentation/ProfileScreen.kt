package com.example.mahayuga.feature.profile.presentation

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.navyuga.presentation.home.InstagramStylePropertyCard
import com.example.mahayuga.feature.profile.data.model.ProfileStat
import kotlinx.coroutines.launch

private val DrawerBg = Color(0xFF050505)
private val DrawerCardBg = Color(0xFF121212)
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

    LaunchedEffect(Unit) {
        if (viewModel.shouldOpenDrawerOnReturn) {
            drawerState.open()
            viewModel.shouldOpenDrawerOnReturn = false
        }
    }

    val userName = if (currentUserState is UiState.Success) {
        (currentUserState as UiState.Success).data.name.ifEmpty { "User" }
    } else "User"
    val userInitials = if (userName.isNotEmpty()) userName.take(2).uppercase() else "YO"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = DrawerBg,
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                        .systemBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1A2835)), contentAlignment = Alignment.Center
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
                            ); Text(
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
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(40.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = { 0.5f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = ProgressGreen,
                                    trackColor = Color.White.copy(alpha = 0.1f),
                                    strokeWidth = 3.dp
                                ); Text(
                                "2/4",
                                style = MaterialTheme.typography.labelSmall,
                                color = ProgressGreen,
                                fontWeight = FontWeight.Bold
                            )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "You're halfway there!",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                ); Text(
                                "Complete verification",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGrey
                            )
                            }
                        }
                    }
                    SettingsGroup {
                        DrawerItem(
                            icon = Icons.Outlined.FavoriteBorder,
                            title = "Liked Properties",
                            onClick = {
                                scope.launch { drawerState.close() }; viewModel.shouldOpenDrawerOnReturn =
                                true; onNavigateToLiked()
                            })
                        DrawerItem(
                            icon = Icons.Outlined.AccountBalanceWallet,
                            title = "Wallet",
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Wallet Coming Soon",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                        DrawerItem(
                            icon = Icons.Outlined.Settings,
                            title = "Settings",
                            onClick = {
                                Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show()
                            })
                        DrawerItem(
                            icon = Icons.Outlined.Lock,
                            title = "Security & privacy",
                            onClick = { })
                    }
                    SettingsGroup {
                        DrawerItem(
                            icon = Icons.Outlined.Info,
                            title = "About Navyuga",
                            onClick = { })
                        DrawerItem(
                            icon = Icons.Outlined.HelpOutline,
                            title = "Help center",
                            onClick = { })
                        DrawerItem(
                            icon = Icons.Outlined.Description,
                            title = "Documents",
                            badge = "NEW",
                            onClick = { })
                        DrawerItem(
                            icon = Icons.Rounded.Book,
                            title = "10 Step Guide",
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Guide Coming Soon",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                    }
                    SettingsGroup {
                        DrawerItem(
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            title = "Log out",
                            textColor = Color(0xFFFF5252),
                            iconColor = Color(0xFFFF5252),
                            showChevron = false,
                            onClick = { scope.launch { drawerState.close() }; viewModel.logout(); onLogout() })
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    ) {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 12.dp, top = 24.dp, bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "User's Portfolio",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                "Menu",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
                item {
                    val displayStats = if (stats.size >= 6) stats else List(6) {
                        ProfileStat(
                            "Loading",
                            "-",
                            0f,
                            0xFF888888
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CompactStatCard(
                                displayStats[0],
                                Modifier.weight(1f)
                            ); CompactStatCard(
                            displayStats[1],
                            Modifier.weight(1f)
                        ); CompactStatCard(
                            displayStats[2],
                            Modifier.weight(1f)
                        )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CompactStatCard(
                                displayStats[3],
                                Modifier.weight(1f)
                            ); CompactStatCard(
                            displayStats[4],
                            Modifier.weight(1f)
                        ); CompactStatCard(
                            displayStats[5],
                            Modifier.weight(1f)
                        )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
                if (ownedProperties.isNotEmpty()) {
                    item {
                        Text(
                            "Your Property",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                        )
                    }
                    items(ownedProperties) { property ->
                        InstagramStylePropertyCard(
                            property = property,
                            onItemClick = { },
                            onLikeClick = { viewModel.toggleLike(property.id, property.isLiked) },
                            onShareClick = { },
                            onInvestClick = { },
                            showInvestButton = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DrawerCardBg)
    ) { Column(modifier = Modifier.padding(vertical = 4.dp)) { content() } }
}

@Composable
fun DrawerItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
        ); Spacer(modifier = Modifier.width(16.dp)); Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        color = textColor,
        modifier = Modifier.weight(1f)
    ); if (badge != null) {
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
    }; if (showChevron) {
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
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .padding(bottom = 8.dp)
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF2A3441),
                    strokeWidth = 4.dp,
                    trackColor = Color.Transparent
                ); CircularProgressIndicator(
                progress = { stat.progress },
                modifier = Modifier.fillMaxSize(),
                color = Color(stat.colorHex),
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
                trackColor = Color.Transparent
            )
            }; Text(
            text = stat.title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 1
        ); Text(
            text = stat.value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        }
    }
}