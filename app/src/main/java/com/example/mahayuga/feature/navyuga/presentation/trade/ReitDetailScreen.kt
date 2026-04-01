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
import java.util.Locale

private val TradeBg = Color(0xFF080F18)
private val TradeCardBg = Color(0xFF0F1722)
private val TextWhite = Color(0xFFFFFFFF)
private val TextGrey = Color(0xFF8B9BB4)
private val BorderDark = Color(0xFF1A2A40)
private val BuyTeal = Color(0xFF14B8A6)
private val SellRed = Color(0xFFE53935)

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

    var isNse by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Estate", "Finance", "News", "Media")

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
                                    Toast.makeText(
                                        context,
                                        "Sharing...",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
            if (selectedTab == 1) {
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

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // Header Details
                    item {
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
                            Text(
                                text = "Multiple Locations, India",
                                color = TextGrey,
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
                                        imageVector = Icons.Default.SwapVert,
                                        contentDescription = "Switch Market",
                                        tint = TextGrey,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Tabs
                    stickyHeader {
                        ScrollableTabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = TradeBg,
                            contentColor = TextWhite,
                            edgePadding = 16.dp,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = BuyTeal
                                )
                            },
                            divider = { HorizontalDivider(color = BorderDark) }
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = {
                                        Text(
                                            title,
                                            fontSize = 15.sp,
                                            color = if (selectedTab == index) TextWhite else TextGrey,
                                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // Tab Contents
                    when (selectedTab) {
                        0 -> { // ESTATE TAB
                            item {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    if (data.images.isNotEmpty()) {
                                        val pagerState =
                                            rememberPagerState(pageCount = { data.images.size })
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(220.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                        ) {
                                            HorizontalPager(
                                                state = pagerState,
                                                modifier = Modifier.fillMaxSize()
                                            ) { page ->
                                                AsyncImage(
                                                    model = data.images[page],
                                                    contentDescription = "Estate Photo",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                            Row(
                                                Modifier
                                                    .height(20.dp)
                                                    .fillMaxWidth()
                                                    .align(Alignment.BottomCenter)
                                                    .padding(bottom = 8.dp),
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                repeat(pagerState.pageCount) { iteration ->
                                                    val color =
                                                        if (pagerState.currentPage == iteration) BuyTeal else Color.LightGray
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(2.dp)
                                                            .clip(CircleShape)
                                                            .background(color)
                                                            .size(6.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(24.dp))
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(TradeCardBg, RoundedCornerShape(12.dp))
                                            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            "Portfolio Details",
                                            color = TextWhite,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            PortfolioMetric("Total Area", data.totalArea)
                                            PortfolioMetric("Occupancy", data.occupancyRate)
                                            PortfolioMetric("WALE", "6.8 yrs")
                                        }
                                    }

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

                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        "Properties Held",
                                        color = TextWhite,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }

                            val subProperties = listOf(
                                Triple("Embassy Manyata", "Bangalore", "14.8M sq ft"),
                                Triple("Embassy TechVillage", "Bangalore", "9.2M sq ft")
                            )

                            items(subProperties) { prop ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp),
                                    colors = CardDefaults.cardColors(containerColor = TradeCardBg),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, BorderDark)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.DarkGray)
                                        ) {
                                            AsyncImage(
                                                model = "https://images.unsplash.com/photo-1497366216548-37526070297c",
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = prop.first,
                                                color = TextWhite,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = prop.second,
                                                color = TextGrey,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
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
                                            .background(TradeCardBg, RoundedCornerShape(12.dp))
                                            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
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
                                                .padding(end = 40.dp, top = 20.dp, bottom = 20.dp)
                                        ) {
                                            if (data.chartPoints.isNotEmpty()) {
                                                val path = Path()
                                                val stepX = size.width / (data.chartPoints.size - 1)

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
                                                fontWeight = if (tf == "1D") FontWeight.Bold else FontWeight.Normal,
                                                modifier = Modifier.clickable { }
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
                                            .background(TradeCardBg, RoundedCornerShape(12.dp))
                                            .border(1.dp, BorderDark, RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Q3 FY26 Investor Presentation", color = TextGrey)
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