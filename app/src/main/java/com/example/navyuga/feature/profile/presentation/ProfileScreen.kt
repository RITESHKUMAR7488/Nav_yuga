package com.example.navyuga.feature.profile.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// Removed: import androidx.compose.ui.unit.sp (No longer needed)
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.navyuga.R
import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.arthyuga.domain.model.PropertyModel
import com.example.navyuga.feature.profile.data.model.ProfileStat

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUserState by viewModel.currentUser.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val ownedProperties by viewModel.ownedProperties.collectAsState()
    val likedProperties by viewModel.likedProperties.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    val userName = if (currentUserState is UiState.Success) {
        (currentUserState as UiState.Success).data.name.ifEmpty { "User" }
    } else "User"

    val activeList = if (selectedTab == 0) ownedProperties else likedProperties
    val emptyMessage = if (selectedTab == 0) "You don't own any properties yet." else "No liked properties found."

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. HEADER
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Fixed: Using bodyLarge (16sp) instead of hardcoded 18sp
                        Text(
                            text = "Hello,",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        // Fixed: Using headlineMedium instead of hardcoded 32sp
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(
                        onClick = onThemeToggle,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Crossfade(targetState = isDarkTheme, label = "theme") { isDark ->
                            val iconRes = if (isDark) R.drawable.ic_sun else R.drawable.ic_moon
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = "Theme",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // 2. STATS GRID
            item {
                // Fixed: Using titleLarge instead of hardcoded 20sp
                Text(
                    text = "Your Real Estate Portfolio",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )

                val displayStats = if (stats.isNotEmpty()) stats else List(4) {
                    ProfileStat("", "-", 0f, 0xFF2979FF)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (displayStats.size >= 1) BigStatCard(displayStats[0], Modifier.weight(1f))
                        if (displayStats.size >= 2) BigStatCard(displayStats[1], Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (displayStats.size >= 3) BigStatCard(displayStats[2], Modifier.weight(1f))
                        if (displayStats.size >= 4) BigStatCard(displayStats[3], Modifier.weight(1f))
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // 3. TABS
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    ProfileTabButton("Your Properties", selectedTab == 0, Modifier.weight(1f)) { selectedTab = 0 }
                    Spacer(modifier = Modifier.width(12.dp))
                    ProfileTabButton("Liked", selectedTab == 1, Modifier.weight(1f)) { selectedTab = 1 }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // 4. PROPERTY LIST
            if (activeList.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        // Fixed: Using bodyMedium (14sp) instead of hardcoded 14sp
                        Text(
                            text = emptyMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(activeList) { property ->
                    ProfilePropertyCard(property = property, onLikeClick = { /* Handle Like */ }, onShareClick = { /* Handle Share */ })
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun BigStatCard(stat: ProfileStat, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, Color(0xFF2A3441))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp).padding(bottom = 12.dp)
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF2A3441),
                    strokeWidth = 8.dp,
                    trackColor = Color.Transparent,
                )
                CircularProgressIndicator(
                    progress = { stat.progress },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF2979FF),
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round,
                    trackColor = Color.Transparent
                )
            }
            // Fixed: Using bodySmall (12sp) instead of hardcoded 12sp
            Text(
                text = stat.title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            // Fixed: Using titleMedium instead of hardcoded 18sp
            Text(
                text = stat.value,
                style = MaterialTheme.typography.titleMedium,
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
                Icon(Icons.Default.MoreVert, "Options", tint = MaterialTheme.colorScheme.onSurface)
            }
            Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp).height(320.dp).clip(RoundedCornerShape(16.dp)).background(Color.LightGray)) {
                AsyncImage(model = property.mainImage, contentDescription = "Property Image", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
            Row(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp, start = 16.dp, end = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                ProfilePropertyStat("Price", "₹${property.minInvest}")
                ProfilePropertyStat("Return", property.rentReturn.ifEmpty { "₹15k" })
                ProfilePropertyStat("ROI", "${property.roi}%", true)
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        if (property.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        "Like",
                        tint = if (property.isLiked) Color.Red else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.scale(scale)
                    )
                }
                IconButton(onClick = onShareClick) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Share", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.rotate(-45f).padding(bottom = 4.dp))
                }
            }
        }
    }
}

@Composable
fun ProfilePropertyStat(label: String, value: String, isHighlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Fixed: Using bodySmall/bodyMedium from theme
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = if (isHighlight) Color(0xFF4ADE80) else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun ProfileTabButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val containerColor = if (isSelected) Color(0xFF2979FF) else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        // Fixed: Using bodyMedium (14sp) instead of hardcoded 14sp
        Text(text = text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = contentColor)
    }
}