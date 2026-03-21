package com.example.mahayuga.feature.navyuga.presentation.home

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
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

private val TradeBg = Color(0xFF040C17)
private val TradeCardBg = Color(0xFF0B1624)
private val TradeGreen = Color(0xFF00BFA5)
private val TradeRed = Color(0xFFD32F2F)
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
    val supportNumber by viewModel.supportNumber.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("SM REITS", "REITS")

    // The coroutine block here is used to animate the scroll to the top of the list
    // without freezing the main UI thread. `LaunchedEffect` creates a coroutine scope
    // tied to the lifecycle of this composable.
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
                    .statusBarsPadding() // ⚡ FIX: This prevents the header from bleeding into the top system status bar
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
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
                            desc = "Search",
                            onClick = onNavigateToSearch
                        )
                        CircularHeaderIcon(
                            icon = Icons.Outlined.Send,
                            desc = "Messages",
                            onClick = { /* Add DM action */ }
                        )
                        CircularHeaderIcon(
                            icon = Icons.Outlined.Notifications,
                            desc = "Notifications",
                            onClick = { /* Add Notif action */ }
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
                            .background(TradeCardBg)
                            .border(borderStroke())
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
                    divider = { HorizontalDivider(color = BorderDark) }
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
                                if (selectedTab == 0) onNavigateToSmReitDetail(quote.symbol) else onNavigateToReitDetail(
                                    quote.symbol
                                )
                            },
                            onBuyClick = {
                                try {
                                    val url =
                                        "https://api.whatsapp.com/send?phone=$supportNumber&text=${
                                            Uri.encode("Hello, I want to BUY units of *${quote.name}*.")
                                        }"
                                    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse(url); setPackage("com.whatsapp")
                                    })
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "WhatsApp not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onSellClick = {
                                Toast.makeText(
                                    context,
                                    "Sell orders available from Portfolio",
                                    Toast.LENGTH_SHORT
                                ).show()
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
            .size(46.dp)
            .shadow(elevation = 8.dp, shape = CircleShape, spotColor = Color.Black)
            .clip(CircleShape)
            .background(TradeCardBg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = desc,
            tint = TextWhite,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun MarketTickerRow(quotes: List<MarketQuote>) {
    val scrollState = rememberScrollState()

    // Coroutine usage here creates an infinite scroll animation loop
    // so the ticker constantly moves to the left without blocking UI rendering
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
            .background(TradeCardBg)
            .border(borderStroke())
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

@Composable
fun LiveAssetTradingCard(
    quote: MarketQuote,
    isSmReit: Boolean,
    onCardClick: () -> Unit,
    onBuyClick: () -> Unit,
    onSellClick: () -> Unit
) {
    val priceColor = if (quote.isPositive) TradeGreen else TradeRed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = TradeCardBg),
        shape = RoundedCornerShape(12.dp),
        border = borderStroke()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            quote.name.split(",")[0],
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(BorderDark, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                if (isSmReit) "BSE" else "NSE",
                                color = TextGrey,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        if (isSmReit) "Prop Share Capital ⭐" else "Public Market Asset ⭐",
                        color = TextGrey,
                        fontSize = 12.sp
                    )
                }
                Icon(
                    Icons.Default.BookmarkBorder,
                    "Save",
                    tint = TextGrey,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "₹${String.format(Locale.US, "%.2f", quote.currentPrice)}",
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "₹${String.format(Locale.US, "%.2f", Math.abs(quote.priceChange))}",
                    color = priceColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(" | ", color = TextGrey, fontSize = 12.sp)
                Text(
                    "${if (quote.isPositive) "+" else ""}${
                        String.format(
                            Locale.US,
                            "%.2f",
                            quote.percentageChange
                        )
                    }%", color = priceColor, fontSize = 14.sp, fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BorderDark, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                StatGridCol(
                    label1 = "Min Invest ⭐",
                    val1 = "₹${quote.currentPrice.toInt()}",
                    valColor1 = TextWhite,
                    label2 = "Dividend Yield",
                    val2 = "${String.format(Locale.US, "%.1f", quote.dividendYield)}%",
                    valColor2 = TextWhite,
                    modifier = Modifier.weight(1f)
                )
                StatGridCol(
                    label1 = "Day High",
                    val1 = "₹${quote.dayHigh.toInt()}",
                    valColor1 = TextWhite,
                    label2 = "Day Low",
                    val2 = "₹${quote.dayLow.toInt()}",
                    valColor2 = priceColor,
                    modifier = Modifier.weight(1f)
                )
                StatGridCol(
                    label1 = "Open Price",
                    val1 = "₹${String.format(Locale.US, "%.1f", quote.openPrice)}",
                    valColor1 = TextWhite,
                    label2 = "Prev Close",
                    val2 = "₹${String.format(Locale.US, "%.1f", quote.previousClose)}",
                    valColor2 = TextWhite,
                    modifier = Modifier.weight(1f)
                )
                StatGridCol(
                    label1 = "52-Wk High",
                    val1 = "₹${quote.fiftyTwoWeekHigh.toInt()}",
                    valColor1 = TextWhite,
                    label2 = "52-Wk Low",
                    val2 = "₹${quote.fiftyTwoWeekLow.toInt()}",
                    valColor2 = TextWhite,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onBuyClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TradeGreen),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("BUY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Button(
                    onClick = onSellClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TradeRed),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        "SELL",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatGridCol(
    label1: String,
    val1: String,
    valColor1: Color,
    label2: String,
    val2: String,
    valColor2: Color,
    modifier: Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label1, color = TextGrey, fontSize = 10.sp, textAlign = TextAlign.Center)
        Text(
            val1,
            color = valColor1,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(label2, color = TextGrey, fontSize = 10.sp, textAlign = TextAlign.Center, maxLines = 1)
        Text(
            val2,
            color = valColor2,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)