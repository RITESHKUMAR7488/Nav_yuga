package com.example.mahayuga.feature.navyuga.presentation.home

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mahayuga.feature.navyuga.domain.model.MarketQuote
import java.util.Locale

private val TradeBg = Color(0xFF080F18)
private val TradeCardBg = Color(0xFF0F1722)
private val TradeGreen = Color(0xFF00BFA5)
private val TradeRed = Color(0xFFFF3B30)
private val TextWhite = Color(0xFFFFFFFF)
private val TextGrey = Color(0xFF8B9BB4)
private val BorderDark = Color(0xFF1A2A40)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSmReitDetail: (String) -> Unit,
    onNavigateToReitDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    scrollToTopTrigger: Boolean,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("SM REITS", "REITS")

    // Coroutine specifically for list scrolling to ensure the main UI thread isn't blocked
    // when calculating and animating the list layout back to index 0.
    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        containerColor = TradeBg,
        topBar = {
            Column(
                modifier = Modifier
                    .background(TradeBg)
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
                            imageVector = Icons.Outlined.Home,
                            contentDescription = "Home Icon",
                            tint = TradeGreen,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Home",
                            color = TextWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CircularHeaderIcon(
                            icon = Icons.Outlined.Search,
                            desc = "Search/Filter",
                            onClick = onNavigateToSearch
                        )
                        CircularHeaderIcon(
                            icon = Icons.Outlined.Send,
                            desc = "Messages",
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Messages coming soon",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                        CircularHeaderIcon(
                            icon = Icons.Outlined.Notifications,
                            desc = "Notifications",
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "No new notifications",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }

                if (uiState.tickerQuotes.isNotEmpty()) {
                    MarketTickerRow(quotes = uiState.tickerQuotes)
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .background(TradeBg)
                    )
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = TradeBg,
                    contentColor = TextWhite,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = TradeGreen
                        )
                    },
                    divider = { HorizontalDivider(color = BorderDark.copy(alpha = 0.5f)) }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    fontSize = 16.sp,
                                    color = if (selectedTab == index) TextWhite else TextGrey,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TradeGreen)
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(TradeBg),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val allAssets =
                    uiState.tickerQuotes.filterNot { it.symbol == "^NSEI" || it.symbol == "^BSESN" }
                val filteredAssets =
                    if (selectedTab == 0) allAssets.filter { it.symbol.endsWith(".BO") } else allAssets.filter {
                        it.symbol.endsWith(".NS")
                    }

                if (filteredAssets.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) { Text("No assets available right now.", color = TextGrey) }
                    }
                } else {
                    items(filteredAssets, key = { it.symbol }) { quote ->
                        LiveAssetTradingCard(
                            quote = quote,
                            isSmReit = selectedTab == 0,
                            onCardClick = {
                                if (selectedTab == 0) onNavigateToSmReitDetail(quote.symbol)
                                else onNavigateToReitDetail(quote.symbol)
                            },
                            onSaveClick = {
                                Toast.makeText(context, "Added to Watchlist", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                    }
                    item { Spacer(Modifier.height(100.dp)) }
                }
            }
        }
    }
}

@Composable
fun CircularHeaderIcon(icon: ImageVector, desc: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .shadow(elevation = 6.dp, shape = CircleShape, spotColor = Color.Black)
            .clip(CircleShape)
            .background(TradeCardBg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = desc,
            tint = TextWhite,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun MarketTickerRow(quotes: List<MarketQuote>) {
    val scrollState = rememberScrollState()

    LaunchedEffect(scrollState.maxValue, quotes) {
        if (scrollState.maxValue > 0 && quotes.isNotEmpty()) {
            while (true) {
                val remainingScroll = scrollState.maxValue - scrollState.value
                if (remainingScroll > 0) scrollState.animateScrollTo(
                    scrollState.maxValue,
                    tween(remainingScroll * 30, easing = LinearEasing)
                )
                else scrollState.scrollTo(0)
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TradeBg)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .horizontalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val mappedQuotes = quotes + quotes
        mappedQuotes.forEach { quote ->
            val displayName = when (quote.symbol) {
                "^NSEI" -> "NIFTY"; "^BSESN" -> "SENSEX"; "PSTITANIA.BO" -> "TITANIA"; "EMBASSY.NS" -> "EMBASSY"; else -> quote.name.uppercase(
                    Locale.ROOT
                ).take(8)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(displayName, color = TextGrey, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(" : ", color = TextGrey, fontSize = 12.sp)
                Text(
                    String.format(Locale.US, "%.2f", quote.currentPrice),
                    color = if (quote.isPositive) TradeGreen else TradeRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text("  |  ", color = TextGrey.copy(alpha = 0.5f), fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LiveAssetTradingCard(
    quote: MarketQuote,
    isSmReit: Boolean,
    onCardClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val priceColor = if (quote.isPositive) TradeGreen else TradeRed

    // Hardcoded mock values for the visual layout as requested
    val location = if (isSmReit) "Sector 62, Gurugram" else null
    val managerName = "Nikhil Kamath"
    val managerTitle = "Asset Manager"

    // Number of mock images to swipe through
    val pagerState = rememberPagerState(pageCount = { 3 })

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = TradeCardBg),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // 1. Top Row: Name, Location, Save Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = quote.name.split(",")[0],
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isSmReit && location != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = location,
                            color = TextGrey,
                            fontSize = 12.sp
                        )
                    }
                }
                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .size(24.dp)
                        .offset(x = 8.dp, y = (-4).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Save to Watchlist",
                        tint = TextGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Price Row & Market Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "₹${String.format(Locale.US, "%,.2f", quote.currentPrice)}",
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${if (quote.isPositive) "+" else ""}₹${
                                String.format(
                                    Locale.US,
                                    "%.2f",
                                    Math.abs(quote.priceChange)
                                )
                            }",
                            color = priceColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(" | ", color = TextGrey, fontSize = 12.sp)
                        Text(
                            text = "${if (quote.isPositive) "+" else ""}${
                                String.format(
                                    Locale.US,
                                    "%.2f",
                                    quote.percentageChange
                                )
                            }%",
                            color = priceColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // NSE / BSE Switch Toggle Badge
                Row(
                    modifier = Modifier
                        .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .clickable { /* Handle market toggle */ },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSmReit) "BSE" else "NSE",
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

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Swipeable Image/Video Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White) // The requested white area placeholder
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    // Placeholder for actual network image/video player
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Media Placeholder",
                                tint = Color.LightGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                "Media Swipe Area (Page ${page + 1})",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Pager Indicators
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
                            if (pagerState.currentPage == iteration) TradeGreen else Color.LightGray
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

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Footer: Asset Manager (SM REIT only) & SEBI Badge (Both)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                if (isSmReit) {
                    Column {
                        Text(
                            text = managerName,
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = managerTitle,
                            color = TextGrey,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f)) // Push SEBI badge to right if no manager
                }

                // SEBI Registration Box
                Column(
                    modifier = Modifier
                        .border(1.dp, BorderDark, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SEBI Registered",
                        color = TextGrey,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "IN/REIT/XXXX", // Fixed for now per instructions
                        color = TextWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}