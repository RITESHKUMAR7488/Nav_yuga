// main/java/com/example/mahayuga/feature/navyuga/presentation/trade/ReitDetailScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.trade

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mahayuga.feature.navyuga.presentation.detail.ReitDetailState
import com.example.mahayuga.feature.navyuga.presentation.detail.ReitDetailViewModel
import kotlinx.coroutines.launch
import java.util.Locale

private val TradeBg = Color(0xFF080F18)
private val TradeCardBg = Color(0xFF0F1722)
private val TextWhite = Color(0xFFFFFFFF)
private val TextGrey = Color(0xFF8B9BB4)
private val BorderDark = Color(0xFF1A2A40)
private val BuyTeal = Color(0xFF14B8A6)
private val SellRed = Color(0xFFE53935)
private val PosGreen = Color(0xFF00E676)

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
    val coroutineScope = rememberCoroutineScope()

    var isNse by remember { mutableStateOf(true) }
    val tabs = listOf("Estate", "Finance", "News", "Media")
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    LaunchedEffect(assetId) {
        viewModel.fetchAssetDetails(assetId)
    }

    Scaffold(
        containerColor = TradeBg,
        topBar = {
            TopAppBar(
                title = { Text("Overview", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (navController != null) navController.popBackStack() else onNavigateBack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .background(TradeCardBg, RoundedCornerShape(50))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextWhite,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    Toast.makeText(context, "Sharing...", Toast.LENGTH_SHORT).show()
                                }
                        )
                        Icon(
                            imageVector = if (isWatchlisted) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Watchlist",
                            tint = if (isWatchlisted) BuyTeal else TextWhite,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    viewModel.toggleWatchlist()
                                    Toast.makeText(
                                        context,
                                        if (isWatchlisted) "Removed" else "Saved",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TradeBg)
            )
        },
        floatingActionButton = {
            if (pagerState.currentPage == 1) {
                FloatingActionButton(
                    onClick = onRoiClick,
                    containerColor = TradeCardBg,
                    contentColor = BuyTeal,
                    shape = CircleShape,
                    modifier = Modifier.border(1.dp, BorderDark, CircleShape)
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = "Calculate ROI")
                }
            }
        },
        bottomBar = {
            Surface(
                color = TradeCardBg,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Start SIP", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(0.6f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                        border = BorderStroke(1.dp, BorderDark)
                    ) {
                        Text("SIP", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            Toast.makeText(context, "Sell Order", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SellRed)
                    ) {
                        Text("SELL", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Button(
                        onClick = {
                            Toast.makeText(context, "Buy Order", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BuyTeal)
                    ) {
                        Text("BUY", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
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
                    CircularProgressIndicator(color = BuyTeal)
                }
            }

            is ReitDetailState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message, color = SellRed)
                }
            }

            is ReitDetailState.Success -> {
                val data = state.data
                val priceColor = if (data.isPositive) BuyTeal else SellRed

                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)) {
                    // Fixed Header Details
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)) {
                        Text(
                            text = data.name,
                            color = TextWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Multiple Locations, India", color = TextGrey, fontSize = 14.sp)

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
                                    color = TextWhite,
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
                                    Text(" | ", color = TextGrey, fontSize = 12.sp)
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
                                        color = TextWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { isNse = !isNse }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isNse) "NSE" else "BSE",
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.SwapVert,
                                    contentDescription = "Switch Market",
                                    tint = TextGrey,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Fixed Tabs
                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = TradeBg,
                        contentColor = TextWhite,
                        edgePadding = 16.dp,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                color = BuyTeal
                            )
                        },
                        divider = { HorizontalDivider(color = BorderDark) }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = pagerState.currentPage == index,
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
                                        color = if (pagerState.currentPage == index) TextWhite else TextGrey,
                                        fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            )
                        }
                    }

                    // Swipeable Pager
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f)
                    ) { page ->
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            when (page) {
                                0 -> { // ESTATE TAB
                                    item {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            // Stats Row
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        "Market Cap",
                                                        color = TextGrey,
                                                        fontSize = 10.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        "₹33,850 Cr",
                                                        color = TextWhite,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Box(
                                                    Modifier
                                                        .width(1.dp)
                                                        .height(30.dp)
                                                        .background(BorderDark)
                                                )
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        "Dividend",
                                                        color = TextGrey,
                                                        fontSize = 10.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        "6.7%",
                                                        color = TextWhite,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Box(
                                                    Modifier
                                                        .width(1.dp)
                                                        .height(30.dp)
                                                        .background(BorderDark)
                                                )
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        "52-Week High",
                                                        color = TextGrey,
                                                        fontSize = 10.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        "₹435.20",
                                                        color = TextWhite,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Box(
                                                    Modifier
                                                        .width(1.dp)
                                                        .height(30.dp)
                                                        .background(BorderDark)
                                                )
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        "52-Week Low",
                                                        color = TextGrey,
                                                        fontSize = 10.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        "₹366.00",
                                                        color = TextWhite,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(16.dp))

                                            // Portfolio Summary Card
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = CardDefaults.cardColors(containerColor = TradeCardBg),
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, BorderDark)
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
                                                            color = TextWhite,
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            "Total Portfolio Value",
                                                            color = TextGrey,
                                                            fontSize = 12.sp
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            "₹48,800 Cr",
                                                            color = TextWhite,
                                                            fontSize = 18.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp))
                                                        Text(
                                                            "Total Holdings: 42.6 MSF",
                                                            color = BuyTeal,
                                                            fontSize = 12.sp
                                                        )
                                                        Text(
                                                            "Under Development: +8.1 MSF",
                                                            color = BuyTeal,
                                                            fontSize = 12.sp
                                                        )
                                                        Text(
                                                            "Occupancy: 86%",
                                                            color = BuyTeal,
                                                            fontSize = 12.sp
                                                        )
                                                    }
                                                    PortfolioDonutChart(modifier = Modifier.size(90.dp))
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(24.dp))
                                            Text(
                                                "All Properties",
                                                color = TextWhite,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                        }
                                    }

                                    // All Properties Horizontal List
                                    item {
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            contentPadding = PaddingValues(horizontal = 16.dp)
                                        ) {
                                            items(2) { index ->
                                                val title =
                                                    if (index == 0) "Manyata Business Park,\nBengaluru" else "TechVillage,\nBengaluru"
                                                // ⚡ FIXED: Using data.currentPrice.toString() instead of data.lastPrice
                                                PropertyMiniCard(
                                                    data.name,
                                                    title,
                                                    data.openPrice,
                                                    data.currentPrice.toString()
                                                )
                                            }
                                        }
                                    }

                                    item {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Spacer(modifier = Modifier.height(24.dp))
                                            Text(
                                                "About",
                                                color = TextWhite,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = data.description,
                                                color = TextGrey,
                                                fontSize = 14.sp,
                                                lineHeight = 20.sp
                                            )
                                        }
                                    }
                                }

                                1 -> { // FINANCE TAB
                                    item {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(200.dp)
                                                    .background(
                                                        TradeCardBg,
                                                        RoundedCornerShape(12.dp)
                                                    )
                                                    .border(
                                                        1.dp,
                                                        BorderDark,
                                                        RoundedCornerShape(12.dp)
                                                    )
                                                    .padding(12.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Fullscreen,
                                                    contentDescription = "Expand Graph",
                                                    tint = TextGrey,
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .size(24.dp)
                                                        .clickable {
                                                            Toast.makeText(
                                                                context,
                                                                "Full Graph",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                )

                                                Canvas(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(
                                                            end = 40.dp,
                                                            top = 20.dp,
                                                            bottom = 20.dp
                                                        )
                                                ) {
                                                    if (data.chartPoints.isNotEmpty()) {
                                                        val path = Path()
                                                        val stepX =
                                                            size.width / (data.chartPoints.size - 1)

                                                        data.chartPoints.forEachIndexed { index, point ->
                                                            val x = index * stepX
                                                            val normalizedY =
                                                                1f - ((point.second - 400f) / 100f).coerceIn(
                                                                    0f,
                                                                    1f
                                                                )
                                                            val y = normalizedY * size.height

                                                            if (index == 0) path.moveTo(
                                                                x,
                                                                y
                                                            ) else path.lineTo(x, y)
                                                        }
                                                        drawPath(
                                                            path,
                                                            color = priceColor,
                                                            style = Stroke(width = 4f)
                                                        )
                                                    }
                                                }

                                                Column(
                                                    modifier = Modifier
                                                        .align(Alignment.CenterEnd)
                                                        .fillMaxHeight()
                                                        .padding(top = 20.dp, bottom = 20.dp),
                                                    verticalArrangement = Arrangement.SpaceBetween,
                                                    horizontalAlignment = Alignment.End
                                                ) {
                                                    Text("₹500", color = TextGrey, fontSize = 10.sp)
                                                    Text("₹450", color = TextGrey, fontSize = 10.sp)
                                                    Text("₹400", color = TextGrey, fontSize = 10.sp)
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceEvenly
                                            ) {
                                                listOf("1D", "1W", "1M", "1Y", "5Y").forEach { tf ->
                                                    Text(
                                                        text = tf,
                                                        color = if (tf == "1D") BuyTeal else TextGrey,
                                                        fontWeight = if (tf == "1D") FontWeight.Bold else FontWeight.Normal
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(24.dp))
                                            ExpandableFinanceSection(
                                                title = "Performance",
                                                data = listOf(
                                                    "Today's Low" to "₹${data.dayLow}",
                                                    "Today's High" to "₹${data.dayHigh}",
                                                    "Open" to "₹${data.openPrice}",
                                                    "Prev. Close" to "₹${data.previousClose}",
                                                    "Volume" to data.volume,
                                                    "Avg. Traded Price" to "₹${data.averageTradedPrice}"
                                                )
                                            )
                                            ExpandableFinanceSection(
                                                title = "Market Fundamentals",
                                                data = listOf(
                                                    "Market Cap" to "₹36,102 Cr",
                                                    "P/E Ratio" to "24.5",
                                                    "P/B Ratio" to "1.2",
                                                    "Dividend Yield" to "6.5%"
                                                )
                                            )
                                            ExpandableFinanceSection(
                                                title = "Financials (Q3)",
                                                data = listOf(
                                                    "Revenue" to "₹850 Cr",
                                                    "Net Profit" to "₹210 Cr",
                                                    "EBITDA Margin" to "82%",
                                                    "Debt to Equity" to "0.35"
                                                )
                                            )
                                            ExpandableFinanceSection(
                                                title = "Shareholding Pattern",
                                                data = listOf(
                                                    "Promoters" to "15.2%",
                                                    "FIIs" to "32.4%",
                                                    "DIIs" to "45.1%",
                                                    "Public" to "7.3%"
                                                )
                                            )
                                        }
                                    }
                                }

                                2 -> { // NEWS TAB
                                    item {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                "Latest News",
                                                color = TextWhite,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            repeat(3) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(bottom = 16.dp)
                                                ) {
                                                    Text(
                                                        "BricX Insights • 2h ago",
                                                        color = BuyTeal,
                                                        fontSize = 12.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        "${data.name} declares quarterly dividend of ₹4.5 per unit.",
                                                        color = TextWhite,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                    HorizontalDivider(
                                                        color = BorderDark,
                                                        modifier = Modifier.padding(top = 12.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                3 -> { // MEDIA TAB
                                    item {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                "Media & Reports",
                                                color = TextWhite,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .background(
                                                        TradeCardBg,
                                                        RoundedCornerShape(12.dp)
                                                    )
                                                    .border(
                                                        1.dp,
                                                        BorderDark,
                                                        RoundedCornerShape(12.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "Q3 FY26 Investor Presentation",
                                                    color = TextGrey
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
}

// Extracted UI Components for ReitDetailScreen to perfectly match AmProfile
@Composable
fun PortfolioDonutChart(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawArc(
            color = Color(0xFFFFD1C1), // Light peach
            startAngle = 90f,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(width = 30.dp.toPx())
        )
        drawArc(
            color = Color(0xFF2979FF), // Brand Blue
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(width = 30.dp.toPx())
        )
    }
}

@Composable
fun SparklineGraph(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(0f, size.height * 0.9f)
            lineTo(size.width * 0.4f, size.height * 0.6f)
            lineTo(size.width, size.height * 0.1f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun PropertyMiniCard(reitName: String, title: String, openPrice: String, lastPrice: String) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(240.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TradeCardBg),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFF0F4F8))
            ) // Image Placeholder
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(reitName, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(title, color = TextGrey, fontSize = 10.sp, lineHeight = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
                SparklineGraph(modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp), color = PosGreen)
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Open Price:", color = TextGrey, fontSize = 10.sp)
                    Text("₹$openPrice", color = PosGreen, fontSize = 10.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Last Price:", color = TextGrey, fontSize = 10.sp)
                    Text("₹$lastPrice", color = TextWhite, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun ExpandableFinanceSection(title: String, data: List<Pair<String, String>>) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand",
                tint = TextGrey
            )
        }
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp, start = 4.dp, end = 4.dp)
            ) {
                data.chunked(2).forEach { rowItems ->
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)) {
                        rowItems.forEach { item ->
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.first, color = TextGrey, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    item.second,
                                    color = TextWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider(color = BorderDark, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun PortfolioMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(text = label, color = TextGrey, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}