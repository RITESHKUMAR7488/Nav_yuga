package com.example.mahayuga.feature.assetmanager.presentation.risk

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

// --- THEME ---
private val RiskBg = Color(0xFF061123)
private val RiskCard = Color(0xFF111c30)
private val SafeGreen = Color(0xFF34C759)
private val WarnOrange = Color(0xFFF59E0B)
private val DangerRed = Color(0xFFFF3B30)
private val TrustBlue = Color(0xFF3B82F6)
private val TextWhite = Color.White
private val TextGrey = Color(0xFF8B9BB4)

@Composable
fun RiskScreen(
    viewModel: RiskViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RiskBg)
            .padding(16.dp)
    ) {
        // Header
        Text(
            "Risk & Compliance",
            style = MaterialTheme.typography.headlineMedium,
            color = TextWhite,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Trust Score, Legal & Regulatory Health",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGrey
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 1. TRUST & HEALTH ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Trust Score
            Card(
                colors = CardDefaults.cardColors(containerColor = RiskCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(140.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Shield, null, tint = TrustBlue, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${state.overallTrustScore}", color = TextWhite, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text("Trust Score", color = TextGrey, fontSize = 12.sp)
                }
            }

            // Critical Alerts
            Card(
                colors = CardDefaults.cardColors(containerColor = if(state.criticalAlerts > 0) DangerRed.copy(0.1f) else SafeGreen.copy(0.1f)),
                border = if(state.criticalAlerts > 0) androidx.compose.foundation.BorderStroke(1.dp, DangerRed) else null,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(140.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        if(state.criticalAlerts > 0) Icons.Default.Error else Icons.Default.CheckCircle,
                        null,
                        tint = if(state.criticalAlerts > 0) DangerRed else SafeGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${state.criticalAlerts}", color = TextWhite, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text(if(state.criticalAlerts == 1) "Critical Breach" else "Critical Breaches", color = TextGrey, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. RISK HEATMAP
        Text("Portfolio Risk Heatmap", style = MaterialTheme.typography.titleMedium, color = TextWhite, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            state.riskHeatmap.take(3).forEach { asset ->
                RiskHeatmapCard(asset, Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. COMPLIANCE CHECKLIST
        Text("Regulatory Compliance", style = MaterialTheme.typography.titleMedium, color = TextWhite, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(state.pendingItems) { item ->
                ComplianceRow(item)
            }
        }
    }
}

@Composable
fun RiskHeatmapCard(asset: RiskAsset, modifier: Modifier) {
    val color = when {
        asset.riskScore > 70 -> DangerRed
        asset.riskScore > 40 -> WarnOrange
        else -> SafeGreen
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = RiskCard),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier.size(8.dp).background(color, CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(asset.name.take(10) + "...", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(asset.riskType, color = TextGrey, fontSize = 10.sp)
        }
    }
}

@Composable
fun ComplianceRow(item: ComplianceItem) {
    val (statusColor, icon) = when(item.status) {
        "Compliant" -> SafeGreen to Icons.Default.CheckCircle
        "Breached" -> DangerRed to Icons.Default.Error
        else -> WarnOrange to Icons.Default.Warning // Pending
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = RiskCard),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = statusColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(item.assetName, color = TextGrey, fontSize = 12.sp)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(item.status.uppercase(), color = statusColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text(item.dueDate, color = TextGrey, fontSize = 10.sp)
            }
        }
    }
}