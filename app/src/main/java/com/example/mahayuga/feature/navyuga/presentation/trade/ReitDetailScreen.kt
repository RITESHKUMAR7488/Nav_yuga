// main/java/com/example/mahayuga/feature/navyuga/presentation/trade/ReitDetailScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.trade

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mahayuga.feature.navyuga.presentation.detail.ReitDetailState
import com.example.mahayuga.feature.navyuga.presentation.detail.ReitDetailViewModel
import java.util.Locale

private val TradeBg = Color(0xFF080F18)
private val TradeCardBg = Color(0xFF0F1722)
private val TextWhite = Color(0xFFFFFFFF)
private val TextGrey = Color(0xFF8B9BB4)
private val BorderDark = Color(0xFF1A2A40)
private val BuyTeal = Color(0xFF14B8A6)
private val SellOrange = Color(0xFFF97316)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReitDetailScreen(
    assetId: String,
    onNavigateBack: () -> Unit = {},
    navController: NavController? = null,
    viewModel: ReitDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val isWatchlisted by viewModel.isWatchlisted.collectAsState()

    LaunchedEffect(assetId) {
        viewModel.fetchAssetDetails(assetId)
    }

    Scaffold(
        containerColor = TradeBg,
        topBar = {
            TopAppBar(
                title = { Text("Overview", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (navController != null) navController.popBackStack() else onNavigateBack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                actions = {
                    // ⚡ Phase 3: Oval Background for Action Icons
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
                                    Toast.makeText(
                                        context,
                                        "Sharing...",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        )
                        Icon(
                            imageVector = if (isWatchlisted) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Watchlist",
                            tint = if (isWatchlisted) BuyTeal else TextWhite,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    viewModel.toggleWatchlist()
                                    Toast.makeText(
                                        context,
                                        if (isWatchlisted) "Removed" else "Saved",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TradeBg)
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is ReitDetailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BuyTeal)
                }
            }

            is ReitDetailState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message, color = SellOrange)
                }
            }

            is ReitDetailState.Success -> {
                val data = state.data
                val priceColor = if (data.isPositive) BuyTeal else SellOrange

                // Fallback location since it's not in the data class
                val locationText = if (data.name.contains(
                        "EMBASSY",
                        ignoreCase = true
                    )
                ) "Bangalore, Pune, Mumbai" else "Multiple Locations, India"

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // 1. Live API Header Details & Price Section (Graph Removed)
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = data.name,
                                color = TextWhite,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = locationText, color = TextGrey, fontSize = 14.sp)

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(
                                        text = "₹${
                                            String.format(
                                                Locale.US,
                                                "%.2f",
                                                data.currentPrice
                                            )
                                        }",
                                        color = TextWhite,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${if (data.isPositive) "+" else ""}₹${
                                                String.format(
                                                    Locale.US,
                                                    "%.2f",
                                                    Math.abs(data.priceChange)
                                                )
                                            }",
                                            color = priceColor,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(" | ", color = TextGrey, fontSize = 12.sp)
                                        Text(
                                            text = "${if (data.isPositive) "+" else ""}${
                                                String.format(
                                                    Locale.US,
                                                    "%.2f",
                                                    data.percentageChange
                                                )
                                            }%",
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
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "NSE",
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
                        }
                    }

                    // 2. Estate Photos Horizontal Scroll (Mapped to your ViewModel images)
                    if (data.images.isNotEmpty()) {
                        item {
                            Column {
                                Text(
                                    "Estate",
                                    color = TextWhite,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                val pagerState =
                                    rememberPagerState(pageCount = { data.images.size })
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    HorizontalPager(
                                        state = pagerState,
                                        modifier = Modifier.fillMaxSize()
                                    ) { page ->
                                        AsyncImage(
                                            model = data.images[page],
                                            contentDescription = "Estate Photo",
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
                            }
                        }
                    }

                    // 3. Portfolio Details
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(TradeCardBg, RoundedCornerShape(12.dp))
                                .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                "Portfolio Details",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                PortfolioMetric("Total Area", data.totalArea)
                                PortfolioMetric("Occupancy", data.occupancyRate)
                                PortfolioMetric("WALE", "6.8 yrs")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                PortfolioMetric("Market Cap", "₹36,102 Cr")
                                PortfolioMetric("Asset Value", "₹52,000 Cr")
                                PortfolioMetric("Yield", "6.5%")
                            }
                        }
                    }

                    // 4. About Section
                    item {
                        Column {
                            Text(
                                "About",
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("CEO: ", color = TextGrey, fontSize = 14.sp)
                                Text(
                                    "Aravind Maiya",
                                    color = TextWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = data.description,
                                color = TextGrey,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // 5. Properties Held by Fund
                    item {
                        Text(
                            "Properties Held",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Mock list for now as per Phase 3 requirements
                    val subProperties = listOf(
                        Triple("Embassy Manyata", "Bangalore, Karnataka", "14.8M sq ft"),
                        Triple("Embassy TechVillage", "Bangalore, Karnataka", "9.2M sq ft"),
                        Triple("Express Towers", "Nariman Point, Mumbai", "0.5M sq ft")
                    )

                    items(subProperties) { prop ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = TradeCardBg),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.DarkGray)
                                ) {
                                    AsyncImage(
                                        model = "https://images.unsplash.com/photo-1497366216548-37526070297c",
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = prop.first,
                                        color = TextWhite,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = prop.second, color = TextGrey, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Area: ${prop.third}",
                                            color = BuyTeal,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Div: 6.2%",
                                            color = TextWhite,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
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
fun PortfolioMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(text = label, color = TextGrey, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}