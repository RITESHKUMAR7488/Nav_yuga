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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalConfiguration
import com.example.mahayuga.feature.navyuga.domain.model.ReitModel
import com.example.mahayuga.feature.navyuga.domain.model.ReitNewsModel
import com.example.mahayuga.feature.navyuga.domain.model.ReitPropertyModel

// --- SPECIFIC UI COLORS FROM MOCKUPS ---
private val ReitBg = Color(0xFF04101F)
private val ReitCard = Color(0xFF0B1727)
private val ReitGreen = Color(0xFF00BFA5)
private val ReitTabActive = Color(0xFF00BFA5)
private val ReitTabInactive = Color(0xFF132337)
private val TextWhite = Color.White
private val TextGrey = Color(0xFF8B9BB4)
private val ChartLineColor = Color(0xFF00E676)
private val ChartFillStart = Color(0xFF00E676).copy(alpha = 0.2f)
private val ChartFillEnd = Color.Transparent
private val PieBlue = Color(0xFF2854A1)
private val PiePink = Color(0xFFFFD1D1)

@Composable
fun ReitDetailScreen(
    assetId: String,
    navController: NavController
) {
    // Dummy Data mapped exactly to your screenshots
    val dummyReit = remember {
        ReitModel(
            id = "embassy_reit",
            name = "Embassy REIT",
            currentPrice = 410.50,
            priceChange = 2.54,
            priceChangePercent = 0.62,
            openPrice = 408.55,
            lastPrice = 407.95,
            marketCap = "₹33,850 Cr",
            dividendYield = "6.7%",
            high52Week = 435.20,
            low52Week = 366.00,
            totalPortfolioValue = "₹48,800 Cr",
            totalHoldingsMsf = 42.6,
            underDevelopmentMsf = 8.1,
            occupancyPercent = 86,
            priceHistory = listOf(390f, 395f, 392f, 400f, 405f, 402f, 410.50f),
            properties = listOf(
                ReitPropertyModel("1", "Embassy", "Manyata Business Park,\nBengaluru", 408.55, 407.95, listOf(10f, 15f, 25f, 40f)),
                ReitPropertyModel("2", "Embassy", "TechVillage,\nBengaluru", 408.55, 407.95, listOf(10f, 20f, 30f, 40f))
            ),
            news = listOf(
                ReitNewsModel("1", "Embassy REIT Q4 FY2024 Results:", "Strong Earnings Driven by Robust Leasing Activity", listOf("Embassy REIT reported strong Q4 FY2024...", "Robust...", "Embassy Tech..."), "12 Apr, 2024"),
                ReitNewsModel("2", "Embassy REIT Q4 FY2024 Results:", "Strong Earnings Driven by Robust Leasing Activity", listOf("Embassy REIT reported strong Q4 FY2024...", "Robust...", "Embassy Tech..."), "12 Apr, 2024")
            )
        )
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Details", "Portfolio", "News")

    Scaffold(
        containerColor = ReitBg,
        topBar = {
            Column(
                modifier = Modifier
                    .background(ReitBg)
                    .statusBarsPadding() // ⚡ ADD THIS LINE: Pushes the content below the phone's status bar / notch
            ) {
                // Top App Bar Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextWhite)
                    }
                    Text(
                        text = dummyReit.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(24.dp)) // Balance for centering
                }

                // Custom Tab Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp), // Full width bleed
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (isSelected) ReitTabActive else ReitTabInactive)
                                .clickable { selectedTab = index }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = if (isSelected) TextWhite else TextWhite.copy(alpha = 0.7f),
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        if (index < tabs.size - 1) {
                            Box(modifier = Modifier.width(1.dp).height(40.dp).background(ReitBg))
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                0 -> ReitDetailsTab(dummyReit)
                1 -> ReitPortfolioTab()
                2 -> ReitNewsTab(dummyReit.news)
            }
        }
    }
}
// ==========================================
// ⚡ TAB 1: DETAILS (The heavy charts & stats)
// ==========================================
@Composable
fun ReitDetailsTab(reit: ReitModel) {
    // ⚡ Calculate exactly half the screen width minus paddings (16dp edge * 2 + 16dp middle gap = 48dp total)
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val cardWidth = (screenWidth - 48.dp) / 2

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Main Price Header & Chart ---
        Card(
            colors = CardDefaults.cardColors(containerColor = ReitCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Color(0xFFE8EDF2))
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(reit.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(reit.assetManager, color = TextGrey, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("₹${String.format("%.2f", reit.currentPrice)}", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("+${reit.priceChangePercent}%", color = ReitGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 3.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Open Price: ₹${String.format("%.2f", reit.openPrice)}", color = TextGrey, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Last Price: ₹${String.format("%.2f", reit.lastPrice)}", color = TextGrey, fontSize = 12.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Box(
                                modifier = Modifier
                                    .border(1.dp, TextGrey.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Registered under SEBI REIT", color = TextGrey, fontSize = 8.sp)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(modifier = Modifier.width(120.dp).height(60.dp)) {
                                SimpleLineChart(data = reit.priceHistory, lineColor = ChartLineColor)
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Market Stats Row (4 blocks) ---
        Card(
            colors = CardDefaults.cardColors(containerColor = ReitCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically // ⚡ Aligns dividers and text perfectly
            ) {
                StatItem("Market Cap", reit.marketCap)
                // ⚡ FIX: Green Straight Line Separator
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(ReitGreen.copy(alpha = 0.6f)))
                StatItem("Dividend", reit.dividendYield)
                // ⚡ FIX: Green Straight Line Separator
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(ReitGreen.copy(alpha = 0.6f)))
                StatItem("52-Week High", "₹${String.format("%.2f", reit.high52Week)}")
                // ⚡ FIX: Green Straight Line Separator
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(ReitGreen.copy(alpha = 0.6f)))
                StatItem("52-Week Low", "₹${String.format("%.2f", reit.low52Week)}")
            }
        }

        // --- 3. Portfolio Overview (with Pie Chart) ---
        Card(
            colors = CardDefaults.cardColors(containerColor = ReitCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Portfolio", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Total Portfolio Value", color = TextGrey, fontSize = 12.sp)
                    Text(reit.totalPortfolioValue, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Total Holdings: ${reit.totalHoldingsMsf} MSF", color = ReitGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text("Under Development: +${reit.underDevelopmentMsf} MSF", color = ReitGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text("Occupancy: ${reit.occupancyPercent}%", color = ReitGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Box(modifier = Modifier.size(100.dp)) {
                    SimpleDonutChart(completedPercent = 60f, color1 = PieBlue, color2 = PiePink)
                }
            }
        }

        // --- 4. All Properties (Horizontal Scroll) ---
        Text("All Properties", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(reit.properties) { prop ->
                // ⚡ FIX: Passing the exact calculated width so 2 cards fit perfectly
                ReitPropertyCard(prop, cardWidth)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ==========================================
// ⚡ TAB 2 & 3: PORTFOLIO & NEWS
// ==========================================
@Composable
fun ReitPortfolioTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Portfolio Details Coming Soon", color = TextGrey)
    }
}

@Composable
fun ReitNewsTab(newsList: List<ReitNewsModel>) {
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
                    // Placeholder Image
                    Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFE8EDF2)))

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(news.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(news.contentPreview, color = TextWhite.copy(0.9f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        news.bulletPoints.forEach { point ->
                            Text("- $point", color = TextGrey, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(news.date, color = TextGrey, fontSize = 10.sp, modifier = Modifier.align(Alignment.End))
                    }
                }
            }
        }
    }
}

// ==========================================
// ⚡ REUSABLE UI COMPONENTS
// ==========================================

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextGrey, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

// Make sure to add this import at the top of the file if it's not there:
// import androidx.compose.ui.unit.Dp

@Composable
fun ReitPropertyCard(prop: ReitPropertyModel, cardWidth: androidx.compose.ui.unit.Dp) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ReitCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(cardWidth) // ⚡ Uses dynamic width to fit exactly 2 on screen
    ) {
        Column {
            // Top Image Placeholder
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color(0xFFE8EDF2)))

            Column(modifier = Modifier.padding(12.dp)) {
                Text(prop.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(prop.location, color = TextGrey, fontSize = 12.sp, lineHeight = 14.sp, modifier = Modifier.height(32.dp))

                Spacer(modifier = Modifier.height(16.dp))

                // Mini Graph inside card
                Box(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                    SimpleLineChart(data = prop.priceHistory, lineColor = ChartLineColor)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Open Price: ", color = TextGrey, fontSize = 10.sp)
                    Text("₹${String.format("%.2f", prop.openPrice)}", color = ReitGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Last Price: ", color = TextGrey, fontSize = 10.sp)
                    Text("₹${String.format("%.2f", prop.lastPrice)}", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- CUSTOM CANVAS CHARTS ---

@Composable
fun SimpleLineChart(data: List<Float>, lineColor: Color) {
    if (data.isEmpty()) return
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val maxVal = data.maxOrNull() ?: 1f
        val minVal = data.minOrNull() ?: 0f
        val range = if ((maxVal - minVal) == 0f) 1f else maxVal - minVal

        val path = Path()
        val startY = height - ((data[0] - minVal) / range) * height
        path.moveTo(0f, startY)

        for (i in 1 until data.size) {
            val x = (i.toFloat() / (data.size - 1)) * width
            val y = height - ((data[i] - minVal) / range) * height
            path.lineTo(x, y)
        }
        drawPath(path = path, color = lineColor, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun SimpleDonutChart(completedPercent: Float, color1: Color, color2: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val sweep1 = (completedPercent / 100f) * 360f
        val sweep2 = 360f - sweep1

        drawArc(
            color = color1,
            startAngle = -90f,
            sweepAngle = sweep1,
            useCenter = true
        )
        drawArc(
            color = color2,
            startAngle = -90f + sweep1,
            sweepAngle = sweep2,
            useCenter = true
        )
        // Inner circle to make it a donut
        drawCircle(
            color = ReitCard,
            radius = size.minDimension / 3f
        )
    }
}