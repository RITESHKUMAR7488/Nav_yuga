// main/java/com/example/mahayuga/feature/assetmanager/presentation/investors/InvestorScreen.kt
package com.example.mahayuga.feature.assetmanager.presentation.investors

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mahayuga.core.common.* // ⚡ IMPORTED COMMON COMPONENTS
import com.example.mahayuga.ui.theme.* // ⚡ IMPORTED BRICX THEME

@Composable
fun InvestorScreen(
    viewModel: InvestorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") } // ⚡ ADDED SEARCH STATE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BricxBackground) // ⚡ UPDATED
            .padding(16.dp)
    ) {
        Text(
            "Investor Intelligence",
            style = MaterialTheme.typography.headlineMedium,
            color = BricxTextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Capital CRM & Concentration Risk",
            style = MaterialTheme.typography.bodyMedium,
            color = BricxTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ⚡ ADDED SEARCH BAR AS REQUESTED
        BricxTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = "Search Investors",
            leadingIcon = { Icon(Icons.Default.Search, null, tint = BricxBrandTeal) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard), // ⚡ UPDATED
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Whale Risk", color = BricxTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        if (state.isWhaleRiskHigh) {
                            Icon(
                                Icons.Default.Warning,
                                null,
                                tint = BricxDangerRed,
                                modifier = Modifier.size(14.dp)
                            ) // ⚡ UPDATED
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                        CircularRiskMeter(
                            percentage = state.whaleConcentrationPercent.toFloat(),
                            isHighRisk = state.isWhaleRiskHigh
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${state.whaleConcentrationPercent.toInt()}%",
                                color = BricxTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text("Top 5 Conc.", color = BricxTextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard), // ⚡ UPDATED
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Avg Ticket Size", color = BricxTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        formatInvK(state.averageTicketSize),
                        color = BricxTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${state.totalInvestors} Active Investors",
                        color = BricxBrandTeal,
                        fontSize = 11.sp
                    ) // ⚡ UPDATED
                    Spacer(modifier = Modifier.weight(1f))
                    // ⚡ REPLACED LOCAL GRAPH WITH COMMON SPARKLINE
                    SparklineGraph(
                        data = state.fundraisingVelocity,
                        color = BricxBrandBlue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Capital Directory",
            style = MaterialTheme.typography.titleMedium,
            color = BricxTextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        val filteredList =
            if (searchQuery.isBlank()) state.investorList else state.investorList.filter {
                it.name.contains(
                    searchQuery,
                    ignoreCase = true
                )
            }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(filteredList) { investor ->
                InvestorItem(investor)
            }
        }
    }
}

@Composable
fun InvestorItem(investor: InvestorModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard), // ⚡ UPDATED
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(BricxSurfaceCardLight, CircleShape), // ⚡ UPDATED
                contentAlignment = Alignment.Center
            ) {
                Text(
                    investor.name.take(1),
                    color = BricxBrandTeal,
                    fontWeight = FontWeight.Bold
                ) // ⚡ UPDATED
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    investor.name,
                    color = BricxTextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        investor.type,
                        color = BricxTextSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                    if (investor.reinvestCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "•  ${investor.reinvestCount}x Reinvestor",
                            color = BricxBrandTeal,
                            style = MaterialTheme.typography.labelSmall
                        ) // ⚡ UPDATED
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatInvK(investor.totalInvested),
                    color = BricxTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    investor.tags.take(2).forEach { tag -> TagBadge(tag) }
                }
            }
        }
    }
}

@Composable
fun TagBadge(text: String) {
    val color = when (text) {
        "WHALE" -> BricxBrandBlue // ⚡ UPDATED
        "LOYAL" -> BricxBrandTeal // ⚡ UPDATED
        "RISK" -> BricxDangerRed // ⚡ UPDATED
        else -> BricxTextSecondary
    }

    Box(
        modifier = Modifier
            .background(color.copy(0.1f), RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(text, color = color, fontSize = 8.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CircularRiskMeter(percentage: Float, isHighRisk: Boolean) {
    val color = if (isHighRisk) BricxDangerRed else BricxBrandTeal // ⚡ UPDATED

    Canvas(modifier = Modifier.size(80.dp)) {
        drawArc(
            color = Color.White.copy(0.1f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx())
        )
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = (percentage / 100) * 360f,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

fun formatInvK(amount: Double): String {
    return when {
        amount >= 10000000 -> String.format("₹%.1f Cr", amount / 10000000)
        amount >= 100000 -> String.format("₹%.1f L", amount / 100000)
        else -> "₹${amount.toInt()}"
    }
}