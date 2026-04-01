package com.example.mahayuga.feature.assetmanager.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.NumberFormat
import java.util.Locale

// --- REFERENCE COLORS ---
private object BricxColors {
    val BgDark = Color(0xFF080F18)       // Deep background
    val CardDark = Color(0xFF0F1722)     // Card background
    val TextWhite = Color(0xFFFFFFFF)
    val TextGrey = Color(0xFF8B9BB4)
    val GraphGreen = Color(0xFF38a882)   // Matches AmAccent (Teal Green)
    val GraphRed = Color(0xFFFF3B30)     // Added for Red Flag
    val InnerBoxBg = Color(0xFF080F18)   // Matches BgDark for the "cut-out" look
}

@Composable
fun PortfolioCommandCentre(
    viewModel: AssetManagerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // --- DUMMY DATA ---
    val aumData = remember { listOf(10f, 25f, 40f, 35f, 50f, 65f, 80f, 100f) }
    val cashData = remember { listOf(20f, 22f, 28f, 35f, 42f, 40f, 55f, 62f) }
    val obliData = remember { listOf(15f, 12f, 18f, 25f, 22f, 30f, 35f, 40f) }
    val roiData = remember { listOf(5f, 6f, 6.5f, 7f, 7.2f, 7.8f, 8f) }
    val irrData = remember { listOf(10f, 11f, 11.8f, 12.5f, 13f, 13.5f, 14f) }

    Scaffold(
        containerColor = BricxColors.BgDark,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "BRICX",
                    style = MaterialTheme.typography.headlineMedium,
                    color = BricxColors.TextWhite,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Portfolio Command Centre",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BricxColors.TextGrey,
                    fontWeight = FontWeight.Light
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            // ⚡ Red Flag Alert (Logic Requirement)
            if (state.hasRedFlag) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = BricxColors.GraphRed.copy(
                            alpha = 0.1f
                        )
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BricxColors.GraphRed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            null,
                            tint = BricxColors.GraphRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Alert: Negative Cash Flow Detected",
                            color = BricxColors.GraphRed,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // --- ROW 1 ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Total AUM (Live)
                BricxCard(
                    title = "Total AUM (Live)",
                    value = formatCurrency(state.totalAum), // ⚡ Bound Data
                    modifier = Modifier
                        .weight(1f)
                        .height(150.dp)
                ) {
                    BricxGraph(dataPoints = aumData, color = BricxColors.GraphGreen)
                }

                // 2. Asset Under Management (Grid)
                BricxCard(
                    title = "Asset Under Management",
                    value = null, // No main value, just grid
                    modifier = Modifier
                        .weight(1f)
                        .height(150.dp)
                ) {
                    AssetGrid(state.assetBreakdown) // ⚡ Bound Data
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- ROW 2 ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 3. Cash Inflow
                BricxCard(
                    title = "Cash Inflow",
                    value = "${formatCurrency(state.cashInflow)} (This month)", // ⚡ Bound Data + Original Text
                    modifier = Modifier
                        .weight(1f)
                        .height(150.dp)
                ) {
                    BricxGraph(dataPoints = cashData, color = BricxColors.GraphGreen)
                }

                // 4. Upcoming Obligations
                BricxCard(
                    title = "Upcoming Obligations",
                    value = "${formatCurrency(state.obligations)} (due this month)", // ⚡ Bound Data + Original Text
                    modifier = Modifier
                        .weight(1f)
                        .height(150.dp)
                ) {
                    BricxGraph(dataPoints = obliData, color = BricxColors.GraphGreen)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- ROW 3 ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 5. Projected ROI
                BricxCard(
                    title = "Projected ROI",
                    value = null,
                    topRightText = String.format("%.0f%%", state.portfolioIrr), // ⚡ Bound Data
                    subText = "Q2 April",
                    modifier = Modifier
                        .weight(1f)
                        .height(150.dp)
                ) {
                    BricxGraph(dataPoints = roiData, color = BricxColors.GraphGreen)
                }

                // 6. Projected IRR
                BricxCard(
                    title = "Projected IRR",
                    value = null,
                    topRightText = String.format("%.0f%%", state.portfolioIrr), // ⚡ Bound Data
                    subText = "Q2 April",
                    modifier = Modifier
                        .weight(1f)
                        .height(150.dp)
                ) {
                    BricxGraph(dataPoints = irrData, color = BricxColors.GraphGreen)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- ROW 4: DISTRIBUTION TIMELINE ---
            BricxCard(
                title = "Distribution Due Timeline",
                value = null,
                modifier = Modifier.fillMaxWidth(),
                isVariableHeight = true
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TimelineRow("01 Apr", "Rent Yield Fund II", "₹23.4 Cr")
                    TimelineRow("05 Apr", "High Street Opps", "₹12.1 Cr")
                    TimelineRow("12 Apr", "Warehouse Yield I", "₹08.5 Cr")
                    TimelineRow("15 Apr", "Data Centre Fund", "₹15.2 Cr")
                    TimelineRow("28 Apr", "Suburban Comm. REIT", "₹04.3 Cr")
                    TimelineRow("02 May", "Bandra North Corp", "₹18.9 Cr")
                    TimelineRow("10 May", "Logistics Park III", "₹09.1 Cr")
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// --- COMPONENTS ---

@Composable
fun BricxCard(
    title: String,
    value: String?,
    topRightText: String? = null,
    subText: String? = null,
    modifier: Modifier = Modifier,
    isVariableHeight: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BricxColors.CardDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = BricxColors.TextGrey,
                    maxLines = 1,
                    fontSize = 11.sp
                )
                if (topRightText != null) {
                    Text(
                        text = topRightText,
                        style = MaterialTheme.typography.bodySmall,
                        color = BricxColors.GraphGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            // Value Row
            if (value != null) {
                Spacer(modifier = Modifier.height(4.dp))
                if (value.contains("(")) {
                    val parts = value.split("(", limit = 2)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = parts[0].trim(),
                            style = MaterialTheme.typography.titleMedium,
                            color = BricxColors.TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(" + parts[1],
                            style = MaterialTheme.typography.bodySmall,
                            color = BricxColors.TextGrey,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                } else {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleMedium,
                        color = BricxColors.TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            if (subText != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subText,
                    style = MaterialTheme.typography.bodySmall,
                    color = BricxColors.TextGrey,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- THE INNER BOX ---
            val boxModifier = if (isVariableHeight) {
                Modifier.fillMaxWidth()
            } else {
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            }

            Box(
                modifier = boxModifier
                    .background(BricxColors.InnerBoxBg, RoundedCornerShape(4.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}

@Composable
fun BricxGraph(dataPoints: List<Float>, color: Color) {
    if (dataPoints.isEmpty()) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val maxVal = dataPoints.maxOrNull() ?: 1f
        val minVal = dataPoints.minOrNull() ?: 0f
        val range = if ((maxVal - minVal) == 0f) 1f else maxVal - minVal

        val path = Path()

        // Start point
        val startY = height - ((dataPoints[0] - minVal) / range) * height
        path.moveTo(0f, startY)

        // Draw connections
        for (i in 1 until dataPoints.size) {
            val x = (i.toFloat() / (dataPoints.size - 1)) * width
            val y = height - ((dataPoints[i] - minVal) / range) * height
            path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // Gradient Fill
        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(color.copy(alpha = 0.2f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )
    }
}

// ⚡ UPDATED GRID LOGIC: Use Weight to Fill Height & Dynamic Data
@Composable
fun AssetGrid(breakdown: Map<String, Int>) {
    // Default dummy map if empty to preserve layout
    val data = if (breakdown.isEmpty()) mapOf(
        "Office" to 2,
        "Retail" to 1,
        "Warehouse" to 3,
        "Data Ctr" to 1
    ) else breakdown
    val keys = data.keys.toList()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Row 1
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // ⚡ Fill 50% height
        ) {
            val k1 = keys.getOrElse(0) { "Office" }
            val v1 = data[k1] ?: 0
            AssetBox(String.format("%02d", v1), k1, Modifier.weight(1f))

            val k2 = keys.getOrElse(1) { "Retail" }
            val v2 = data[k2] ?: 0
            AssetBox(String.format("%02d", v2), k2, Modifier.weight(1f))
        }
        // Row 2
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // ⚡ Fill 50% height
        ) {
            val k3 = keys.getOrElse(2) { "Warehouse" }
            val v3 = data[k3] ?: 0
            AssetBox(String.format("%02d", v3), k3, Modifier.weight(1f))

            val k4 = keys.getOrElse(3) { "Data Ctr" }
            val v4 = data[k4] ?: 0
            AssetBox(String.format("%02d", v4), k4, Modifier.weight(1f))
        }
    }
}

@Composable
fun AssetBox(number: String, label: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight() // ⚡ Fill the row height
            .background(Color(0xFF131B26), RoundedCornerShape(4.dp)),
        // Removed fixed vertical padding, rely on centering
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = number,
                color = BricxColors.GraphGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label.take(8), // Safety truncate
                color = BricxColors.TextGrey,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                lineHeight = 10.sp
            )
        }
    }
}

@Composable
fun TimelineRow(date: String, title: String, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Date
        Text(
            text = date,
            color = BricxColors.TextWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(45.dp)
        )

        // Center: Title (CENTERED)
        Text(
            text = title,
            color = BricxColors.TextGrey,
            fontSize = 11.sp,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            textAlign = TextAlign.Center
        )

        // Right: Amount
        Text(
            text = amount,
            color = BricxColors.GraphGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ⚡ HELPER for Formatting
fun formatCurrency(amount: Double): String {
    return try {
        when {
            amount >= 10000000 -> {
                val cr = amount / 10000000
                String.format("₹%.2f Cr", cr)
            }

            amount >= 100000 -> {
                val l = amount / 100000
                String.format("₹%.2f L", l)
            }

            else -> {
                val formatter = NumberFormat.getInstance(Locale("en", "IN"))
                formatter.maximumFractionDigits = 0
                "₹" + formatter.format(amount)
            }
        }
    } catch (e: Exception) {
        "₹0"
    }
}
