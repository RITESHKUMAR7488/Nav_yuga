// main/java/com/example/mahayuga/feature/navyuga/presentation/portfolio/PortfolioScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.portfolio

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
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
import com.example.mahayuga.core.common.*
import com.example.mahayuga.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)
@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel = hiltViewModel(),
    onNavigateToSmReitDetail: (String) -> Unit = {},
    onNavigateToReitDetail: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    // Pager Setup for Horizontal Swiping
    val mainTabsList = listOf("Holdings", "Positions")
    val pagerState = rememberPagerState(pageCount = { mainTabsList.size })
    val coroutineScope = rememberCoroutineScope()

    var filterTab by remember { mutableIntStateOf(0) }
    val filterTabsList = listOf("All", "SM REITs", "REITs")

    Scaffold(
        containerColor = BricxBackground,
        topBar = {
            Column(
                modifier = Modifier
                    .background(BricxBackground)
                    .statusBarsPadding()
            ) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PieChart,
                                contentDescription = "Portfolio",
                                tint = BricxBrandTeal,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Portfolio",
                                color = BricxTextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BricxBackground,
                        scrolledContainerColor = BricxBackground
                    )
                )

                HorizontalDivider(color = BricxSurfaceCardLight.copy(alpha = 0.5f))

                // TabRow synchronized with Pager
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = BricxBackground,
                    contentColor = BricxTextPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = BricxBrandTeal
                        )
                    },
                    divider = { HorizontalDivider(color = BricxSurfaceCardLight) }
                ) {
                    mainTabsList.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 16.sp,
                                    color = if (pagerState.currentPage == index) BricxTextPrimary else BricxTextSecondary,
                                    fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
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
                CircularProgressIndicator(color = BricxBrandTeal)
            }
        } else {
            // Horizontal Pager allows swiping between Holdings and Positions
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) { page ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    if (page == 0) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // ⚡ THE NEW 3D PIE CHART
                                Box(
                                    modifier = Modifier.size(260.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    PortfolioTrue3DDonutChart(
                                        values = listOf(state.smReitPercent, state.reitPercent),
                                        colors = listOf(ChartSkyBlue, ChartBlue),
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // ⚡ FIX: All inner text is now hardcoded to pure bright White
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.offset(y = (-15).dp)
                                    ) {
                                        Text(
                                            "Total Value",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            state.portfolioValue,
                                            color = Color.White,
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                state.dailyChangeValue,
                                                color = Color.White,
                                                fontSize = 13.sp, fontWeight = FontWeight.Bold
                                            )
                                            Text(" | ", color = Color.White, fontSize = 13.sp)
                                            Text(
                                                state.dailyChangePercent,
                                                color = Color.White,
                                                fontSize = 13.sp, fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    LegendBox(
                                        "SM REITs",
                                        "${state.smReitPercent.toInt()}%",
                                        ChartSkyBlue
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    LegendBox("REITs", "${state.reitPercent.toInt()}%", ChartBlue)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item {
                            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                                Text(
                                    "Holdings Summary",
                                    color = BricxTextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    DataMetricRow("Properties", state.propertiesCount)
                                    Box(Modifier
                                        .width(1.dp)
                                        .height(30.dp)
                                        .background(BricxBorder))
                                    DataMetricRow("Invested", state.totalInvested)
                                    Box(Modifier
                                        .width(1.dp)
                                        .height(30.dp)
                                        .background(BricxBorder))
                                    DataMetricRow("Total sq. ft.", state.totalSqFt)
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    DataMetricRow("Dividend", state.totalDividend)
                                    Box(Modifier
                                        .width(1.dp)
                                        .height(30.dp)
                                        .background(BricxBorder))
                                    DataMetricRow("Avg ROI", state.avgRoi)
                                    Box(Modifier
                                        .width(1.dp)
                                        .height(30.dp)
                                        .background(BricxBorder))
                                    DataMetricRow("Growth", state.totalGrowth)
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }

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
                                        color = if (isSelected) BricxTextPrimary else BricxTextSecondary,
                                        fontSize = 16.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.clickable { filterTab = index }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

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
                                    Text(
                                        "No holdings found in this category.",
                                        color = BricxTextSecondary
                                    )
                                }
                            }
                        } else {
                            items(filteredHoldings) { holding ->
                                HoldingItemCard(
                                    holding = holding,
                                    onClick = {
                                        // Specific Navigation Based on Asset Type
                                        if (holding.type == "SM REIT") {
                                            onNavigateToSmReitDetail(holding.symbol)
                                        } else {
                                            onNavigateToReitDetail(holding.symbol)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                    } else { // Positions Tab
                        if (state.positions.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) { Text("No active orders found.", color = BricxTextSecondary) }
                            }
                        } else {
                            item {
                                Text(
                                    "Pending Orders",
                                    color = BricxTextPrimary,
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
}

// ---------------- CUSTOM COMPONENTS ----------------

@Composable
fun PortfolioTrue3DDonutChart(
    values: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val total = values.sum()
        if (total == 0f) return@Canvas

        val canvasWidth = size.width
        val canvasHeight = size.height

        // Sizing & 3D Parameters
        val thickness = canvasWidth * 0.22f // Ring thickness
        val depth = canvasWidth * 0.16f     // 3D Extrusion Height
        val tilt = 0.55f                    // Angle of perspective (squish)

        val ovalWidth = canvasWidth - thickness
        val ovalHeight = ovalWidth * tilt
        val topLeftX = thickness / 2f
        val topLeftY = (canvasHeight - ovalHeight - depth) / 2f

        var currentAngle = -90f
        val startAngles = mutableListOf<Float>()
        val sweepAngles = mutableListOf<Float>()
        val gapAngle = 2.5f // Creates the modern slice separation

        for (value in values) {
            val sweep = (value / total) * 360f
            startAngles.add(currentAngle)
            sweepAngles.add(if (sweep > gapAngle) sweep - gapAngle else sweep)
            currentAngle += sweep
        }

        // Draw solid extrusion from bottom up
        val depthInt = depth.toInt()
        for (d in depthInt downTo 0) {
            val isTopFace = (d == 0)
            val yOffset = d.toFloat()

            for (i in values.indices) {
                val baseColor = colors[i]

                val color = if (isTopFace) {
                    baseColor // Bright top face
                } else {
                    // Vertical Gradient lighting for side walls
                    val darknessFactor = 0.65f + (0.25f * (1f - (d.toFloat() / depth)))
                    Color(
                        red = baseColor.red * darknessFactor,
                        green = baseColor.green * darknessFactor,
                        blue = baseColor.blue * darknessFactor,
                        alpha = baseColor.alpha
                    )
                }

                drawArc(
                    color = color,
                    startAngle = startAngles[i],
                    sweepAngle = sweepAngles[i],
                    useCenter = false,
                    topLeft = Offset(topLeftX, topLeftY + yOffset),
                    size = Size(ovalWidth, ovalHeight),
                    style = Stroke(width = thickness, cap = StrokeCap.Butt)
                )
            }
        }
    }
}

@Composable
fun HoldingItemCard(holding: NewPortfolioHolding, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BricxSurfaceCardLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        holding.name,
                        color = BricxTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        holding.location,
                        color = BricxTextSecondary,
                        fontSize = 12.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Current Value", color = BricxTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        holding.currentValue,
                        color = BricxTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1.15f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    InlineStatText("Invested", holding.invested)
                    InlineStatText("Units", holding.totalUnits)
                    InlineStatText("Buy Price", holding.buyPrice)
                    InlineStatText("Current Price", holding.currentPrice)
                }

                Spacer(modifier = Modifier.width(32.dp))

                Box(
                    modifier = Modifier
                        .weight(0.85f)
                        .height(100.dp)
                        .background(BricxBackground, RoundedCornerShape(8.dp))
                        .border(1.dp, BricxSurfaceCardLight, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    SparklineGraph(
                        data = holding.priceHistory,
                        color = if (holding.isPositiveGrowth) BricxBrandTeal else BricxDangerRed,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BricxSurfaceCardLight)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Growth", color = BricxTextSecondary, fontSize = 13.sp)
                Text(
                    text = "${if (holding.isPositiveGrowth) "+" else ""}${holding.growth}",
                    color = if (holding.isPositiveGrowth) BricxBrandTeal else BricxDangerRed,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LegendBox(title: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .border(1.dp, BricxSurfaceCardLight, RoundedCornerShape(8.dp))
            .background(BricxSurfaceCard, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(color, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = BricxTextSecondary, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = BricxTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PositionItemCard(position: PortfolioPosition) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BricxSurfaceCardLight)
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
                        color = if (position.orderType == "BUY") BricxBrandTeal.copy(0.2f) else BricxDangerRed.copy(
                            0.2f
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = position.orderType,
                            color = if (position.orderType == "BUY") BricxBrandTeal else BricxDangerRed,
                            fontSize = 10.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        position.name,
                        color = BricxTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Qty: ${position.quantity} @ ${position.orderPrice}",
                    color = BricxTextSecondary,
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    position.status,
                    color = BricxWarningOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun InlineStatText(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = BricxTextPrimary,
            fontSize = 13.sp
        )
        Text(
            text = value,
            color = BricxTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}