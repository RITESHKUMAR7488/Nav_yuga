// main/java/com/example/mahayuga/feature/navyuga/presentation/portfolio/PortfolioScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.portfolio

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mahayuga.core.common.*
import com.example.mahayuga.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel = hiltViewModel(),
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var mainTab by remember { mutableIntStateOf(0) }
    val mainTabsList = listOf("Holdings", "Positions")

    var filterTab by remember { mutableIntStateOf(0) }
    val filterTabsList = listOf("All", "SM REITs", "REITs")

    Scaffold(
        containerColor = BricxBackground,
        topBar = {
            Column(modifier = Modifier
                .background(BricxBackground)
                .statusBarsPadding()) {
                BricxHubTopAppBar(
                    title = "Portfolio",
                    icon = Icons.Outlined.PieChart,
                    onSearchClick = {
                        Toast.makeText(context, "Search", Toast.LENGTH_SHORT).show()
                    },
                    onNotificationClick = onNavigateToNotifications,
                    onMessageClick = onNavigateToMessages
                )

                HorizontalDivider(color = BricxSurfaceCardLight.copy(alpha = 0.5f))

                TabRow(
                    selectedTabIndex = mainTab,
                    containerColor = BricxBackground,
                    contentColor = BricxTextPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[mainTab]),
                            color = BricxBrandTeal
                        )
                    },
                    divider = { HorizontalDivider(color = BricxSurfaceCardLight) }
                ) {
                    mainTabsList.forEachIndexed { index, title ->
                        Tab(
                            selected = mainTab == index,
                            onClick = { mainTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 16.sp,
                                    color = if (mainTab == index) BricxTextPrimary else BricxTextSecondary,
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
                CircularProgressIndicator(color = BricxBrandTeal)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                if (mainTab == 0) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Portfolio Value",
                                    color = BricxTextSecondary,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    state.portfolioValue,
                                    color = BricxTextPrimary,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        state.dailyChangeValue,
                                        color = if (state.isPositiveChange) BricxBrandTeal else BricxDangerRed,
                                        fontSize = 14.sp, fontWeight = FontWeight.Medium
                                    )
                                    Text(" | ", color = BricxTextSecondary, fontSize = 14.sp)
                                    Text(
                                        state.dailyChangePercent,
                                        color = if (state.isPositiveChange) BricxBrandTeal else BricxDangerRed,
                                        fontSize = 14.sp, fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    LegendBox(
                                        "SM REITs",
                                        "${state.smReitPercent.toInt()}%",
                                        BricxBrandTeal
                                    )
                                    LegendBox(
                                        "REITs",
                                        "${state.reitPercent.toInt()}%",
                                        BricxBrandBlue
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier.size(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                PortfolioDonutChart(
                                    values = listOf(state.smReitPercent, state.reitPercent),
                                    colors = listOf(ChartTeal, ChartBlue),
                                    modifier = Modifier.fillMaxSize(),
                                    strokeWidthDp = 30f
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Mix", color = BricxTextSecondary, fontSize = 10.sp)
                                    Text(
                                        "100%",
                                        color = BricxTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
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
                            HoldingItemCard(holding = holding)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                } else {
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

@Composable
fun LegendBox(title: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .border(1.dp, BricxSurfaceCardLight, RoundedCornerShape(8.dp))
            .background(BricxSurfaceCard, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, color = BricxTextSecondary, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = BricxTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HoldingItemCard(holding: NewPortfolioHolding) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
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
                    Text(holding.type, color = BricxTextSecondary, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Current Value", color = BricxTextSecondary, fontSize = 10.sp)
                    Text(
                        holding.currentValue,
                        color = BricxTextPrimary,
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
                Column(modifier = Modifier.weight(1f)) {
                    InlineStatText("Invested:", holding.invested)
                    InlineStatText("Units:", holding.totalUnits)
                    InlineStatText("Buy Price:", holding.buyPrice)
                    InlineStatText("Current Price:", holding.currentPrice)
                }

                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(80.dp)
                        .background(BricxBackground, RoundedCornerShape(8.dp))
                        .border(1.dp, BricxSurfaceCardLight, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    SparklineGraph(
                        data = holding.priceHistory,
                        color = if (holding.isPositiveGrowth) BricxBrandTeal else BricxDangerRed,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BricxSurfaceCardLight)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Growth", color = BricxTextSecondary, fontSize = 12.sp)
                Text(
                    text = "${if (holding.isPositiveGrowth) "+" else ""}${holding.growth}",
                    color = if (holding.isPositiveGrowth) BricxBrandTeal else BricxDangerRed,
                    fontSize = 14.sp, fontWeight = FontWeight.Bold
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
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = "$label ", color = BricxTextSecondary, fontSize = 11.sp)
        Text(text = value, color = BricxTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}