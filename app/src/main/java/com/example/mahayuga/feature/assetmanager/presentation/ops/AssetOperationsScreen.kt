// main/java/com/example/mahayuga/feature/assetmanager/presentation/ops/AssetOperationsScreen.kt
package com.example.mahayuga.feature.assetmanager.presentation.ops

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

// Consistent Bricx Theme Colors
private val AmBackground = Color(0xFF061123)
private val AmAccent = Color(0xFF38a882)
private val MetallicStart = Color(0xFF232D3F)
private val MetallicBorder = Color(0xFF37475A)
private val PendingWarning = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetOperationsScreen(
    viewModel: AssetOpsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var isSearchActive by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmBackground)
    ) {
        // --- HEADER LOGIC (Exactly matching the dashboard) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: Assets + Icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Apartment,
                    contentDescription = "Assets",
                    tint = AmAccent,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Assets",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White
                )
            }

            // Right Side: 3 Icons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { isSearchActive = !isSearchActive },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Rounded.Search, contentDescription = "Search", tint = Color.White)
                }

                BadgedBox(badge = { Badge(containerColor = Color.Red) { Text("3") } }) {
                    Icon(
                        Icons.Rounded.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White
                    )
                }

                BadgedBox(badge = { Badge(containerColor = AmAccent) { Text("1") } }) {
                    Icon(
                        Icons.Outlined.MailOutline,
                        contentDescription = "Messages",
                        tint = Color.White
                    )
                }
            }
        }

        // --- SEARCH BAR (Animated Visibility) ---
        AnimatedVisibility(visible = isSearchActive) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search specific trust or property...", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmAccent,
                    unfocusedBorderColor = MetallicBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // --- LIST NEW PROPERTY BUTTON ---
        Button(
            onClick = { /* TODO: Route to AddPropertyScreen */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AmAccent),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "List New Property / Trust",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        // --- DYNAMIC CONTENT AREA ---
        when (uiState) {
            is AssetsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AmAccent)
                }
            }

            is AssetsUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error loading assets", color = Color.Red)
                }
            }

            is AssetsUiState.Success -> {
                val assets = (uiState as AssetsUiState.Success).assets
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(assets) { asset ->
                        TrustAssetCard(asset = asset)
                    }
                }
            }
        }
    }
}

@Composable
fun TrustAssetCard(asset: TrustAsset) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "RotateIcon"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MetallicBorder, RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MetallicStart)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Trust Name & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = asset.trustName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (asset is TrustAsset.Reit) "REIT (Multi-Property)" else "SM REIT (Single Property)",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Status Chip
                Surface(
                    color = if (asset.status == AssetStatus.LISTED) AmAccent.copy(alpha = 0.2f) else PendingWarning.copy(
                        alpha = 0.2f
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = borderStrokeFromStatus(asset.status)
                ) {
                    Text(
                        text = asset.status.name,
                        color = if (asset.status == AssetStatus.LISTED) AmAccent else PendingWarning,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Stock Price", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        "₹${asset.stockPrice}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = Color.White,
                    modifier = Modifier.rotate(rotationState)
                )
            }

            // Expandable Content (The Flowchart mapping)
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    when (asset) {
                        is TrustAsset.SmReit -> {
                            Text(
                                "📍 ${asset.propertyName}",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            asset.spvTenants.forEach { tenant ->
                                Text("  ↳ $tenant", color = Color.Gray, fontSize = 13.sp)
                            }
                        }

                        is TrustAsset.Reit -> {
                            asset.spvBuildings.forEach { building ->
                                Text(
                                    "🏢 ${building.buildingName}",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                building.tenants.forEach { tenant ->
                                    Text("  ↳ $tenant", color = Color.Gray, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { /* TODO: Route to detail page for deep edits */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmAccent)
                    ) {
                        Text("Manage Trust Properties", color = AmAccent, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun borderStrokeFromStatus(status: AssetStatus): androidx.compose.foundation.BorderStroke {
    return if (status == AssetStatus.LISTED) {
        androidx.compose.foundation.BorderStroke(1.dp, AmAccent.copy(alpha = 0.5f))
    } else {
        androidx.compose.foundation.BorderStroke(1.dp, PendingWarning.copy(alpha = 0.5f))
    }
}