package com.example.mahayuga.feature.assetmanager.presentation.investors

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Locale

// --- THEME ---
private val InvBg = Color(0xFF061123)
private val InvCard = Color(0xFF111c30)
private val InvTeal = Color(0xFF38a882)
private val InvRed = Color(0xFFFF3B30)
private val InvBlue = Color(0xFF3B82F6)
private val TextWhite = Color.White
private val TextGrey = Color(0xFF8B9BB4)

@Composable
fun InvestorScreen(
    viewModel: InvestorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InvBg)
            .padding(16.dp)
    ) {
        // Header
        Text(
            "Investor Intelligence",
            style = MaterialTheme.typography.headlineMedium,
            color = TextWhite,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Capital CRM & Concentration Risk",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGrey
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 1. RISK & VELOCITY ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Whale Risk Card
            Card(
                colors = CardDefaults.cardColors(containerColor = InvCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(160.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Whale Risk", color = TextGrey, fontSize = 12.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        if (state.isWhaleRiskHigh) {
                            Icon(Icons.Default.Warning, null, tint = InvRed, modifier = Modifier.size(14.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Circular Progress
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                        CircularRiskMeter(percentage = state.whaleConcentrationPercent.toFloat(), isHighRisk = state.isWhaleRiskHigh)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${state.whaleConcentrationPercent.toInt()}%",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text(
                                "Top 5 Conc.",
                                color = TextGrey,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Avg Ticket Card
            Card(
                colors = CardDefaults.cardColors(containerColor = InvCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(160.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Avg Ticket Size", color = TextGrey, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        formatInvK(state.averageTicketSize),
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${state.totalInvestors} Active Investors",
                        color = InvTeal,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    // Tiny Trend Graph
                    VelocityGraph(data = state.fundraisingVelocity, color = InvBlue)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. INVESTOR DIRECTORY
        Text(
            "Capital Directory",
            style = MaterialTheme.typography.titleMedium,
            color = TextWhite,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(state.investorList) { investor ->
                InvestorItem(investor)
            }
        }
    }
}

@Composable
fun InvestorItem(investor: InvestorModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = InvCard),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF1E293B), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    investor.name.take(1),
                    color = InvTeal,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    investor.name,
                    color = TextWhite,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        investor.type,
                        color = TextGrey,
                        style = MaterialTheme.typography.labelSmall
                    )
                    if (investor.reinvestCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "•  ${investor.reinvestCount}x Reinvestor",
                            color = InvTeal,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            // Amount & Tags
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatInvK(investor.totalInvested),
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    investor.tags.take(2).forEach { tag ->
                        TagBadge(tag)
                    }
                }
            }
        }
    }
}

@Composable
fun TagBadge(text: String) {
    val color = when(text) {
        "WHALE" -> InvBlue
        "LOYAL" -> InvTeal
        "RISK" -> InvRed
        else -> TextGrey
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
    val color = if (isHighRisk) InvRed else InvTeal

    Canvas(modifier = Modifier.size(80.dp)) {
        // Background Circle
        drawArc(
            color = Color.White.copy(0.1f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx())
        )
        // Progress Arc
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = (percentage / 100) * 360f,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun VelocityGraph(data: List<Float>, color: Color) {
    if (data.isEmpty()) return

    Canvas(modifier = Modifier.fillMaxWidth().height(30.dp)) {
        val width = size.width
        val height = size.height
        val max = data.maxOrNull() ?: 1f
        val points = data.mapIndexed { i, v ->
            Offset(
                x = (i.toFloat() / (data.size - 1)) * width,
                y = height - ((v / max) * height)
            )
        }

        // Draw Line
        for (i in 0 until points.size - 1) {
            drawLine(
                color = color,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

fun formatInvK(amount: Double): String {
    return when {
        amount >= 10000000 -> String.format("₹%.1f Cr", amount / 10000000)
        amount >= 100000 -> String.format("₹%.1f L", amount / 100000)
        else -> "₹${amount.toInt()}"
    }
}