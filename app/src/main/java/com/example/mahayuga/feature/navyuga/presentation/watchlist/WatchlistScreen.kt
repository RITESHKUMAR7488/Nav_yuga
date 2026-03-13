// main/java/com/example/mahayuga/feature/navyuga/presentation/watchlist/WatchlistScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.watchlist

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

// Theme Colors based on your screenshots
private val BackgroundDark = Color(0xFF040C17)
private val CardBackground = Color(0xFF162032)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFFA0AEC0)
private val PositiveGreen = Color(0xFF00E676)
private val NegativeRed = Color(0xFFFF1744)
private val SearchBarBg = Color(0xFF1E293B)
private val ImagePlaceholderBg = Color(0xFFE2E8F0)

@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(top = 48.dp, start = 16.dp, end = 16.dp) // Added top padding for status bar area
    ) {
        // Top Header
        Text(
            text = "Watchlist",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search and Filter Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                placeholder = {
                    Text("Search by Properties, Manager and Location", color = TextSecondary, fontSize = 12.sp)
                },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search", tint = TextSecondary)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SearchBarBg,
                    unfocusedContainerColor = SearchBarBg,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Filter Button
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SearchBarBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Filter",
                    tint = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // State Handling & List
        when (val state = uiState) {
            is WatchlistState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PositiveGreen)
                }
            }
            is WatchlistState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = NegativeRed)
                }
            }
            is WatchlistState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp) // padding for bottom nav
                ) {
                    items(state.items) { item ->
                        WatchlistCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun WatchlistCard(item: WatchlistItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
    ) {
        // Left Image Placeholder (Matches the light grey box in your wireframe)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(120.dp)
                .background(ImagePlaceholderBg)
        )

        // Right Content Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Name and NSE badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = item.name,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.subtitle,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "NSE ↕",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }

            // Middle: Sparkline Chart
            SparklineChart(
                data = item.sparklineData,
                isPositive = item.isPositive,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(30.dp)
                    .padding(vertical = 4.dp)
            )

            // Bottom Row: Price info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Price / Day",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "₹${item.currentPrice}",
                        color = if (item.isPositive) PositiveGreen else NegativeRed,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "₹${item.priceChange} | +${item.percentageChange}%",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// Custom Canvas to draw the mini line chart exactly like the wireframe
@Composable
fun SparklineChart(
    data: List<Float>,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    val lineColor = if (isPositive) PositiveGreen else NegativeRed

    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas

        val max = data.maxOrNull() ?: 1f
        val min = data.minOrNull() ?: 0f
        val range = if (max == min) 1f else max - min

        val width = size.width
        val height = size.height
        val stepX = width / (data.size - 1).coerceAtLeast(1)

        val path = Path()
        data.forEachIndexed { index, value ->
            val x = index * stepX
            // Y is inverted (0 is top in Canvas)
            val y = height - ((value - min) / range * height)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}