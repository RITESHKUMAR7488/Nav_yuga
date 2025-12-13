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
import androidx.compose.material.icons.filled.MoreVert
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

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToLiked: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUserState by viewModel.currentUser.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val ownedProperties by viewModel.ownedProperties.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val userName = if (currentUserState is UiState.Success) {
        (currentUserState as UiState.Success).data.name.ifEmpty { "User" }
    } else "User"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. HEADER WITH MENU
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

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Liked") },
                                onClick = {
                                    showMenu = false
                                    onNavigateToLiked()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Wallet") },
                                onClick = {
                                    showMenu = false
                                    Toast.makeText(context, "Wallet Coming Soon", Toast.LENGTH_SHORT).show()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("10 Step Guide Buying Property") },
                                onClick = {
                                    showMenu = false
                                    Toast.makeText(context, "Guide Coming Soon", Toast.LENGTH_SHORT).show()
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Log Out", color = Color.Red) },
                                onClick = {
                                    showMenu = false
                                    viewModel.logout()
                                    onLogout()
                                }
                            )
                        }
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
                    // Fixed: Added 'L' suffix to hex color for Long
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
                    // ⚡ FIXED: Using `stat.colorHex` instead of `stat.color`
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