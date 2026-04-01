// main/java/com/example/mahayuga/feature/navyuga/presentation/watchlist/WatchlistScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.watchlist

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mahayuga.core.common.BricxHubTopAppBar
import com.example.mahayuga.feature.navyuga.presentation.home.LiveAssetTradingCard
import com.example.mahayuga.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    onNavigateToSmReitDetail: (String) -> Unit,
    onNavigateToReitDetail: (String) -> Unit,
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
    viewModel: WatchlistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "SM REITs", "REITs")
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = BricxBackground,
        topBar = {
            Column(
                modifier = Modifier
                    .background(BricxBackground)
                    .statusBarsPadding()
            ) {
                BricxHubTopAppBar(
                    title = "Watchlist",
                    icon = Icons.Filled.Bookmark,
                    onSearchClick = { isSearchActive = !isSearchActive },
                    onNotificationClick = onNavigateToNotifications,
                    onMessageClick = onNavigateToMessages
                )

                HorizontalDivider(color = BricxBorder.copy(alpha = 0.5f))

                if (isSearchActive) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Search Watchlist...", color = BricxTextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BricxBrandTeal,
                            unfocusedBorderColor = BricxBorder,
                            focusedTextColor = BricxTextPrimary,
                            unfocusedTextColor = BricxTextPrimary
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = BricxBackground,
                    contentColor = BricxTextPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = BricxBrandTeal
                        )
                    },
                    divider = { HorizontalDivider(color = BricxBorder.copy(alpha = 0.5f)) }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    fontSize = 16.sp,
                                    color = if (selectedTab == index) BricxTextPrimary else BricxTextSecondary,
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
                CircularProgressIndicator(color = BricxBrandTeal)
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = uiState.error ?: "Error Fetching Data", color = BricxDangerRed)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BricxBackground),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var filteredAssets = uiState.watchlistedQuotes

                if (searchQuery.isNotBlank()) {
                    filteredAssets = filteredAssets.filter {
                        it.name.contains(searchQuery, ignoreCase = true) || it.symbol.contains(
                            searchQuery,
                            ignoreCase = true
                        )
                    }
                }

                val smReitSymbols = listOf("PSTITANIA", "PSPLATINA")
                val displayedAssets = when (selectedTab) {
                    1 -> filteredAssets.filter { smReitSymbols.contains(it.symbol) }
                    2 -> filteredAssets.filterNot { smReitSymbols.contains(it.symbol) }
                    else -> filteredAssets
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
                            quote = quote, isSmReit = isSmReit, isSaved = true,
                            onCardClick = {
                                if (isSmReit) onNavigateToSmReitDetail(quote.symbol) else onNavigateToReitDetail(
                                    quote.symbol
                                )
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