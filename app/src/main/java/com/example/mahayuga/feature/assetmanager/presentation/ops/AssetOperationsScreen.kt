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
import com.example.mahayuga.core.common.* // ⚡ IMPORTED COMMON COMPONENTS
import com.example.mahayuga.ui.theme.* // ⚡ IMPORTED BRICX THEME

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
            .background(BricxBackground) // ⚡ UPDATED
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
                    Icons.Rounded.Apartment,
                    contentDescription = "Assets",
                    tint = BricxBrandTeal,
                    modifier = Modifier.size(28.dp)
                ) // ⚡ UPDATED
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Assets",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = BricxTextPrimary
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { isSearchActive = !isSearchActive },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = BricxTextPrimary
                    )
                }
                BadgedBox(badge = { Badge(containerColor = BricxDangerRed) { Text("3") } }) {
                    Icon(
                        Icons.Rounded.Notifications,
                        contentDescription = "Notifications",
                        tint = BricxTextPrimary
                    )
                }
                BadgedBox(badge = { Badge(containerColor = BricxBrandTeal) { Text("1") } }) {
                    Icon(
                        Icons.Outlined.MailOutline,
                        contentDescription = "Messages",
                        tint = BricxTextPrimary
                    )
                }
            }
        }

        AnimatedVisibility(visible = isSearchActive) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                // ⚡ REPLACED RAW OUTLINEDTEXTFIELD WITH BRICTEXTFIELD
                BricxTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    label = "Search specific trust or property..."
                )
            }
        }

        // ⚡ REPLACED RAW BUTTON WITH BRICXPRIMARYBUTTON
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            BricxPrimaryButton(
                text = "List New Property / Trust",
                onClick = { /* TODO: Route to AddPropertyScreen */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }

        when (uiState) {
            is AssetsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = BricxBrandTeal) }
            }

            is AssetsUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text("Error loading assets", color = BricxDangerRed) }
            }

            is AssetsUiState.Success -> {
                val assets = (uiState as AssetsUiState.Success).assets
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(assets) { asset -> TrustAssetCard(asset = asset) }
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
            .border(1.dp, BricxBorder, RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded }, // ⚡ UPDATED
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCardLight) // ⚡ UPDATED
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        color = BricxTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (asset is TrustAsset.Reit) "REIT (Multi-Property)" else "SM REIT (Single Property)",
                        fontSize = 12.sp,
                        color = BricxTextSecondary
                    )
                }

                Surface(
                    color = if (asset.status == AssetStatus.LISTED) BricxBrandTeal.copy(alpha = 0.2f) else BricxWarningOrange.copy(
                        alpha = 0.2f
                    ), // ⚡ UPDATED
                    shape = RoundedCornerShape(8.dp),
                    border = borderStrokeFromStatus(asset.status)
                ) {
                    Text(
                        text = asset.status.name,
                        color = if (asset.status == AssetStatus.LISTED) BricxBrandTeal else BricxWarningOrange, // ⚡ UPDATED
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Stock Price", color = BricxTextSecondary, fontSize = 12.sp)
                    Text(
                        "₹${asset.stockPrice}",
                        color = BricxTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = BricxTextPrimary,
                    modifier = Modifier.rotate(rotationState)
                )
            }

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
                                color = BricxTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            asset.spvTenants.forEach { tenant ->
                                Text(
                                    "  ↳ $tenant",
                                    color = BricxTextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        is TrustAsset.Reit -> {
                            asset.spvBuildings.forEach { building ->
                                Text(
                                    "🏢 ${building.buildingName}",
                                    color = BricxTextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                building.tenants.forEach { tenant ->
                                    Text(
                                        "  ↳ $tenant",
                                        color = BricxTextSecondary,
                                        fontSize = 13.sp
                                    )
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
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            BricxBrandTeal
                        ) // ⚡ UPDATED
                    ) {
                        Text("Manage Trust Properties", color = BricxBrandTeal, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun borderStrokeFromStatus(status: AssetStatus): androidx.compose.foundation.BorderStroke {
    return if (status == AssetStatus.LISTED) {
        androidx.compose.foundation.BorderStroke(1.dp, BricxBrandTeal.copy(alpha = 0.5f))
    } else {
        androidx.compose.foundation.BorderStroke(1.dp, BricxWarningOrange.copy(alpha = 0.5f))
    }
}