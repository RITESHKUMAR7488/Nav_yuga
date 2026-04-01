// main/java/com/example/mahayuga/feature/navyuga/presentation/detail/PropertyDetailScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.detail

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Fullscreen
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
import com.example.mahayuga.core.common.* // IMPORTS
import com.example.mahayuga.ui.theme.* // IMPORTS
import kotlinx.coroutines.launch

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
    val coroutineScope = rememberCoroutineScope() // Coroutine scope

    var isSaved by remember { mutableStateOf(false) }
    var isNse by remember { mutableStateOf(true) }
    val tabs = listOf("Estate", "Finance", "News", "Media")
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Scaffold(
        containerColor = BricxBackground,
        topBar = {
            BricxTopAppBar(
                title = "Overview",
                onNavigateBack = onNavigateBack,
                showTrailingIcons = true,
                isWatchlisted = isSaved,
                onShareClick = { Toast.makeText(context, "Sharing...", Toast.LENGTH_SHORT).show() },
                onWatchlistClick = { isSaved = !isSaved }
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
            StickyTradeBottomBar(
                onSipClick = { Toast.makeText(context, "Start SIP", Toast.LENGTH_SHORT).show() },
                onSellClick = { Toast.makeText(context, "Sell Order", Toast.LENGTH_SHORT).show() },
                onBuyClick = { Toast.makeText(context, "Buy Order", Toast.LENGTH_SHORT).show() }
            )
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
                    CircularProgressIndicator(color = BricxBrandTeal)
                }
            }

            is PropertyDetailState.Success -> {
                val property = state.property
                val formattedPrice =
                    if (property.totalValuation.startsWith("₹")) property.totalValuation else "₹${property.totalValuation}"

                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)) {
                    // Fixed Header
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)) {
                        Text(
                            text = property.title,
                            color = BricxTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = property.location, color = BricxTextSecondary, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = formattedPrice,
                                    color = BricxTextPrimary,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "+₹1,50,000",
                                        color = BricxBrandTeal,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(" | ", color = BricxTextSecondary, fontSize = 12.sp)
                                    Text(
                                        text = "+${property.roi}%",
                                        color = BricxBrandTeal,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
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
                                Modifier.tabIndicatorOffset(
                                    tabPositions[pagerState.currentPage]
                                ), color = BricxBrandTeal
                            )
                        },
                        divider = { HorizontalDivider(color = BricxBorder) }
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
                                            Text(
                                                "Property Details",
                                                color = BricxTextPrimary,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))

                                            // USING OUR NEW GRID COMPONENT
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
                                            Text(
                                                "Lease Details",
                                                color = BricxTextPrimary,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))

                                            // USING OUR NEW GRID COMPONENT
                                            DetailGrid(
                                                listOf(
                                                    "Tenants" to property.tenantName.ifEmpty { "Multiple" },
                                                    "Occupancy" to "${property.fundedPercent}%",
                                                    "Annual Rent" to "₹${property.grossAnnualRent}",
                                                    "Property Taxes" to "₹${property.annualPropertyTax}"
                                                )
                                            )
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
                                                    val path = Path()
                                                    val points = listOf(420f, 415f, 425f, 430f, 428f, 435f, 440f, 438f, 445f, 450f)
                                                    val stepX = size.width / (points.size - 1)

                                                    points.forEachIndexed { index, yValue ->
                                                        val x = index * stepX
                                                        val normalizedY = 1f - ((yValue - 400f) / 100f).coerceIn(0f, 1f)
                                                        val y = normalizedY * size.height
                                                        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                                    }
                                                    drawPath(path, color = BricxBrandTeal, style = Stroke(
                                                        width = 4f
                                                    )
                                                    )
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
                                                data = listOf("Today's Low" to "₹995.00", "Today's High" to "₹1,015.00", "Open" to "₹1,000.00", "Prev. Close" to "₹998.50", "Volume" to "450K", "Avg. Traded Price" to "₹1,005.20")
                                            )
                                            ExpandableFinanceSection(
                                                title = "Market Fundamentals",
                                                data = listOf("Market Cap" to "₹1,200 Cr", "P/E Ratio" to "18.5", "P/B Ratio" to "1.1", "Dividend Yield" to "${property.roi}%")
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

            else -> {}
        }
    }
}