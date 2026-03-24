// main/java/com/example/mahayuga/feature/navyuga/presentation/portfolio/PortfolioScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.portfolio

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mahayuga.feature.navyuga.presentation.home.GroupedHeaderIcons

// --- UI COLORS ---
private val PortBg = Color(0xFF080F18)
private val PortCard = Color(0xFF0F1722)
private val PortCardLighter = Color(0xFF1A2A40)
private val PortTeal = Color(0xFF00BFA5)
private val PortTextWhite = Color.White
private val PortTextGrey = Color(0xFF8B9BB4)
private val PortPieBlue = Color(0xFF2979FF)
private val PortPieTeal = Color(0xFF00BFA5) // Replaced white with Teal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Main Tabs (Holdings vs Positions)
    var mainTab by remember { mutableIntStateOf(0) }
    val mainTabsList = listOf("Holdings", "Positions")

    // Sub Tabs for Holdings (All, SM REITs, REITs)
    var filterTab by remember { mutableIntStateOf(0) }
    val filterTabsList = listOf("All", "SM REITs", "REITs")

    Scaffold(
        containerColor = PortBg,
        topBar = {
            Column(
                modifier = Modifier
                    .background(PortBg)
                    .statusBarsPadding()
            ) {
                // 1. Header with Icons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GroupedHeaderIcons(
                            listOf(Icons.Outlined.Search to {
                                Toast.makeText(
                                    context,
                                    "Search",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                        )
                        GroupedHeaderIcons(
                            listOf(
                                Icons.Outlined.Send to {
                                    Toast.makeText(
                                        context,
                                        "Messages coming soon",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                Icons.Outlined.Notifications to {
                                    Toast.makeText(
                                        context,
                                        "No new notifications",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        )
                    }
                }

                HorizontalDivider(color = PortCardLighter.copy(alpha = 0.5f))

                // 2. Holdings vs Positions Tabs
                TabRow(
                    selectedTabIndex = mainTab,
                    containerColor = PortBg,
                    contentColor = PortTextWhite,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[mainTab]),
                            color = PortTeal
                        )
                    },
                    divider = { HorizontalDivider(color = PortCardLighter) }
                ) {
                    mainTabsList.forEachIndexed { index, title ->
                        Tab(
                            selected = mainTab == index,
                            onClick = { mainTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 16.sp,
                                    color = if (mainTab == index) PortTextWhite else PortTextGrey,
                                    fontWeight = if (mainTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
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
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                if (mainTab == 0) {
                    // --- HOLDINGS CONTENT ---
                    item {
                        // Value & Pie Chart
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Portfolio Value", color = PortTextGrey, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    state.portfolioValue,
                                    color = PortTextWhite,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        state.dailyChangeValue,
                                        color = if (state.isPositiveChange) PortTeal else Color.Red,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(" | ", color = PortTextGrey, fontSize = 14.sp)
                                    Text(
                                        state.dailyChangePercent,
                                        color = if (state.isPositiveChange) PortTeal else Color.Red,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Legend Boxes
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    LegendBox(
                                        "SM REITs",
                                        "${state.smReitPercent.toInt()}%",
                                        PortPieTeal
                                    )
                                    LegendBox("REITs", "${state.reitPercent.toInt()}%", PortPieBlue)
                                }
                            }

                            // Donut Chart with text inside
                            LabeledDonutChart(
                                smReitPercent = state.smReitPercent,
                                reitPercent = state.reitPercent
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Individual Stat Boxes Grid
                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Text(
                                "Holdings Summary",
                                color = PortTextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                GridStatBox(
                                    state.propertiesCount,
                                    "Properties",
                                    Modifier.weight(1f)
                                )
                                GridStatBox(state.totalInvested, "Invested", Modifier.weight(1f))
                                GridStatBox(state.totalSqFt, "Total sq. ft.", Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                GridStatBox(state.totalDividend, "Dividend", Modifier.weight(1f))
                                GridStatBox(state.avgRoi, "Avg ROI", Modifier.weight(1f))
                                GridStatBox(state.totalGrowth, "Growth", Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // Filter Tabs
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            filterTabsList.forEachIndexed { index, title ->
                                val isSelected = filterTab == index
                                Text(
                                    text = title,
                                    color = if (isSelected) PortTextWhite else PortTextGrey,
                                    fontSize = 16.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.clickable { filterTab = index }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Holdings List
                    val filteredHoldings = when (filterTab) {
                        1 -> state.holdings.filter { it.type == "SM REIT" }
                        2 -> state.holdings.filter { it.type == "REIT" }
                        else -> state.holdings
                    }

                    if (filteredHoldings.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No holdings found in this category.", color = PortTextGrey)
                            }
                        }
                    } else {
                        items(filteredHoldings) { holding ->
                            HoldingItemCard(holding = holding)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                } else {
                    // --- POSITIONS CONTENT ---
                    if (state.positions.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No active orders found.", color = PortTextGrey)
                            }
                        }
                    } else {
                        item {
                            Text(
                                "Pending Orders",
                                color = PortTextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(
                                    start = 24.dp,
                                    top = 16.dp,
                                    bottom = 16.dp
                                )
                            )
                        }
                        items(state.positions) { position ->
                            PositionItemCard(position)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun LegendBox(title: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .border(1.dp, PortCardLighter, RoundedCornerShape(8.dp))
            .background(PortCard, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, color = PortTextGrey, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = PortTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GridStatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(PortCard, RoundedCornerShape(8.dp))
            .border(1.dp, PortCardLighter, RoundedCornerShape(8.dp))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = PortTeal, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, color = PortTextGrey, fontSize = 11.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun LabeledDonutChart(smReitPercent: Float, reitPercent: Float) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(key1 = smReitPercent) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val totalAngle = 360f * animationProgress.value
            val smReitAngle = (smReitPercent / 100f) * totalAngle
            val reitAngle = (reitPercent / 100f) * totalAngle

            // Draw Teal Slice
            drawArc(
                color = PortPieTeal,
                startAngle = -90f,
                sweepAngle = smReitAngle,
                useCenter = true
            )
            // Draw Blue Slice
            drawArc(
                color = PortPieBlue,
                startAngle = -90f + smReitAngle,
                sweepAngle = reitAngle,
                useCenter = true
            )
            // Center Cutout to make it a Donut and hold text
            drawCircle(color = PortBg, radius = size.minDimension / 2.8f)
        }

        // Text inside the Donut hole
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Mix", color = PortTextGrey, fontSize = 10.sp)
            Text("100%", color = PortTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HoldingItemCard(holding: NewPortfolioHolding) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = PortCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PortCardLighter)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        holding.name,
                        color = PortTextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(holding.type, color = PortTextGrey, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Current Value", color = PortTextGrey, fontSize = 10.sp)
                    Text(
                        holding.currentValue,
                        color = PortTextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Details
                Column(modifier = Modifier.weight(1f)) {
                    InlineStatText("Invested:", holding.invested)
                    InlineStatText("Units:", holding.totalUnits)
                    InlineStatText("Buy Price:", holding.buyPrice)
                    InlineStatText("Current Price:", holding.currentPrice)
                }

                // Right Chart Box
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(80.dp)
                        .background(PortBg, RoundedCornerShape(8.dp))
                        .border(1.dp, PortCardLighter, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    WeeklyStockChart(
                        data = holding.priceHistory,
                        lineColor = if (holding.isPositiveGrowth) PortTeal else Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = PortCardLighter)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Growth", color = PortTextGrey, fontSize = 12.sp)
                Text(
                    text = "${if (holding.isPositiveGrowth) "+" else ""}${holding.growth}",
                    color = if (holding.isPositiveGrowth) PortTeal else Color.Red,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PositionItemCard(position: PortfolioPosition) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = PortCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PortCardLighter)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (position.orderType == "BUY") PortTeal.copy(0.2f) else Color.Red.copy(
                            0.2f
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = position.orderType,
                            color = if (position.orderType == "BUY") PortTeal else Color.Red,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        position.name,
                        color = PortTextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Qty: ${position.quantity} @ ${position.orderPrice}",
                    color = PortTextGrey,
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    position.status,
                    color = Color(0xFFFFB300),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun InlineStatText(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = "$label ", color = PortTextGrey, fontSize = 11.sp)
        Text(text = value, color = PortTextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun WeeklyStockChart(data: List<Float>, lineColor: Color) {
    if (data.isEmpty()) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val maxVal = data.maxOrNull() ?: 1f
        val minVal = data.minOrNull() ?: 0f
        val range = if ((maxVal - minVal) == 0f) 1f else maxVal - minVal

        // Draw Grid Lines (Weekly representation)
        val gridLinesX = 4
        val gridLinesY = 3
        val gridColor = Color.White.copy(alpha = 0.05f)

        for (i in 0..gridLinesX) {
            val x = i * (width / gridLinesX)
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1.dp.toPx()
            )
        }
        for (i in 0..gridLinesY) {
            val y = i * (height / gridLinesY)
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw Line Chart
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