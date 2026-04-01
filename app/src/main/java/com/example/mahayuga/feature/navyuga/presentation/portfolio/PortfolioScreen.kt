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
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Search
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
import com.example.mahayuga.core.common.* // ⚡ IMPORTED COMMON COMPONENTS
import com.example.mahayuga.feature.navyuga.presentation.home.GroupedHeaderIcons
import com.example.mahayuga.ui.theme.* // ⚡ IMPORTED NEW THEME

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
        containerColor = BricxBackground, // ⚡ UPDATED
        topBar = {
            Column(
                modifier = Modifier
                    .background(BricxBackground) // ⚡ UPDATED
                    .statusBarsPadding()
            ) {
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
                            tint = BricxBrandTeal, // ⚡ UPDATED
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Portfolio",
                            color = BricxTextPrimary, // ⚡ UPDATED
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
                                Icons.Outlined.Notifications to { onNavigateToNotifications() },
                                Icons.AutoMirrored.Outlined.Send to { onNavigateToMessages() }
                            )
                        )
                    }
                }

                HorizontalDivider(color = BricxSurfaceCardLight.copy(alpha = 0.5f)) // ⚡ UPDATED

                TabRow(
                    selectedTabIndex = mainTab,
                    containerColor = BricxBackground, // ⚡ UPDATED
                    contentColor = BricxTextPrimary, // ⚡ UPDATED
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[mainTab]),
                            color = BricxBrandTeal // ⚡ UPDATED
                        )
                    },
                    divider = { HorizontalDivider(color = BricxSurfaceCardLight) } // ⚡ UPDATED
                ) {
                    mainTabsList.forEachIndexed { index, title ->
                        Tab(
                            selected = mainTab == index,
                            onClick = { mainTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 16.sp,
                                    color = if (mainTab == index) BricxTextPrimary else BricxTextSecondary, // ⚡ UPDATED
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
                CircularProgressIndicator(color = BricxBrandTeal) // ⚡ UPDATED
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
                                ) // ⚡ UPDATED
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    state.portfolioValue,
                                    color = BricxTextPrimary, // ⚡ UPDATED
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        state.dailyChangeValue,
                                        color = if (state.isPositiveChange) BricxBrandTeal else BricxDangerRed, // ⚡ UPDATED
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        " | ",
                                        color = BricxTextSecondary,
                                        fontSize = 14.sp
                                    ) // ⚡ UPDATED
                                    Text(
                                        state.dailyChangePercent,
                                        color = if (state.isPositiveChange) BricxBrandTeal else BricxDangerRed, // ⚡ UPDATED
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
                                        BricxBrandTeal
                                    ) // ⚡ UPDATED
                                    LegendBox(
                                        "REITs",
                                        "${state.reitPercent.toInt()}%",
                                        BricxBrandBlue
                                    ) // ⚡ UPDATED
                                }
                            }

                            // ⚡ REPLACED CUSTOM CHART LOGIC WITH BRICX COMMON COMPONENT
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

                    // ⚡ REPLACED GRIDSTATBOX WITH DATAMETRICROW
                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Text(
                                "Holdings Summary",
                                color = BricxTextPrimary, // ⚡ UPDATED
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                DataMetricRow("Properties", state.propertiesCount)
                                Box(
                                    Modifier
                                        .width(1.dp)
                                        .height(30.dp)
                                        .background(BricxBorder)
                                ) // Separator
                                DataMetricRow("Invested", state.totalInvested)
                                Box(
                                    Modifier
                                        .width(1.dp)
                                        .height(30.dp)
                                        .background(BricxBorder)
                                ) // Separator
                                DataMetricRow("Total sq. ft.", state.totalSqFt)
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                DataMetricRow("Dividend", state.totalDividend)
                                Box(
                                    Modifier
                                        .width(1.dp)
                                        .height(30.dp)
                                        .background(BricxBorder)
                                ) // Separator
                                DataMetricRow("Avg ROI", state.avgRoi)
                                Box(
                                    Modifier
                                        .width(1.dp)
                                        .height(30.dp)
                                        .background(BricxBorder)
                                ) // Separator
                                DataMetricRow("Growth", state.totalGrowth)
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
                                    color = if (isSelected) BricxTextPrimary else BricxTextSecondary, // ⚡ UPDATED
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
                                Text(
                                    "No holdings found in this category.",
                                    color = BricxTextSecondary
                                ) // ⚡ UPDATED
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
                                Text(
                                    "No active orders found.",
                                    color = BricxTextSecondary
                                ) // ⚡ UPDATED
                            }
                        }
                    } else {
                        item {
                            Text(
                                "Pending Orders",
                                color = BricxTextPrimary, // ⚡ UPDATED
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
            .border(1.dp, BricxSurfaceCardLight, RoundedCornerShape(8.dp)) // ⚡ UPDATED
            .background(BricxSurfaceCard, RoundedCornerShape(8.dp)) // ⚡ UPDATED
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
                Text(title, color = BricxTextSecondary, fontSize = 10.sp) // ⚡ UPDATED
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                color = BricxTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            ) // ⚡ UPDATED
        }
    }
}

@Composable
fun HoldingItemCard(holding: NewPortfolioHolding) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard), // ⚡ UPDATED
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BricxSurfaceCardLight) // ⚡ UPDATED
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        holding.name,
                        color = BricxTextPrimary, // ⚡ UPDATED
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(holding.type, color = BricxTextSecondary, fontSize = 12.sp) // ⚡ UPDATED
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Current Value", color = BricxTextSecondary, fontSize = 10.sp) // ⚡ UPDATED
                    Text(
                        holding.currentValue,
                        color = BricxTextPrimary, // ⚡ UPDATED
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

                // ⚡ REPLACED CUSTOM SPARKLINE WITH BRICX COMMON COMPONENT
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(80.dp)
                        .background(BricxBackground, RoundedCornerShape(8.dp)) // ⚡ UPDATED
                        .border(1.dp, BricxSurfaceCardLight, RoundedCornerShape(8.dp)) // ⚡ UPDATED
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
            HorizontalDivider(color = BricxSurfaceCardLight) // ⚡ UPDATED
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Growth", color = BricxTextSecondary, fontSize = 12.sp) // ⚡ UPDATED
                Text(
                    text = "${if (holding.isPositiveGrowth) "+" else ""}${holding.growth}",
                    color = if (holding.isPositiveGrowth) BricxBrandTeal else BricxDangerRed, // ⚡ UPDATED
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
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard), // ⚡ UPDATED
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BricxSurfaceCardLight) // ⚡ UPDATED
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
                        ), // ⚡ UPDATED
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = position.orderType,
                            color = if (position.orderType == "BUY") BricxBrandTeal else BricxDangerRed, // ⚡ UPDATED
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        position.name,
                        color = BricxTextPrimary, // ⚡ UPDATED
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Qty: ${position.quantity} @ ${position.orderPrice}",
                    color = BricxTextSecondary, // ⚡ UPDATED
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    position.status,
                    color = BricxWarningOrange, // ⚡ UPDATED
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
        Text(text = "$label ", color = BricxTextSecondary, fontSize = 11.sp) // ⚡ UPDATED
        Text(
            text = value,
            color = BricxTextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        ) // ⚡ UPDATED
    }
}