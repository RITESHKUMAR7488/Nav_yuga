// main/java/com/example/mahayuga/feature/navyuga/presentation/search/SearchScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.search

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mahayuga.core.common.* // ⚡ IMPORTED COMMON COMPONENTS
import com.example.mahayuga.feature.navyuga.presentation.home.LiveAssetTradingCard
import com.example.mahayuga.ui.theme.* // ⚡ IMPORTED NEW THEME

@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSmReitDetail: (String) -> Unit,
    onNavigateToReitDetail: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val smReitSymbols = listOf("PSTITANIA", "PSPLATINA")

    Scaffold(
        containerColor = BricxBackground, // ⚡ UPDATED
        topBar = {
            // ⚡ REPLACED CUSTOM HEADER WITH BRICXTOPAPPBAR
            BricxTopAppBar(
                title = "Search",
                onNavigateBack = onNavigateBack,
                showTrailingIcons = false
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ⚡ REPLACED RAW OUTLINEDTEXTFIELD WITH BRICTEXTFIELD
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                BricxTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    label = "Search by Property, Asset Manager etc.",
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = BricxBrandTeal
                        )
                    }
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BricxBrandTeal) // ⚡ UPDATED
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (searchQuery.isBlank()) {
                        // 1. Asset Managers
                        item {
                            SearchCategorySection(
                                title = "Asset Managers",
                                items = listOf(
                                    Triple(
                                        "Embassy Group",
                                        "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab",
                                        "embassy"
                                    ),
                                    Triple(
                                        "Mindspace",
                                        "https://images.unsplash.com/photo-1554118811-1e0d58224f24",
                                        "mindspace"
                                    ),
                                    Triple(
                                        "Nexus Select",
                                        "https://images.unsplash.com/photo-1572025442646-866d16c84a54",
                                        "nexus"
                                    )
                                ),
                                isSquareLayout = true,
                                onItemClick = { /* Handle Asset Manager Click later */ }
                            )
                        }

                        // 2. REITs
                        item {
                            SearchCategorySection(
                                title = "REITs",
                                items = listOf(
                                    Triple(
                                        "Embassy REIT",
                                        "https://images.unsplash.com/photo-1582037928769-181f2422677e",
                                        "EMBASSY"
                                    ),
                                    Triple(
                                        "Mindspace REIT",
                                        "https://images.unsplash.com/photo-1416331108676-a22ccb276e35",
                                        "MINDSPACE"
                                    )
                                ),
                                isSquareLayout = false,
                                onItemClick = onNavigateToReitDetail
                            )
                        }

                        // 3. SM REITs
                        item {
                            SearchCategorySection(
                                title = "SM REITs",
                                items = listOf(
                                    Triple(
                                        "PropShare Titania",
                                        "https://images.unsplash.com/photo-1497366216548-37526070297c",
                                        "PSTITANIA"
                                    ),
                                    Triple(
                                        "PropShare Platina",
                                        "https://images.unsplash.com/photo-1552566626-52f8b828add9",
                                        "PSPLATINA"
                                    )
                                ),
                                isSquareLayout = false,
                                onItemClick = onNavigateToSmReitDetail
                            )
                        }
                    } else {
                        // SHOW LIVE API SEARCH RESULTS (Using same cards as Home Screen)
                        if (searchResults.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "No assets match your search.",
                                        color = BricxTextSecondary
                                    )
                                } // ⚡ UPDATED
                            }
                        } else {
                            items(searchResults, key = { it.symbol }) { quote ->
                                val isSmReit = smReitSymbols.contains(quote.symbol)

                                LiveAssetTradingCard(
                                    quote = quote,
                                    isSmReit = isSmReit,
                                    isSaved = false, // Connect to watchlist state later if needed
                                    onCardClick = {
                                        if (isSmReit) onNavigateToSmReitDetail(quote.symbol)
                                        else onNavigateToReitDetail(quote.symbol)
                                    },
                                    onSaveClick = {
                                        Toast.makeText(
                                            context,
                                            "Added to Watchlist",
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
    }
}

@Composable
fun SearchCategorySection(
    title: String,
    items: List<Triple<String, String, String>>, // Name, ImageUrl, Id
    isSquareLayout: Boolean,
    onItemClick: (String) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)) {
        Text(
            text = title,
            color = BricxTextPrimary, // ⚡ UPDATED
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                Column(
                    horizontalAlignment = if (isSquareLayout) Alignment.CenterHorizontally else Alignment.Start,
                    modifier = Modifier
                        .width(if (isSquareLayout) 100.dp else 220.dp)
                        .clickable { onItemClick(item.third) }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isSquareLayout) 100.dp else 140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BricxSurfaceCard) // ⚡ UPDATED
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = item.second,
                            contentDescription = item.first,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.first,
                        color = BricxTextPrimary, // ⚡ UPDATED
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}