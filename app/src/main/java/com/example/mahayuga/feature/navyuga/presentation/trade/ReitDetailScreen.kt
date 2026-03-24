package com.example.mahayuga.feature.navyuga.presentation.trade

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.mahayuga.feature.navyuga.presentation.detail.ReitDetailState
import com.example.mahayuga.feature.navyuga.presentation.detail.ReitDetailViewModel
import kotlinx.coroutines.launch

// --- THEME COLORS ---
private val BgDark = Color(0xFF080F18)
private val CardDark = Color(0xFF0F1722)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF8B9BB4)
private val BuyTeal = Color(0xFF14B8A6)
private val SellOrange = Color(0xFFF97316)

@Composable
fun ReitDetailScreen(
    assetId: String,
    navController: NavController,
    viewModel: ReitDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isWatchlisted by viewModel.isWatchlisted.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(assetId) {
        viewModel.fetchAssetDetails(assetId)
    }

    Scaffold(
        containerColor = BgDark,
        bottomBar = {
            if (uiState is ReitDetailState.Success) {
                ReitBottomActionBar()
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ReitDetailState.Loading -> {
                    CircularProgressIndicator(
                        color = BuyTeal,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ReitDetailState.Error -> {
                    Text(
                        text = state.message,
                        color = SellOrange,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ReitDetailState.Success -> {
                    val data = state.data

                    LazyColumn(modifier = Modifier.fillMaxSize()) {

                        item {
                            ReitHeaderSection(
                                name = data.name,
                                symbol = data.symbol,
                                currentPrice = data.currentPrice,
                                priceChange = data.priceChange,
                                percentageChange = data.percentageChange,
                                isPositive = data.isPositive,
                                isWatchlisted = isWatchlisted,
                                onBackClick = { navController.popBackStack() },
                                onWatchlistClick = { viewModel.toggleWatchlist() },
                                onShareClick = {
                                    Toast.makeText(
                                        context,
                                        "Share coming soon",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }

                        item {
                            ReitTabsSection(state.data)
                        }
                    }
                }
            }
        }
    }
}

// --- SECTIONS ---

@Composable
fun ReitHeaderSection(
    name: String,
    symbol: String,
    currentPrice: Double,
    priceChange: Double,
    percentageChange: Double,
    isPositive: Boolean,
    isWatchlisted: Boolean,
    onBackClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDark)
            .padding(top = 40.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CardDark)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = name,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Share",
                    tint = TextPrimary,
                    modifier = Modifier.clickable { onShareClick() }
                )
                Icon(
                    imageVector = if (isWatchlisted) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Watchlist",
                    tint = if (isWatchlisted) BuyTeal else TextPrimary,
                    modifier = Modifier.clickable { onWatchlistClick() }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = symbol, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "₹${currentPrice}",
            color = TextPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        val color = if (isPositive) BuyTeal else SellOrange
        val sign = if (isPositive) "+" else "-"
        Text(
            text = "$sign$priceChange ($percentageChange%) 1D",
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ReitTabsSection(data: com.example.mahayuga.feature.navyuga.presentation.detail.ReitDetailData) {
    val tabs = listOf("Estate", "Finance", "News")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = BgDark,
            contentColor = BuyTeal,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = BuyTeal
                )
            },
            divider = { HorizontalDivider(color = CardDark) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = {
                        Text(
                            text = title,
                            color = if (pagerState.currentPage == index) BuyTeal else TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 600.dp)
        ) { page ->
            when (page) {
                0 -> EstateTabContent(data)
                1 -> FinanceTabContent(data)
                2 -> NewsTabContent(data)
            }
        }
    }
}

@Composable
fun EstateTabContent(data: com.example.mahayuga.feature.navyuga.presentation.detail.ReitDetailData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(data.images) { imageUrl ->
                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Property Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "About the Estate",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(data.description, color = TextSecondary, fontSize = 14.sp, lineHeight = 20.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardDark, RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EstateDetailRow("Property Type", data.propertyType)
            HorizontalDivider(color = BgDark)
            EstateDetailRow("Total Area", data.totalArea)
            HorizontalDivider(color = BgDark)
            EstateDetailRow("Occupancy Rate", data.occupancyRate)
            HorizontalDivider(color = BgDark)
            EstateDetailRow("Major Tenants", data.majorTenants.joinToString(", "))
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun EstateDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextSecondary, fontSize = 14.sp)
        Text(value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FinanceTabContent(data: com.example.mahayuga.feature.navyuga.presentation.detail.ReitDetailData) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("1D", "1W", "1M", "6M", "1Y", "5Y", "All").forEach { tf ->
                val isSelected = tf == "1D"
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) CardDark else Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        tf,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (data.chartPoints.isNotEmpty()) {
            val chartColor = if (data.isPositive) BuyTeal else SellOrange
            val yChartsPoints = data.chartPoints.map { Point(it.first, it.second) }

            val xAxisData = AxisData.Builder()
                .axisStepSize(100.dp)
                .backgroundColor(BgDark)
                .steps(yChartsPoints.size - 1)
                .labelData { i -> i.toString() }
                .labelAndAxisLinePadding(15.dp)
                .axisLineColor(Color.Transparent)
                .axisLabelColor(Color.Transparent)
                .build()

            val yAxisData = AxisData.Builder()
                .steps(5)
                .backgroundColor(BgDark)
                .labelAndAxisLinePadding(20.dp)
                .labelData { i ->
                    val minY = yChartsPoints.minOf { it.y }
                    val maxY = yChartsPoints.maxOf { it.y }
                    val yScale = (maxY - minY) / 5
                    (minY + (i * yScale)).toInt().toString()
                }
                .axisLineColor(Color.Transparent)
                .axisLabelColor(Color.Transparent)
                .build()

            val lineChartData = LineChartData(
                linePlotData = LinePlotData(
                    lines = listOf(
                        Line(
                            dataPoints = yChartsPoints,
                            LineStyle(
                                color = chartColor,
                                lineType = LineType.Straight()
                            ),
                            IntersectionPoint(color = Color.Transparent, radius = 0.dp),
                            SelectionHighlightPoint(color = TextPrimary),
                            ShadowUnderLine(
                                alpha = 0.2f,
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        chartColor.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            ),
                            SelectionHighlightPopUp(
                                backgroundColor = CardDark,
                                labelColor = TextPrimary,
                                labelTypeface = android.graphics.Typeface.DEFAULT_BOLD
                            )
                        )
                    )
                ),
                backgroundColor = BgDark,
                xAxisData = xAxisData,
                yAxisData = yAxisData,
                gridLines = GridLines(color = Color.Transparent)
            )

            LineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                lineChartData = lineChartData
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Fundamentals",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FinanceStatItem("Day Low", data.dayLow, Modifier.weight(1f))
                FinanceStatItem("Day High", data.dayHigh, Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FinanceStatItem("52W Low", data.week52Low, Modifier.weight(1f))
                FinanceStatItem("52W High", data.week52High, Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FinanceStatItem("Volume", data.volume, Modifier.weight(1f))
                FinanceStatItem("Avg Volume", data.avgVolume, Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FinanceStatItem("Market Cap", data.marketCap, Modifier.weight(1f))
                FinanceStatItem("P/E Ratio", data.peRatio, Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FinanceStatItem("Div Yield", data.dividendYield, Modifier.weight(1f))
                FinanceStatItem("All-Time Adjusted ROI", "14.2%", Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun FinanceStatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NewsTabContent(data: com.example.mahayuga.feature.navyuga.presentation.detail.ReitDetailData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        data.newsItems.forEach { news ->
            /* Placeholder layout since news is Any right now */
        }
        Text(
            "No recent news found for this asset.",
            color = TextSecondary,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun ReitBottomActionBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDark)
            .border(1.dp, CardDark, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = SellOrange),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        ) {
            Text("SELL", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = BuyTeal),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        ) {
            Text("BUY", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        ) {
            Text("SIP", fontWeight = FontWeight.Bold, color = BgDark)
        }
    }
}