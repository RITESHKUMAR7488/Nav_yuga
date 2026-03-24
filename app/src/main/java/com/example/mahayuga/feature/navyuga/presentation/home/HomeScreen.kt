package com.example.mahayuga.feature.navyuga.presentation.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mahayuga.feature.navyuga.domain.model.MarketQuote
import kotlinx.coroutines.isActive
import java.util.Locale

private val TradeBg = Color(0xFF080F18)
private val TradeCardBg = Color(0xFF0F1722)
private val TextWhite = Color(0xFFFFFFFF)
private val TextGrey = Color(0xFF8B9BB4)
private val BorderDark = Color(0xFF1A2A40)

private val BuyTeal = Color(0xFF14B8A6)
private val SellOrange = Color(0xFFF97316)

@Composable
fun GroupedHeaderIcons(icons: List<Pair<ImageVector, () -> Unit>>) {
    Row(
        modifier = Modifier
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(50))
            .background(TradeCardBg.copy(alpha = 0.85f), RoundedCornerShape(50))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icons.forEach { (icon, onClick) ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextWhite,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
            )
        }
    }
}

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

    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    var isTickerOpen by remember { mutableStateOf(false) }

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
                            tint = BuyTeal,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Home",
                            color = TextWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GroupedHeaderIcons(
                            listOf(Icons.Outlined.Search to { isSearchActive = !isSearchActive })
                        )
                        GroupedHeaderIcons(
                            listOf(
                                Icons.Outlined.Send to {
                                    Toast.makeText(
                                        context,
                                        "Messages coming soon",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                Icons.Outlined.Notifications to {
                                    Toast.makeText(
                                        context,
                                        "No new notifications",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        )
                    }
                }

                HorizontalDivider(color = BorderDark.copy(alpha = 0.5f))

                if (isSearchActive) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Search properties...", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BuyTeal,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = TradeBg,
                    contentColor = TextWhite,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = BuyTeal
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = BuyTeal,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(TradeBg),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val allAssets =
                        uiState.tickerQuotes.filterNot { it.symbol == "^NSEI" || it.symbol == "^BSESN" }

                    // FIXED SYMBOL ROUTING
                    val smReitsList = listOf("PSTITANIA", "PSPLATINA")

                    var filteredAssets = if (selectedTab == 0) {
                        allAssets.filter { smReitsList.contains(it.symbol) }
                    } else {
                        allAssets.filterNot { smReitsList.contains(it.symbol) }
                    }

                    if (searchQuery.isNotBlank()) {
                        filteredAssets = filteredAssets.filter {
                            it.name.contains(searchQuery, ignoreCase = true) || it.symbol.contains(
                                searchQuery,
                                ignoreCase = true
                            )
                        }
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
                            val isSaved = uiState.watchlistedSymbols.contains(quote.symbol)

                            LiveAssetTradingCard(
                                quote = quote,
                                isSmReit = selectedTab == 0,
                                isSaved = isSaved,
                                onCardClick = {
                                    if (selectedTab == 0) onNavigateToSmReitDetail(quote.symbol)
                                    else onNavigateToReitDetail(quote.symbol)
                                },
                                onSaveClick = {
                                    viewModel.toggleWatchlist(quote.symbol)
                                    val msg =
                                        if (isSaved) "Removed from Watchlist" else "Added to Watchlist"
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        item { Spacer(Modifier.height(100.dp)) }
                    }
                }
            }

            AnimatedVisibility(
                visible = isTickerOpen,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { isTickerOpen = false }
                )
            }

            // --- RIGHT-CENTER TICKER OVERLAY ---
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                        .background(TradeCardBg.copy(alpha = 0.9f))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                        )
                        .clickable { isTickerOpen = !isTickerOpen }
                        .padding(horizontal = 4.dp, vertical = 24.dp)
                ) {
                    Icon(
                        imageVector = if (isTickerOpen) Icons.Default.ChevronRight else Icons.Default.ChevronLeft,
                        contentDescription = "Toggle Ticker",
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                AnimatedVisibility(
                    visible = isTickerOpen,
                    enter = slideInHorizontally(initialOffsetX = { it }),
                    exit = slideOutHorizontally(targetOffsetX = { it })
                ) {
                    Box(
                        modifier = Modifier
                            .width(110.dp)
                            .fillMaxHeight()
                            .padding(bottom = 100.dp)
                            .background(
                                color = TradeCardBg.copy(alpha = 0.95f),
                                shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                            )
                    ) {
                        val tickerListState = rememberLazyListState()

                        LaunchedEffect(isTickerOpen) {
                            if (isTickerOpen) {
                                while (isActive) {
                                    tickerListState.animateScrollBy(
                                        value = 60f,
                                        animationSpec = tween(
                                            durationMillis = 1000,
                                            easing = LinearEasing
                                        )
                                    )
                                }
                            }
                        }

                        val tickerQuotes =
                            uiState.tickerQuotes.filterNot { it.symbol == "^NSEI" || it.symbol == "^BSESN" }

                        LazyColumn(
                            state = tickerListState,
                            modifier = Modifier.fillMaxSize(),
                            userScrollEnabled = false,
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            if (tickerQuotes.isNotEmpty()) {
                                items(10000) { index ->
                                    val quote = tickerQuotes[index % tickerQuotes.size]
                                    val displayName =
                                        quote.name.split(",")[0].take(10).uppercase(Locale.ROOT)

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            displayName,
                                            color = TextWhite,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            "₹${
                                                String.format(
                                                    Locale.US,
                                                    "%.2f",
                                                    quote.currentPrice
                                                )
                                            }", color = TextWhite, fontSize = 12.sp
                                        )
                                        Text(
                                            "${if (quote.isPositive) "+" else ""}${
                                                String.format(
                                                    Locale.US,
                                                    "%.2f",
                                                    quote.percentageChange
                                                )
                                            }%",
                                            color = if (quote.isPositive) BuyTeal else SellOrange,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.3f))
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LiveAssetTradingCard(
    quote: MarketQuote,
    isSmReit: Boolean,
    isSaved: Boolean,
    onCardClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val priceColor = if (quote.isPositive) BuyTeal else SellOrange

    // FIXED HARDCODED SUFFIXES FOR MAPPING
    val assetData = when (quote.symbol) {
        "PSTITANIA" -> Triple(
            "PropShare Titania",
            "Property Share",
            listOf("https://images.unsplash.com/photo-1497366216548-37526070297c")
        )

        "PSPLATINA" -> Triple(
            "PropShare Platina",
            "Property Share",
            listOf("https://images.unsplash.com/photo-1416331108676-a22ccb276e35")
        )

        "EMBASSY" -> Triple(
            "Embassy REIT",
            "Embassy Group",
            listOf("https://images.unsplash.com/photo-1572025442646-866d16c84a54")
        )

        "MINDSPACE" -> Triple(
            "Mindspace REIT",
            "Mindspace Group",
            listOf("https://images.unsplash.com/photo-1486406146926-c627a92ad1ab")
        )

        "NEXUS" -> Triple(
            "Nexus Select REIT",
            "Nexus Group",
            listOf("https://images.unsplash.com/photo-1554118811-1e0d58224f24")
        )

        "BIRET" -> Triple(
            "Brookfield India REIT",
            "Brookfield Group",
            listOf("https://images.unsplash.com/photo-1582037928769-181f2422677e")
        )

        else -> Triple(
            quote.name.split(",")[0],
            "Knowledge Group",
            listOf("https://images.unsplash.com/photo-1552566626-52f8b828add9")
        )
    }

    val (displayName, managerName, images) = assetData
    val pagerState = rememberPagerState(pageCount = { images.size })

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = TradeCardBg),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayName,
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isSmReit) "SM REIT" else "REIT",
                        color = TextGrey,
                        fontSize = 12.sp
                    )
                }
                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .size(24.dp)
                        .offset(x = 8.dp, y = (-4).dp)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save to Watchlist",
                        tint = if (isSaved) BuyTeal else TextGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                                String.format(Locale.US, "%.2f", Math.abs(quote.priceChange))
                            }",
                            color = priceColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(" | ", color = TextGrey, fontSize = 12.sp)
                        Text(
                            text = "${if (quote.isPositive) "+" else ""}${
                                String.format(Locale.US, "%.2f", quote.percentageChange)
                            }%",
                            color = priceColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    AsyncImage(
                        model = images.getOrElse(page) { images.first() },
                        contentDescription = "Property Image",
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

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = managerName,
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Asset Manager",
                        color = TextGrey,
                        fontSize = 12.sp
                    )
                }

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
                        text = "IN/REIT/XXXX",
                        color = TextWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}