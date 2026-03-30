// main/java/com/example/mahayuga/feature/navyuga/presentation/watchlist/WatchlistScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.watchlist

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
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
import com.example.mahayuga.feature.navyuga.presentation.home.GroupedHeaderIcons
import com.example.mahayuga.feature.navyuga.presentation.home.LiveAssetTradingCard

private val BorderDark = Color(0xFF1A2A40)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    onNavigateToSmReitDetail: (String) -> Unit,
    onNavigateToReitDetail: (String) -> Unit,
    viewModel: WatchlistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "SM REITs", "REITs")
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color(0xFF080F18),
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color(0xFF080F18))
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
                            imageVector = Icons.Filled.Bookmark,
                            contentDescription = "Watchlist Icon",
                            tint = Color(0xFF00BFA5),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Watchlist",
                            color = Color.White,
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
                                // ⚡ FIX: Reordered to match Home Screen (Notifications -> Messages)
                                Icons.Outlined.Notifications to {
                                    Toast.makeText(
                                        context,
                                        "No new notifications",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                Icons.AutoMirrored.Outlined.Send to {
                                    Toast.makeText(
                                        context,
                                        "Messages coming soon",
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
                        placeholder = { Text("Search Watchlist...", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00BFA5),
                            unfocusedBorderColor = Color(0xFF1A2A40),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF080F18),
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF00BFA5)
                        )
                    },
                    divider = { HorizontalDivider(color = Color(0xFF1A2A40).copy(alpha = 0.5f)) }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    fontSize = 16.sp,
                                    color = if (selectedTab == index) Color.White else Color(
                                        0xFF8B9BB4
                                    ),
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
                CircularProgressIndicator(color = Color(0xFF00BFA5))
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = uiState.error ?: "Error Fetching Data", color = Color.Red)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFF080F18)),
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

                // FIXED LIST FILTERING
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
                        ) { Text("No assets in watchlist.", color = Color(0xFF8B9BB4)) }
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
                            // ⚡ FIX: Added the missing parameter here
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