// main/java/com/example/mahayuga/feature/navyuga/presentation/portfolio/PortfolioScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.portfolio

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

// --- SPECIFIC UI COLORS FROM MOCKUPS ---
private val PortBg = Color(0xFF040C17)
private val PortCard = Color(0xFF0B1727)
private val PortTeal = Color(0xFF00BFA5) // The vibrant green/teal accent
private val PortTextWhite = Color.White
private val PortTextGrey = Color(0xFF8B9BB4)
private val PortPieBlue = Color(0xFF2854A1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "SM REITs", "REITs")

    Scaffold(
        containerColor = PortBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.PieChart,
                    contentDescription = "Portfolio Icon",
                    tint = PortTeal,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Portfolio",
                    color = PortTextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PortTeal)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // --- 1. Portfolio Value & Pie Chart Section ---
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Portfolio Value",
                                color = PortTextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.portfolioValue,
                                color = PortTextWhite,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = state.dailyChangeValue,
                                    color = if (state.isPositiveChange) PortTeal else Color.Red,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = " | ",
                                    color = PortTextGrey,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = state.dailyChangePercent,
                                    color = if (state.isPositiveChange) PortTeal else Color.Red,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Custom Legend
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("SM REITs", color = PortTextGrey, fontSize = 12.sp)
                                    Text("${state.smReitPercent.toInt()}%", color = PortTeal, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(20.dp))
                                // Vertical divider line
                                Box(modifier = Modifier.height(30.dp).width(1.dp).background(PortTextGrey.copy(alpha = 0.3f)))
                                Spacer(modifier = Modifier.width(20.dp))
                                Column {
                                    Text("REITs", color = PortTextGrey, fontSize = 12.sp)
                                    Text("${state.reitPercent.toInt()}%", color = PortTeal, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Pie Chart
                        AnimatedPieChart(
                            smReitPercent = state.smReitPercent,
                            reitPercent = state.reitPercent
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // --- 2. Holdings Stats Grid ---
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = PortCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 16.dp)) {
                            Text(
                                text = "Holdings",
                                color = PortTextWhite,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 12.dp) // ✅ Chained correctly
                            )

                            HorizontalDivider(color = PortTextGrey.copy(alpha = 0.2f), thickness = 1.dp)

                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                                GridStatItem(state.propertiesCount, "Properties", Modifier.weight(1f))
                                VerticalDivider(color = PortTextGrey.copy(alpha = 0.2f), modifier = Modifier.height(40.dp))
                                GridStatItem(state.totalInvested, "Invested", Modifier.weight(1f))
                                VerticalDivider(color = PortTextGrey.copy(alpha = 0.2f), modifier = Modifier.height(40.dp))
                                GridStatItem(state.totalSqFt, "Total sq. ft.", Modifier.weight(1f))
                            }

                            HorizontalDivider(color = PortTextGrey.copy(alpha = 0.2f), thickness = 1.dp)

                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                                GridStatItem(state.totalDividend, "Dividend", Modifier.weight(1f))
                                VerticalDivider(color = PortTextGrey.copy(alpha = 0.2f), modifier = Modifier.height(40.dp))
                                GridStatItem(state.avgRoi, "Average ROI", Modifier.weight(1f))
                                VerticalDivider(color = PortTextGrey.copy(alpha = 0.2f), modifier = Modifier.height(40.dp))
                                GridStatItem(state.totalGrowth, "Growth", Modifier.weight(1f))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // --- 3. Tabs ---
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        tabs.forEachIndexed { index, title ->
                            val isSelected = selectedTab == index
                            Text(
                                text = title,
                                color = if (isSelected) PortTextWhite else PortTextGrey,
                                fontSize = 18.sp,
                                fontWeight = if (isSelected) FontWeight.Normal else FontWeight.Light,
                                modifier = Modifier.clickable { selectedTab = index }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // --- 4. Holdings List ---
                items(state.holdings) { holding ->
                    HoldingItemCard(holding = holding)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun GridStatItem(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = PortTeal,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = PortTextWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
fun HoldingItemCard(holding: NewPortfolioHolding) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = PortCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Left Column: Details
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = holding.name,
                    color = PortTextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = holding.type,
                    color = PortTextGrey,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )

                Spacer(modifier = Modifier.height(12.dp))

                InlineStatText("Invested: ", holding.invested)
                Spacer(modifier = Modifier.height(4.dp))
                InlineStatText("Total Units: ", holding.totalUnits)

                Spacer(modifier = Modifier.height(12.dp))

                InlineStatText("Price Per Unit: ", holding.pricePerUnit)
                Spacer(modifier = Modifier.height(4.dp))
                InlineStatText("Buy Price: ", holding.buyPrice)
                Spacer(modifier = Modifier.height(4.dp))
                InlineStatText("Current Price: ", holding.currentPrice)
            }

            // Right Column: Chart & Value
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                // Mini Line Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(bottom = 16.dp, start = 16.dp)
                ) {
                    MiniLineChart(
                        data = holding.priceHistory,
                        lineColor = if (holding.isPositiveGrowth) PortTeal else Color.Red
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Current Value", color = PortTextGrey, fontSize = 10.sp)
                        Text(holding.currentValue, color = PortTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Growth", color = PortTextGrey, fontSize = 10.sp)
                        Text(
                            text = holding.growth,
                            color = if (holding.isPositiveGrowth) PortTeal else Color.Red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InlineStatText(label: String, value: String) {
    Row {
        Text(text = label, color = PortTextGrey, fontSize = 12.sp)
        Text(text = value, color = PortTeal, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun AnimatedPieChart(smReitPercent: Float, reitPercent: Float) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(key1 = smReitPercent) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Box(modifier = Modifier.size(110.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val totalAngle = 360f * animationProgress.value
            val smReitAngle = (smReitPercent / 100f) * totalAngle
            val reitAngle = (reitPercent / 100f) * totalAngle

            // Draw White Slice (SM REITs)
            drawArc(
                color = Color.White,
                startAngle = -90f,
                sweepAngle = smReitAngle,
                useCenter = true
            )
            // Draw Blue Slice (REITs)
            drawArc(
                color = PortPieBlue,
                startAngle = -90f + smReitAngle,
                sweepAngle = reitAngle,
                useCenter = true
            )
        }
    }
}

@Composable
fun MiniLineChart(data: List<Float>, lineColor: Color) {
    if (data.isEmpty()) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val maxVal = data.maxOrNull() ?: 1f
        val minVal = data.minOrNull() ?: 0f
        val range = if ((maxVal - minVal) == 0f) 1f else maxVal - minVal

        val path = Path()
        val startY = height - ((data[0] - minVal) / range) * height
        path.moveTo(0f, startY)

        for (i in 1 until data.size) {
            val x = (i.toFloat() / (data.size - 1)) * width
            val y = height - ((data[i] - minVal) / range) * height
            path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}