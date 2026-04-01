// main/java/com/example/mahayuga/feature/navyuga/presentation/trade/ReitDetailScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.trade

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mahayuga.core.common.* // IMPORTING OUR COMPONENTS
import com.example.mahayuga.feature.navyuga.presentation.detail.ReitDetailState
import com.example.mahayuga.feature.navyuga.presentation.detail.ReitDetailViewModel
import com.example.mahayuga.ui.theme.* // IMPORTING OUR COLORS
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReitDetailScreen(
    assetId: String,
    onNavigateBack: () -> Unit = {},
    onRoiClick: () -> Unit = {},
    navController: NavController? = null,
    viewModel: ReitDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val isWatchlisted by viewModel.isWatchlisted.collectAsState()
    val coroutineScope = rememberCoroutineScope() // Coroutine scope for pager animations

    var isNse by remember { mutableStateOf(true) }
    val tabs = listOf("Estate", "Finance", "News", "Media")
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    LaunchedEffect(assetId) {
        viewModel.fetchAssetDetails(assetId)
    }

    Scaffold(
        containerColor = BricxBackground,
        topBar = {
            // USING OUR NEW COMPONENT
            BricxTopAppBar(
                title = "Overview",
                onNavigateBack = { if (navController != null) navController.popBackStack() else onNavigateBack() },
                showTrailingIcons = true,
                isWatchlisted = isWatchlisted,
                onShareClick = { Toast.makeText(context, "Sharing...", Toast.LENGTH_SHORT).show() },
                onWatchlistClick = {
                    viewModel.toggleWatchlist()
                    Toast.makeText(
                        context,
                        if (isWatchlisted) "Removed" else "Saved",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        },
        floatingActionButton = {
            if (pagerState.currentPage == 1) {
                FloatingActionButton(
                    onClick = onRoiClick,
                    containerColor = BricxSurfaceCard,
                    contentColor = BricxBrandTeal,
                    shape = CircleShape,
                    modifier = Modifier.border(1.dp, BricxBorder, CircleShape)
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = "Calculate ROI")
                }
            }
        },
        bottomBar = {
            // USING OUR NEW COMPONENT
            StickyTradeBottomBar(
                onSipClick = { Toast.makeText(context, "Start SIP", Toast.LENGTH_SHORT).show() },
                onSellClick = { Toast.makeText(context, "Sell Order", Toast.LENGTH_SHORT).show() },
                onBuyClick = { Toast.makeText(context, "Buy Order", Toast.LENGTH_SHORT).show() }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is ReitDetailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BricxBrandTeal)
                }
            }

            is ReitDetailState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message, color = BricxDangerRed)
                }
            }

            is ReitDetailState.Success -> {
                val data = state.data
                val priceColor = if (data.isPositive) BricxSuccessGreen else BricxDangerRed

                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)) {
                    // Fixed Header Details
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)) {
                        Text(
                            text = data.name,
                            color = BricxTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Multiple Locations, India",
                            color = BricxTextSecondary,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "₹${
                                        String.format(
                                            Locale.US,
                                            "%.2f",
                                            data.currentPrice
                                        )
                                    }",
                                    color = BricxTextPrimary,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${if (data.isPositive) "+" else ""}₹${
                                            String.format(
                                                Locale.US,
                                                "%.2f",
                                                Math.abs(data.priceChange)
                                            )
                                        }",
                                        color = priceColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(" | ", color = BricxTextSecondary, fontSize = 12.sp)
                                    Text(
                                        text = "${if (data.isPositive) "+" else ""}${
                                            String.format(
                                                Locale.US,
                                                "%.2f",
                                                data.percentageChange
                                            )
                                        }%",
                                        color = priceColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "1D",
                                        color = BricxTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .border(1.dp, BricxBorder, RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { isNse = !isNse }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isNse) "NSE" else "BSE",
                                    color = BricxTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.SwapVert,
                                    contentDescription = "Switch Market",
                                    tint = BricxTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Fixed Tabs
                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = BricxBackground,
                        contentColor = BricxTextPrimary,
                        edgePadding = 16.dp,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                color = BricxBrandTeal
                            )
                        },
                        divider = { HorizontalDivider(color = BricxBorder) }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                // HERE IS THE COROUTINE: Launching a suspend function to animate scroll smoothly
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(
                                            index
                                        )
                                    }
                                },
                                text = {
                                    Text(
                                        title,
                                        fontSize = 15.sp,
                                        color = if (pagerState.currentPage == index) BricxTextPrimary else BricxTextSecondary,
                                        fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            )
                        }
                    }

                    // Swipeable Pager
                    HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            when (page) {
                                0 -> { // ESTATE TAB
                                    item {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            // USING OUR NEW COMPONENT
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                DataMetricRow("Market Cap", "₹33,850 Cr")
                                                Box(
                                                    Modifier
                                                        .width(1.dp)
                                                        .height(30.dp)
                                                        .background(BricxBorder)
                                                )
                                                DataMetricRow("Dividend", "6.7%")
                                                Box(
                                                    Modifier
                                                        .width(1.dp)
                                                        .height(30.dp)
                                                        .background(BricxBorder)
                                                )
                                                DataMetricRow("52-Week High", "₹435.20")
                                                Box(
                                                    Modifier
                                                        .width(1.dp)
                                                        .height(30.dp)
                                                        .background(BricxBorder)
                                                )
                                                DataMetricRow("52-Week Low", "₹366.00")
                                            }

                                            Spacer(modifier = Modifier.height(16.dp))

                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, BricxBorder)
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .padding(16.dp)
                                                        .fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            "Portfolio",
                                                            color = BricxTextPrimary,
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            "Total Portfolio Value",
                                                            color = BricxTextSecondary,
                                                            fontSize = 12.sp
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            "₹48,800 Cr",
                                                            color = BricxTextPrimary,
                                                            fontSize = 18.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp))
                                                        Text(
                                                            "Total Holdings: 42.6 MSF",
                                                            color = BricxBrandTeal,
                                                            fontSize = 12.sp
                                                        )
                                                        Text(
                                                            "Under Development: +8.1 MSF",
                                                            color = BricxBrandTeal,
                                                            fontSize = 12.sp
                                                        )
                                                        Text(
                                                            "Occupancy: 86%",
                                                            color = BricxBrandTeal,
                                                            fontSize = 12.sp
                                                        )
                                                    }
                                                    // USING OUR NEW DYNAMIC DONUT CHART COMPONENT
                                                    PortfolioDonutChart(
                                                        values = listOf(70f, 30f),
                                                        colors = listOf(ChartBlue, ChartPeach),
                                                        modifier = Modifier.size(90.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(24.dp))
                                            Text(
                                                "All Properties",
                                                color = BricxTextPrimary,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                        }
                                    }

                                    item {
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            contentPadding = PaddingValues(horizontal = 16.dp)
                                        ) {
                                            items(2) { index ->
                                                val title =
                                                    if (index == 0) "Manyata Business Park,\nBengaluru" else "TechVillage,\nBengaluru"
                                                // USING OUR NEW COMPONENT
                                                PropertyMiniCard(
                                                    propertyName = data.name,
                                                    title = title,
                                                    openPrice = data.openPrice.toString(),
                                                    lastPrice = data.currentPrice.toString()
                                                )
                                            }
                                        }
                                    }
                                }

                                1 -> { // FINANCE TAB
                                    item {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            // The Graph
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(200.dp)
                                                    .background(BricxSurfaceCard, RoundedCornerShape(12.dp))
                                                    .border(1.dp, BricxBorder, RoundedCornerShape(12.dp))
                                                    .padding(12.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Fullscreen,
                                                    contentDescription = "Expand Graph",
                                                    tint = BricxTextSecondary,
                                                    modifier = Modifier.align(Alignment.TopEnd).size(24.dp).clickable {
                                                        Toast.makeText(context, "Full Graph", Toast.LENGTH_SHORT).show()
                                                    }
                                                )

                                                Canvas(
                                                    modifier = Modifier.fillMaxSize().padding(end = 40.dp, top = 20.dp, bottom = 20.dp)
                                                ) {
                                                    if (data.chartPoints.isNotEmpty()) {
                                                        val path = Path()
                                                        val stepX = size.width / (data.chartPoints.size - 1)

                                                        data.chartPoints.forEachIndexed { index, point ->
                                                            val x = index * stepX
                                                            val normalizedY = 1f - ((point.second - 400f) / 100f).coerceIn(0f, 1f)
                                                            val y = normalizedY * size.height

                                                            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                                        }
                                                        drawPath(path, color = priceColor, style = Stroke(width = 4f))
                                                    }
                                                }

                                                Column(
                                                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(top = 20.dp, bottom = 20.dp),
                                                    verticalArrangement = Arrangement.SpaceBetween,
                                                    horizontalAlignment = Alignment.End
                                                ) {
                                                    Text("₹500", color = BricxTextSecondary, fontSize = 10.sp)
                                                    Text("₹450", color = BricxTextSecondary, fontSize = 10.sp)
                                                    Text("₹400", color = BricxTextSecondary, fontSize = 10.sp)
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                                listOf("1D", "1W", "1M", "1Y", "5Y").forEach { tf ->
                                                    Text(text = tf, color = if (tf == "1D") BricxBrandTeal else BricxTextSecondary, fontWeight = if (tf == "1D") FontWeight.Bold else FontWeight.Normal)
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(24.dp))
                                            // The 4 Restored Sections
                                            ExpandableFinanceSection(
                                                title = "Performance",
                                                data = listOf("Today's Low" to "₹${data.dayLow}", "Today's High" to "₹${data.dayHigh}", "Open" to "₹${data.openPrice}", "Prev. Close" to "₹${data.previousClose}", "Volume" to data.volume, "Avg. Traded Price" to "₹${data.averageTradedPrice}")
                                            )
                                            ExpandableFinanceSection(
                                                title = "Market Fundamentals",
                                                data = listOf("Market Cap" to "₹36,102 Cr", "P/E Ratio" to "24.5", "P/B Ratio" to "1.2", "Dividend Yield" to "6.5%")
                                            )
                                            ExpandableFinanceSection(
                                                title = "Financials (Q3)",
                                                data = listOf("Revenue" to "₹850 Cr", "Net Profit" to "₹210 Cr", "EBITDA Margin" to "82%", "Debt to Equity" to "0.35")
                                            )
                                            ExpandableFinanceSection(
                                                title = "Shareholding Pattern",
                                                data = listOf("Promoters" to "15.2%", "FIIs" to "32.4%", "DIIs" to "45.1%", "Public" to "7.3%")
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}