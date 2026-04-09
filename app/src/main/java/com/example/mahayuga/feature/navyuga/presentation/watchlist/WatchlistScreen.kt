// main/java/com/example/mahayuga/feature/navyuga/presentation/watchlist/WatchlistScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.watchlist

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mahayuga.core.common.GroupedHeaderIcons
import com.example.mahayuga.feature.navyuga.presentation.home.LiveAssetTradingCard
import com.example.mahayuga.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WatchlistScreen(
    onNavigateToSmReitDetail: (String) -> Unit,
    onNavigateToReitDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: WatchlistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf("All", "SM REITs", "REITs")

    // FIXED: Properly initialized pagerState linked to the size of the tabs
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        containerColor = BricxBackground,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(modifier = Modifier.background(BricxBackground)) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = "Watchlist",
                                tint = BricxBrandTeal,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Watchlist",
                                color = BricxTextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    actions = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            GroupedHeaderIcons(listOf(Icons.Outlined.Search to onNavigateToSearch))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BricxBackground,
                        scrolledContainerColor = BricxBackground
                    ),
                    scrollBehavior = scrollBehavior
                )

                HorizontalDivider(color = BricxBorder.copy(alpha = 0.5f))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BricxBackground)
        ) {
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
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
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

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BricxBrandTeal)
                }
            } else if (uiState.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.error ?: "Error Fetching Data", color = BricxDangerRed)
                }
            } else {
                // FIXED: Wrapped the lists in a HorizontalPager to actually handle the swipe gestures
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val smReitSymbols = listOf("PSTITANIA", "PSPLATINA")
                        val displayedAssets = when (page) {
                            1 -> uiState.watchlistedQuotes.filter { smReitSymbols.contains(it.symbol) }
                            2 -> uiState.watchlistedQuotes.filterNot { smReitSymbols.contains(it.symbol) }
                            else -> uiState.watchlistedQuotes
                        }

                        if (displayedAssets.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No assets in watchlist.", color = BricxTextSecondary)
                                }
                            }
                        } else {
                            items(displayedAssets, key = { it.symbol }) { quote ->
                                val isSmReit = smReitSymbols.contains(quote.symbol)
                                LiveAssetTradingCard(
                                    quote = quote,
                                    isSmReit = isSmReit,
                                    isSaved = true,
                                    onCardClick = {
                                        if (isSmReit) onNavigateToSmReitDetail(quote.symbol)
                                        else onNavigateToReitDetail(quote.symbol)
                                    },
                                    onSaveClick = {
                                        viewModel.removeWatchlist(quote.symbol)
                                        Toast.makeText(
                                            context,
                                            "Removed from Watchlist",
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
                            item { Spacer(Modifier.height(100.dp)) }
                        }
                    }
                }
            }
        }
    }
}