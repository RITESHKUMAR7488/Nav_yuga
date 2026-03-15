// main/java/com/example/mahayuga/feature/navyuga/presentation/trade/ReitDetailScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.trade

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mahayuga.feature.navyuga.domain.model.ReitModel
import com.example.mahayuga.feature.navyuga.domain.model.ReitNewsModel
import com.example.mahayuga.feature.navyuga.domain.model.ReitPropertyModel
import com.example.mahayuga.feature.navyuga.presentation.detail.ReitDetailState
import com.example.mahayuga.feature.navyuga.presentation.detail.ReitDetailViewModel

private val ReitBg = Color(0xFF040C17)
private val ReitCard = Color(0xFF0B1727)
private val ReitGreen = Color(0xFF00BFA5)
private val ReitTabActive = Color(0xFF00BFA5)
private val ReitTabInactive = Color(0xFF132337)
private val TextWhite = Color.White
private val TextGrey = Color(0xFF8B9BB4)
private val ChartLineColor = Color(0xFF00E676)
private val PieBlue = Color(0xFF2854A1)
private val PiePink = Color(0xFFFFD1D1)

@Composable
fun ReitDetailScreen(
    assetId: String,
    navController: NavController,
    viewModel: ReitDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Details", "Portfolio", "News")

    Scaffold(
        containerColor = ReitBg,
        topBar = {
            Column(modifier = Modifier
                .background(ReitBg)
                .statusBarsPadding()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(24.dp)
                    ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextWhite) }
                    Text(
                        text = when (uiState) {
                            is ReitDetailState.Success -> (uiState as ReitDetailState.Success).reit.name; is ReitDetailState.Loading -> "Loading..."; else -> "Error"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabs.forEachIndexed { index, title ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (selectedTab == index) ReitTabActive else ReitTabInactive)
                                .clickable { selectedTab = index }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                title,
                                color = if (selectedTab == index) TextWhite else TextWhite.copy(
                                    alpha = 0.7f
                                ),
                                fontSize = 16.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        if (index < tabs.size - 1) Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(ReitBg)
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            when (val state = uiState) {
                is ReitDetailState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = ReitGreen) }

                is ReitDetailState.Error -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text(state.message, color = Color.Red) }

                is ReitDetailState.Success -> {
                    when (selectedTab) {
                        0 -> ReitDetailsTab(state.reit); 1 -> ReitPortfolioTab(); 2 -> ReitNewsTab(
                        state.reit.news
                    )
                    }
                }
            }
        }
    }
}

@Composable
fun ReitDetailsTab(reit: ReitModel) {
    val cardWidth = (LocalConfiguration.current.screenWidthDp.dp - 48.dp) / 2

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Card(
            colors = CardDefaults.cardColors(containerColor = ReitCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0xFFE8EDF2)))
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)) {
                            Text(
                                reit.name,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                reit.assetManager,
                                color = TextGrey,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    "₹${
                                        String.format(
                                            java.util.Locale.US,
                                            "%.2f",
                                            reit.currentPrice
                                        )
                                    }",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "${if (reit.priceChangePercent >= 0) "+" else ""}${
                                        String.format(
                                            java.util.Locale.US,
                                            "%.2f",
                                            reit.priceChangePercent
                                        )
                                    }%",
                                    color = if (reit.priceChangePercent >= 0) ReitGreen else Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Open Price: ₹${
                                    String.format(
                                        java.util.Locale.US,
                                        "%.2f",
                                        reit.openPrice
                                    )
                                }", color = TextGrey, fontSize = 12.sp
                            )
                            Text(
                                "Last Price: ₹${
                                    String.format(
                                        java.util.Locale.US,
                                        "%.2f",
                                        reit.lastPrice
                                    )
                                }", color = TextGrey, fontSize = 12.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Box(
                                modifier = Modifier
                                    .border(
                                        1.dp,
                                        TextGrey.copy(alpha = 0.5f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) { Text("Registered under SEBI", color = TextGrey, fontSize = 8.sp) }
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(modifier = Modifier
                                .width(120.dp)
                                .height(60.dp)) {
                                SimpleLineChart(
                                    data = reit.priceHistory,
                                    lineColor = if (reit.priceChange >= 0) ChartLineColor else Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }

        // NO STARS HERE! This is real API data synced from the ViewModel
        Card(
            colors = CardDefaults.cardColors(containerColor = ReitCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem("Market Cap", reit.marketCap)
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(ReitGreen.copy(alpha = 0.6f))
                )
                StatItem("Dividend", reit.dividendYield)
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(ReitGreen.copy(alpha = 0.6f))
                )
                StatItem(
                    "52-Wk High",
                    "₹${String.format(java.util.Locale.US, "%.0f", reit.high52Week)}"
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(ReitGreen.copy(alpha = 0.6f))
                )
                StatItem(
                    "52-Wk Low",
                    "₹${String.format(java.util.Locale.US, "%.0f", reit.low52Week)}"
                )
            }
        }

        // STARS KEPT HERE. Yahoo Finance does not know the square footage of a building.
        Card(
            colors = CardDefaults.cardColors(containerColor = ReitCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Portfolio ⭐",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text("Total Portfolio Value", color = TextGrey, fontSize = 12.sp)
                    Text(
                        reit.totalPortfolioValue,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Total Holdings: ${reit.totalHoldingsMsf} MSF",
                        color = ReitGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Under Development: +${reit.underDevelopmentMsf} MSF",
                        color = ReitGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Occupancy: ${reit.occupancyPercent}%",
                        color = ReitGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(modifier = Modifier.size(100.dp)) { SimpleDonutChart(60f, PieBlue, PiePink) }
            }
        }

        if (reit.properties.isNotEmpty()) {
            Text(
                "All Properties ⭐",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(reit.properties) { prop ->
                    ReitPropertyCard(
                        prop,
                        cardWidth
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ReitPortfolioTab() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) { Text("Portfolio Details Coming Soon", color = TextGrey) }
}

@Composable
fun ReitNewsTab(newsList: List<ReitNewsModel>) {
    if (newsList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { Text("No news available currently.", color = TextGrey) }; return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(newsList) { news ->
            Card(
                colors = CardDefaults.cardColors(containerColor = ReitCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE8EDF2))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            news.title,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(news.contentPreview, color = TextWhite.copy(0.9f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        news.bulletPoints.forEach { point ->
                            Text(
                                "- $point",
                                color = TextGrey,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            news.date,
                            color = TextGrey,
                            fontSize = 10.sp,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextGrey, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun ReitPropertyCard(prop: ReitPropertyModel, cardWidth: androidx.compose.ui.unit.Dp) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ReitCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(cardWidth)
    ) {
        Column {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFFE8EDF2)))
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    prop.name,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    prop.location,
                    color = TextGrey,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    modifier = Modifier.height(32.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) { SimpleLineChart(prop.priceHistory, ChartLineColor) }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Open Price: ",
                        color = TextGrey,
                        fontSize = 10.sp
                    ); Text(
                    "₹${String.format(java.util.Locale.US, "%.2f", prop.openPrice)}",
                    color = ReitGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Last Price: ",
                        color = TextGrey,
                        fontSize = 10.sp
                    ); Text(
                    "₹${String.format(java.util.Locale.US, "%.2f", prop.lastPrice)}",
                    color = TextWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                }
            }
        }
    }
}

@Composable
fun SimpleLineChart(data: List<Float>, lineColor: Color) {
    if (data.isEmpty()) return
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width;
        val height = size.height;
        val maxVal = data.maxOrNull() ?: 1f;
        val minVal = data.minOrNull() ?: 0f;
        val range = if ((maxVal - minVal) == 0f) 1f else maxVal - minVal
        val path = Path().apply { moveTo(0f, height - ((data[0] - minVal) / range) * height) }
        for (i in 1 until data.size) path.lineTo(
            (i.toFloat() / (data.size - 1)) * width,
            height - ((data[i] - minVal) / range) * height
        )
        drawPath(path, lineColor, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun SimpleDonutChart(completedPercent: Float, color1: Color, color2: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val sweep1 = (completedPercent / 100f) * 360f;
        val sweep2 = 360f - sweep1
        drawArc(color1, -90f, sweep1, true); drawArc(
        color2,
        -90f + sweep1,
        sweep2,
        true
    ); drawCircle(ReitCard, size.minDimension / 3f)
    }
}