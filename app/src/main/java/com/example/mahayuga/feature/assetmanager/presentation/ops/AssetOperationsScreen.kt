package com.example.mahayuga.feature.assetmanager.presentation.ops

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.NumberFormat
import java.util.Locale

// --- THEME ---
private val AmBackground = Color(0xFF061123)
private val AmSurface = Color(0xFF111c30)
private val AmAccent = Color(0xFF38a882)
private val RiskRed = Color(0xFFFF3B30)
private val SafeGreen = Color(0xFF34C759)
private val TextWhite = Color.White
private val TextGrey = Color(0xFF8B9BB4)

@Composable
fun AssetOperationsScreen(
    viewModel: AssetOpsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmBackground)
            .padding(16.dp)
    ) {
        // HEADER
        Text(
            "Operations & Risk",
            style = MaterialTheme.typography.headlineMedium,
            color = TextWhite,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Lease Expiry & Maintenance Control",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGrey
        )

        Spacer(modifier = Modifier.height(24.dp))

        // KPI ROW
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OpsKpiCard(
                "Vacancy Risk",
                "${state.highRiskCount} Assets",
                "Expiring < 90 Days",
                if (state.highRiskCount > 0) RiskRed else SafeGreen,
                Modifier.weight(1f)
            )
            OpsKpiCard(
                "Maint. Spend (YTD)",
                formatK(state.totalMaintenanceSpend),
                "Avg 1.2% of Revenue",
                AmAccent,
                Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ASSET LIST
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AmAccent)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.assets) { item ->
                    AssetOpCard(item)
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun AssetOpCard(item: AssetOpsModel) {
    val statusColor = if (item.isHighRisk) RiskRed else SafeGreen

    Card(
        colors = CardDefaults.cardColors(containerColor = AmSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        item.property.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        item.property.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrey
                    )
                }
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        if (item.isHighRisk) "HIGH RISK" else "STABLE",
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metrics Grid
            Row(modifier = Modifier.fillMaxWidth()) {
                // Lease Countdown
                Column(modifier = Modifier.weight(1f)) {
                    Text("DAYS TO VACANCY", style = MaterialTheme.typography.labelSmall, color = TextGrey)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, null, tint = statusColor, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${item.daysToVacancy} Days",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Maintenance
                Column(modifier = Modifier.weight(1f)) {
                    Text("MAINTENANCE (YTD)", style = MaterialTheme.typography.labelSmall, color = TextGrey)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        formatK(item.maintenanceSpendYtd),
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Transparency
                Column(modifier = Modifier.weight(0.8f), horizontalAlignment = Alignment.End) {
                    Text("TRUST SCORE", style = MaterialTheme.typography.labelSmall, color = TextGrey)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${item.transparencyScore}%",
                        color = AmAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Forecast Alert (If spending is high)
            if (item.capexForecast > item.maintenanceSpendYtd) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(0.05f), RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.TrendingUp, null, tint = TextGrey, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Capex Forecast: ${formatK(item.capexForecast)} next year (10% increase)",
                        color = TextGrey,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OpsKpiCard(title: String, value: String, subtitle: String, accentColor: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = AmSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(accentColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextWhite)
            Text(title, style = MaterialTheme.typography.bodyMedium, color = TextGrey)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = accentColor)
        }
    }
}

fun formatK(amount: Double): String {
    return when {
        amount >= 10000000 -> String.format("₹%.1f Cr", amount / 10000000)
        amount >= 100000 -> String.format("₹%.1f L", amount / 100000)
        else -> "₹${amount.toInt()}"
    }
}