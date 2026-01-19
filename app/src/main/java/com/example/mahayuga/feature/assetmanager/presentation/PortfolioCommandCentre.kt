package com.example.mahayuga.feature.assetmanager.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- DESIGN SYSTEM CONSTANTS (Based on your Uploaded Image) ---
private object AMDesign {
    val BgGradientStart = Color(0xFF071226)
    val BgGradientEnd = Color(0xFF061123)
    val CardBg = Color(0xFF111c30)
    val TextWhite = Color(0xFFFFFFFF)
    val TextGrey = Color(0xFFAFAFAF)
    val HighlightGreen = Color(0xFF38a882)
}

@Composable
fun PortfolioCommandCentre() {
    // Scrollable container with the Gradient Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(AMDesign.BgGradientStart, AMDesign.BgGradientEnd)
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- HEADER ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "BRICX",
                    color = AMDesign.TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Portfolio Command Centre",
                    color = AMDesign.TextGrey,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- ROW 1: AUM & ASSET GRID ---
            Row(
                modifier = Modifier.height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Total AUM Card
                DashboardCard(modifier = Modifier.weight(1f)) {
                    Column(Modifier.fillMaxSize()) {
                        Text("Total AUM (Live)", color = AMDesign.TextGrey, fontSize = 12.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("₹6,250 Cr", color = AMDesign.TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                        Spacer(Modifier.weight(1f))

                        // Fake Green Graph
                        SimpleGreenGraph(
                            dataPoints = listOf(0.1f, 0.25f, 0.4f, 0.35f, 0.6f, 0.8f, 1.0f)
                        )
                    }
                }

                // 2. Asset Grid Card
                DashboardCard(modifier = Modifier.weight(1f)) {
                    Column(Modifier.fillMaxSize()) {
                        Text("Asset Under Management", color = AMDesign.TextGrey, fontSize = 12.sp)
                        Spacer(Modifier.height(12.dp))

                        // 3x2 Grid for Asset Types
                        val assets = listOf("Office", "Retail", "Warehouse", "Land", "Data Centre", "Residential")
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            userScrollEnabled = false,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(assets) { type ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("01", color = AMDesign.HighlightGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(type, color = AMDesign.TextWhite, fontSize = 8.sp, textAlign = TextAlign.Center, lineHeight = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            // --- ROW 2: CASH INFLOW & OBLIGATIONS ---
            Row(
                modifier = Modifier.height(140.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cash Inflow
                DashboardCard(modifier = Modifier.weight(1f)) {
                    Column(Modifier.fillMaxSize()) {
                        Text("Cash Inflow", color = AMDesign.TextGrey, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("₹23.4 Cr", color = AMDesign.TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(4.dp))
                            Text("(This month)", color = AMDesign.TextGrey, fontSize = 10.sp)
                        }
                        Spacer(Modifier.weight(1f))
                        SimpleGreenGraph(dataPoints = listOf(0.2f, 0.3f, 0.25f, 0.5f, 0.7f))
                    }
                }

                // Upcoming Obligations
                DashboardCard(modifier = Modifier.weight(1f)) {
                    Column(Modifier.fillMaxSize()) {
                        Text("Upcoming Obligations", color = AMDesign.TextGrey, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("₹6.8 Cr", color = AMDesign.TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(4.dp))
                            Text("(due this month)", color = AMDesign.TextGrey, fontSize = 10.sp)
                        }
                        Spacer(Modifier.weight(1f))
                        SimpleGreenGraph(dataPoints = listOf(0.1f, 0.2f, 0.3f, 0.45f, 0.6f))
                    }
                }
            }

            // --- ROW 3: ROI & IRR ---
            Row(
                modifier = Modifier.height(140.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Projected ROI
                DashboardCard(modifier = Modifier.weight(1f)) {
                    Column(Modifier.fillMaxSize()) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Projected ROI", color = AMDesign.TextGrey, fontSize = 12.sp)
                            Text("08%", color = AMDesign.HighlightGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("Q2 April", color = AMDesign.TextGrey, fontSize = 10.sp)
                        Spacer(Modifier.weight(1f))
                        SimpleGreenGraph(dataPoints = listOf(0.1f, 0.3f, 0.5f, 0.7f, 0.9f))
                    }
                }

                // Projected IRR
                DashboardCard(modifier = Modifier.weight(1f)) {
                    Column(Modifier.fillMaxSize()) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Projected IRR", color = AMDesign.TextGrey, fontSize = 12.sp)
                            Text("14%", color = AMDesign.HighlightGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("Q2 April", color = AMDesign.TextGrey, fontSize = 10.sp)
                        Spacer(Modifier.weight(1f))
                        SimpleGreenGraph(dataPoints = listOf(0.1f, 0.25f, 0.4f, 0.6f, 0.85f))
                    }
                }
            }

            // --- ROW 4: TIMELINE (Table) ---
            DashboardCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Distribution Due Timeline", color = AMDesign.TextGrey, fontSize = 14.sp)
                    Spacer(Modifier.height(16.dp))

                    // Fake Timeline Rows
                    TimelineRow("01 April", "Rent Yield Fund II", "₹23.4 Cr")
                    TimelineRow("01 April", "Rent Yield Fund II", "₹23.4 Cr")
                    TimelineRow("01 April", "Rent Yield Fund II", "₹23.4 Cr")
                    TimelineRow("01 April", "Rent Yield Fund II", "₹23.4 Cr")
                    TimelineRow("01 April", "Rent Yield Fund II", "₹23.4 Cr")
                }
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

// --- HELPER COMPOSABLES ---

@Composable
fun DashboardCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AMDesign.CardBg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            content()
        }
    }
}

@Composable
fun TimelineRow(date: String, fund: String, amount: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = date,
                color = AMDesign.TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = fund,
                color = AMDesign.TextGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(2f)
            )
            Text(
                text = amount,
                color = AMDesign.HighlightGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(8.dp))
        // Dashed/Dotted Line simulation
        Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun SimpleGreenGraph(dataPoints: List<Float>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp) // Fixed height for graph area
    ) {
        if (dataPoints.isEmpty()) return@Canvas

        val path = Path()
        val width = size.width
        val height = size.height
        val stepX = width / (dataPoints.size - 1)

        dataPoints.forEachIndexed { index, ratio ->
            val x = index * stepX
            val y = height - (ratio * height) // Invert Y because Canvas 0,0 is top-left

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = AMDesign.HighlightGreen,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun AssetManagerDashboardPreview() {
    MaterialTheme {
        PortfolioCommandCentre()
    }
}