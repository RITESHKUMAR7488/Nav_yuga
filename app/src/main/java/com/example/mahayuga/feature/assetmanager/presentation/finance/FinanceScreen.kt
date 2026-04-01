// main/java/com/example/mahayuga/feature/assetmanager/presentation/finance/FinanceScreen.kt
package com.example.mahayuga.feature.assetmanager.presentation.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mahayuga.core.common.* // ⚡ IMPORTED COMMON COMPONENTS
import com.example.mahayuga.ui.theme.* // ⚡ IMPORTED BRICX THEME

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.selectedAsset != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.clearSelection() },
            containerColor = BricxSurfaceCard, // ⚡ UPDATED
            contentColor = BricxTextPrimary
        ) {
            PayoutSheetContent(
                asset = state.selectedAsset!!,
                onConfirm = { viewModel.executePayout() },
                onCancel = { viewModel.clearSelection() }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BricxBackground) // ⚡ UPDATED
            .padding(16.dp)
    ) {
        Text(
            "Finance Engine",
            style = MaterialTheme.typography.headlineMedium,
            color = BricxTextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Income, Yield & Distribution",
            style = MaterialTheme.typography.bodyMedium,
            color = BricxTextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FinanceKpiCard(
                title = "Total NDI",
                value = formatK(state.totalNdi),
                sub = "Net Distributable",
                color = BricxWarningOrange, // ⚡ UPDATED (Gold-ish)
                icon = Icons.Default.AccountBalanceWallet,
                modifier = Modifier.weight(1f)
            )

            FinanceKpiCard(
                title = "Avg Yield",
                value = String.format("%.2f%%", state.avgPortfolioYield),
                sub = "Target: 14.0%",
                color = BricxSuccessGreen, // ⚡ UPDATED
                icon = Icons.Default.TrendingUp,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Active Distributions (Run Payout)",
            style = MaterialTheme.typography.titleMedium,
            color = BricxTextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(state.assets) { item ->
                PayoutAssetCard(
                    item = item,
                    onClick = { viewModel.selectAssetForPayout(item) }
                )
            }
        }
    }
}

@Composable
fun PayoutAssetCard(
    item: AssetFinanceModel,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard), // ⚡ UPDATED
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.property.title,
                    color = BricxTextPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "NDI: ${formatK(item.ndi)} / month",
                    color = BricxWarningOrange,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    String.format("Yield: %.1f%%", item.yield),
                    color = BricxSuccessGreen,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(BricxBackground, RoundedCornerShape(50))
                        .border(1.dp, BricxSuccessGreen.copy(0.5f), RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        "RUN PAYOUT",
                        color = BricxSuccessGreen,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PayoutSheetContent(asset: AssetFinanceModel, onConfirm: () -> Unit, onCancel: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Confirm Distribution",
                style = MaterialTheme.typography.headlineSmall,
                color = BricxTextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onCancel) {
                Icon(
                    Icons.Default.Close,
                    null,
                    tint = BricxTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = BricxBackground),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                RowSpec("Gross Rent Collected", formatK(asset.grossRent), BricxTextPrimary)
                HorizontalDivider(color = BricxBorder, modifier = Modifier.padding(vertical = 8.dp))
                RowSpec("Less: Property Tax & Ops", "- ${formatK(asset.expenses)}", BricxDangerRed)
                RowSpec(
                    "Less: Capital Reserves (5%)",
                    "- ${formatK(asset.reserves)}",
                    BricxDangerRed
                )
                HorizontalDivider(color = BricxBorder, modifier = Modifier.padding(vertical = 8.dp))
                RowSpec("Net Distributable Income", formatK(asset.ndi), BricxWarningOrange, true)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onConfirm,
            colors = ButtonDefaults.buttonColors(containerColor = BricxBrandTeal),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Download, null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("APPROVE & GENERATE PDF", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "This action will generate the Capital & Rental Statement and notify investors.",
            style = MaterialTheme.typography.bodySmall,
            color = BricxTextSecondary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun RowSpec(label: String, value: String, color: Color, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = BricxTextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            color = color,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun FinanceKpiCard(
    title: String,
    value: String,
    sub: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(color.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                }
                // ⚡ ADDED SPARKLINE FOR VISUAL DENSITY
                SparklineGraph(
                    data = listOf(10f, 15f, 12f, 20f, 25f),
                    color = color,
                    modifier = Modifier
                        .width(40.dp)
                        .height(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = BricxTextPrimary
            )
            Text(title, style = MaterialTheme.typography.bodyMedium, color = BricxTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(sub, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}

fun formatK(amount: Double): String {
    return when {
        amount >= 10000000 -> String.format("₹%.2f Cr", amount / 10000000)
        amount >= 100000 -> String.format("₹%.2f L", amount / 100000)
        else -> "₹${amount.toInt()}"
    }
}