// main/java/com/example/mahayuga/feature/navyuga/presentation/home/HomeScreen.kt
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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mahayuga.core.common.BricxHubTopAppBar
import com.example.mahayuga.feature.navyuga.domain.model.MarketQuote
import com.example.mahayuga.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavigateToSmReitDetail: (String) -> Unit,
    onNavigateToReitDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
    scrollToTopTrigger: Boolean,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val tabs = listOf("SM REITS", "REITS")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    var isTickerOpen by remember { mutableStateOf(false) }

    // FIXED: Removed the conflicting ScrollBehavior that was swallowing scroll events
    // because BricxHubTopAppBar is a static component and cannot collapse.
    Scaffold(
        containerColor = BricxBackground,
        topBar = {
            Column(modifier = Modifier.background(BricxBackground)) {
                BricxHubTopAppBar(
                    title = "Home",
                    icon = Icons.Outlined.Home,
                    onSearchClick = onNavigateToSearch,
                    onNotificationClick = onNavigateToNotifications,
                    onMessageClick = onNavigateToMessages
                )

                HorizontalDivider(color = BricxBorder.copy(alpha = 0.5f))

                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = BricxBackground,
                    contentColor = BricxTextPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = BricxBrandTeal
                        )
                    },
                    divider = { HorizontalDivider(color = BricxBorder.copy(alpha = 0.5f)) }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                            text = {
                                Text(
                                    title,
                                    fontSize = 16.sp,
                                    color = if (pagerState.currentPage == index) BricxTextPrimary else BricxTextSecondary,
                                    fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
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
                    color = BricxBrandTeal,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    val pullToRefreshState = rememberPullToRefreshState()
                    var isRefreshing by remember { mutableStateOf(false) }

                    // FIXED: Applied Modifier.fillMaxSize() to ensure bounds are strictly passed to LazyColumn
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = {
                            coroutineScope.launch {
                                isRefreshing = true
                                delay(1000)
                                isRefreshing = false
                            }
                        },
                        state = pullToRefreshState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val listState = rememberLazyListState()
                        LaunchedEffect(scrollToTopTrigger) {
                            if (scrollToTopTrigger) listState.animateScrollToItem(0)
                        }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(BricxBackground),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 100.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val allAssets =
                                uiState.tickerQuotes.filterNot { it.symbol == "^NSEI" || it.symbol == "^BSESN" }
                            val smReitsList = listOf("PSTITANIA", "PSPLATINA")
                            val filteredAssets =
                                if (page == 0) allAssets.filter { smReitsList.contains(it.symbol) }
                                else allAssets.filterNot { smReitsList.contains(it.symbol) }

                            if (filteredAssets.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(40.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "No assets available right now.",
                                            color = BricxTextSecondary
                                        )
                                    }
                                }
                            } else {
                                items(filteredAssets, key = { it.symbol }) { quote ->
                                    val isSaved = uiState.watchlistedSymbols.contains(quote.symbol)
                                    LiveAssetTradingCard(
                                        quote = quote, isSmReit = page == 0, isSaved = isSaved,
                                        onCardClick = {
                                            if (page == 0) onNavigateToSmReitDetail(quote.symbol)
                                            else onNavigateToReitDetail(quote.symbol)
                                        },
                                        onSaveClick = {
                                            viewModel.toggleWatchlist(quote.symbol)
                                            Toast.makeText(
                                                context,
                                                if (isSaved) "Removed from Watchlist" else "Added to Watchlist",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onShareClick = {
                                            Toast.makeText(
                                                context,
                                                "Sharing Property...",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // FIXED: Removed the overarching Box overlay that intercepted touch events.
            // Scrim and Ticker are now properly aligned directly onto the parent.
            AnimatedVisibility(visible = isTickerOpen, enter = fadeIn(), exit = fadeOut()) {
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

            if (!isTickerOpen) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Open Ticker",
                    tint = BricxTextPrimary.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(36.dp)
                        .padding(end = 4.dp)
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                if (dragAmount < -5) isTickerOpen = true
                            }
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { isTickerOpen = true }
                )
            }

            AnimatedVisibility(
                visible = isTickerOpen,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .fillMaxHeight()
                        .padding(bottom = 100.dp)
                        .background(
                            color = BricxSurfaceCard.copy(alpha = 0.98f),
                            shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                        )
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                if (dragAmount > 10) isTickerOpen = false
                            }
                        }
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Markets",
                                color = BricxTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "By BricX",
                                color = BricxBrandTeal,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        HorizontalDivider(color = BricxBorder.copy(alpha = 0.8f))

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
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            quote.name.split(",")[0].take(10)
                                                .uppercase(Locale.ROOT),
                                            color = BricxTextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        RealtimeFlickerText(
                                            text = "₹${
                                                String.format(
                                                    Locale.US,
                                                    "%.2f",
                                                    quote.currentPrice
                                                )
                                            }",
                                            currentValue = quote.currentPrice,
                                            defaultColor = BricxTextPrimary,
                                            textStyle = TextStyle(fontSize = 12.sp)
                                        )
                                        Text(
                                            "${if (quote.isPositive) "+" else ""}${
                                                String.format(
                                                    Locale.US,
                                                    "%.2f",
                                                    quote.percentageChange
                                                )
                                            }%",
                                            color = if (quote.isPositive) BricxBrandTeal else BricxWarningOrange,
                                            fontSize = 11.sp, fontWeight = FontWeight.Bold
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
    quote: MarketQuote, isSmReit: Boolean, isSaved: Boolean,
    onCardClick: () -> Unit, onSaveClick: () -> Unit, onShareClick: () -> Unit
) {
    val priceColor = if (quote.isPositive) BricxBrandTeal else BricxWarningOrange
    var isNse by remember { mutableStateOf(true) }

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
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BricxBorder)
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
                        color = BricxTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isSmReit) "Mumbai, Maharashtra" else "Multiple Cities",
                        color = BricxTextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier
                            .size(24.dp)
                            .offset(y = (-4).dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = BricxTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = onSaveClick,
                        modifier = Modifier
                            .size(24.dp)
                            .offset(y = (-4).dp)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (isSaved) BricxBrandTeal else BricxTextSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    RealtimeFlickerText(
                        text = "₹${String.format(Locale.US, "%,.2f", quote.currentPrice)}",
                        currentValue = quote.currentPrice, defaultColor = BricxTextPrimary,
                        textStyle = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${if (quote.isPositive) "+" else ""}₹${
                                String.format(
                                    Locale.US,
                                    "%.2f",
                                    Math.abs(quote.priceChange)
                                )
                            }", color = priceColor, fontSize = 14.sp, fontWeight = FontWeight.Medium
                        )
                        Text(" | ", color = BricxTextSecondary, fontSize = 12.sp)
                        Text(
                            text = "${if (quote.isPositive) "+" else ""}${
                                String.format(
                                    Locale.US,
                                    "%.2f",
                                    quote.percentageChange
                                )
                            }%", color = priceColor, fontSize = 14.sp, fontWeight = FontWeight.Bold
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
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Switch Market",
                        tint = BricxTextSecondary,
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
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    AsyncImage(
                        model = images.getOrElse(page) { images.first() },
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
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
                        color = BricxTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Asset Manager", color = BricxTextSecondary, fontSize = 12.sp)
                }
                Column(
                    modifier = Modifier
                        .border(1.dp, BricxBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SEBI Registered",
                        color = BricxTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "IN/REIT/XXXX",
                        color = BricxTextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}