// main/java/com/example/mahayuga/feature/navyuga/presentation/detail/PropertyDetailScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.detail

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
import androidx.compose.material.icons.outlined.Warning
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
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

private val TradeBg = Color(0xFF080F18)
private val TradeCardBg = Color(0xFF0F1722)
private val TextWhite = Color(0xFFFFFFFF)
private val TextGrey = Color(0xFF8B9BB4)
private val BorderDark = Color(0xFF1A2A40)
private val BuyTeal = Color(0xFF14B8A6)
private val SellRed = Color(0xFFE53935)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PropertyDetailScreen(
    propertyId: String,
    onNavigateBack: () -> Unit,
    onRoiClick: () -> Unit = {},
    viewModel: PropertyDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var isSaved by remember { mutableStateOf(false) }
    var isNse by remember { mutableStateOf(true) }
    val tabs = listOf("Estate", "Finance", "News", "Media")
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Scaffold(
        containerColor = TradeBg,
        topBar = {
            TopAppBar(
                title = { Text("Overview", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Watchlist",
                            tint = if (isSaved) BuyTeal else TextWhite,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    isSaved = !isSaved
                                    Toast.makeText(
                                        context,
                                        if (isSaved) "Saved" else "Removed",
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
            is PropertyDetailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BuyTeal)
                }
            }

            is PropertyDetailState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.Warning,
                            contentDescription = "Error",
                            tint = SellRed,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = state.message, color = TextWhite)
                    }
                }
            }

            is PropertyDetailState.Success -> {
                val property = state.property
                val priceColor = BuyTeal
                val formattedPrice =
                    if (property.totalValuation.startsWith("₹")) property.totalValuation else "₹${property.totalValuation}"

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Fixed Header
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)) {
                        Text(
                            text = property.title,
                            color = TextWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = property.location, color = TextGrey, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = formattedPrice,
                                    color = TextWhite,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "+₹1,50,000",
                                        color = priceColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(" | ", color = TextGrey, fontSize = 12.sp)
                                    Text(
                                        text = "+${property.roi}%",
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
                                            if (property.imageUrls.isNotEmpty()) {
                                                val innerPagerState =
                                                    rememberPagerState(pageCount = { property.imageUrls.size })
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(250.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                ) {
                                                    HorizontalPager(
                                                        state = innerPagerState,
                                                        modifier = Modifier.fillMaxSize()
                                                    ) { imgPage ->
                                                        AsyncImage(
                                                            model = property.imageUrls[imgPage],
                                                            contentDescription = "Property Photo",
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
                                                        repeat(innerPagerState.pageCount) { iteration ->
                                                            val color =
                                                                if (innerPagerState.currentPage == iteration) BuyTeal else Color.LightGray
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

                                            SectionHeader("Property Details")
                                            DetailGrid(
                                                listOf(
                                                    "SM REIT Type" to "Commercial Grade-A",
                                                    "Location" to property.city,
                                                    "Area" to "${property.area} sq ft",
                                                    "Floor" to property.floor,
                                                    "Age" to "4 Years",
                                                    "Parking" to property.carPark,
                                                    "Scheme Document" to "View PDF"
                                                )
                                            )

                                            Spacer(modifier = Modifier.height(24.dp))
                                            SectionHeader("Asset Manager Details")
                                            DetailGrid(
                                                listOf(
                                                    "Name" to property.assetManager.ifEmpty { "Navyuga Assets" },
                                                    "CEO" to "Kunal Singh",
                                                    "Sponsor" to "BricX Group"
                                                )
                                            )

                                            Spacer(modifier = Modifier.height(24.dp))
                                            SectionHeader("Lease Details")
                                            DetailGrid(
                                                listOf(
                                                    "Tenants" to property.tenantName.ifEmpty { "Multiple" },
                                                    "Occupancy" to "${property.fundedPercent}%",
                                                    "Escalations" to property.escalation,
                                                    "Annual Rent" to "₹${property.grossAnnualRent}",
                                                    "Property Taxes" to "₹${property.annualPropertyTax}",
                                                    "Maintenance" to "₹2,50,000",
                                                    "Management Fee" to "1.5%"
                                                )
                                            )

                                            Spacer(modifier = Modifier.height(24.dp))
                                            SectionHeader("Financial Breakdown")
                                            Text(
                                                "Net Dividend Yield: ${property.roi}%",
                                                color = BuyTeal,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                "Calculated based on current stock price.",
                                                color = TextGrey,
                                                fontSize = 12.sp
                                            )

                                            Spacer(modifier = Modifier.height(24.dp))
                                            SectionHeader("Description")
                                            Text(
                                                text = property.description,
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

                                                // Mock Graph for SM REIT Finance
                                                Canvas(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(
                                                            end = 40.dp,
                                                            top = 20.dp,
                                                            bottom = 20.dp
                                                        )
                                                ) {
                                                    val path = Path()
                                                    val points = listOf(
                                                        420f,
                                                        415f,
                                                        425f,
                                                        430f,
                                                        428f,
                                                        435f,
                                                        440f,
                                                        438f,
                                                        445f,
                                                        450f
                                                    )
                                                    val stepX = size.width / (points.size - 1)

                                                    points.forEachIndexed { index, yValue ->
                                                        val x = index * stepX
                                                        val normalizedY =
                                                            1f - ((yValue - 400f) / 100f).coerceIn(
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
                                                    "Today's Low" to "₹995.00",
                                                    "Today's High" to "₹1,015.00",
                                                    "Open" to "₹1,000.00",
                                                    "Prev. Close" to "₹998.50",
                                                    "Volume" to "450K",
                                                    "Avg. Traded Price" to "₹1,005.20"
                                                )
                                            )
                                            ExpandableFinanceSection(
                                                title = "Market Fundamentals",
                                                data = listOf(
                                                    "Market Cap" to "₹1,200 Cr",
                                                    "P/E Ratio" to "18.5",
                                                    "P/B Ratio" to "1.1",
                                                    "Dividend Yield" to "${property.roi}%"
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
                                                "Property Updates",
                                                color = TextWhite,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            repeat(2) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(bottom = 16.dp)
                                                ) {
                                                    Text(
                                                        "Asset Manager Update • 1d ago",
                                                        color = BuyTeal,
                                                        fontSize = 12.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        "New lease agreement signed for 15,000 sq ft on the 12th Floor.",
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
                                                "Media gallery",
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
                                                Text("Virtual Tour Video", color = TextGrey)
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

@Composable
fun SectionHeader(title: String) {
    Text(title, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
fun DetailGrid(items: List<Pair<String, String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TradeCardBg, RoundedCornerShape(12.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items.forEach { (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = value,
                    color = TextGrey,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
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