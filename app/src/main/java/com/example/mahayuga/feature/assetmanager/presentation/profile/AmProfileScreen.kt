// main/java/com/example/mahayuga/feature/assetmanager/presentation/profile/AmProfileScreen.kt
package com.example.mahayuga.feature.assetmanager.presentation.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mahayuga.core.common.UiState

// --- THEME COLORS ---
private val AmBg = Color(0xFF061123)
private val AmCard = Color(0xFF111c30)
private val AmTeal = Color(0xFF38a882)
private val TextWhite = Color.White
private val TextGrey = Color(0xFF8B9BB4)
private val PosGreen = Color(0xFF00E676)
private val BorderDark = Color(0xFF1A2A40)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToListings: () -> Unit, // ⚡ ADDED NAV PARAMETER
    onLogout: () -> Unit,             // ⚡ ADDED LOGOUT PARAMETER
    viewModel: AmProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showMenuSheet by remember { mutableStateOf(false) }
    val tabs = listOf("Details", "Portfolio", "News", "Media")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmBg)
    ) {
        // --- 1. HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Manager Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextWhite
                )
            }
            IconButton(onClick = { showMenuSheet = true }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextWhite)
            }
        }

        if (uiState is UiState.Loading) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = AmTeal) }
            return
        }

        val data = (uiState as UiState.Success).data

        // --- 2. ASSET MANAGER PICTURE, NAME & BUTTONS ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AmCard)
                    .border(2.dp, BorderDark, CircleShape)
                    .clickable { /* Trigger Image Upload */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AddAPhoto,
                    contentDescription = "Upload Picture",
                    tint = TextGrey
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Brand Name
            Text(data.brandName, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            // Contact Manager Name (Sub-header)
            Text("Managed by: ${data.contactName}", color = TextGrey, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // ⚡ MY LISTINGS BUTTON ⚡
            Button(
                onClick = onNavigateToListings,
                colors = ButtonDefaults.buttonColors(containerColor = AmTeal),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("View My Listings", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 3. TABS ---
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = AmBg,
            contentColor = TextWhite,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = AmTeal
                )
            },
            divider = { HorizontalDivider(color = BorderDark) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            title,
                            fontSize = 14.sp,
                            color = if (selectedTab == index) TextWhite else TextGrey,
                        )
                    }
                )
            }
        }

        // --- 4. TAB CONTENT ---
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            when (selectedTab) {
                0 -> item { DetailsTabContent(data) }
                2 -> item { NewsTabContent() }
                else -> {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Content updating soon", color = TextGrey)
                        }
                    }
                }
            }
        }

        // --- MENU BOTTOM SHEET ---
        if (showMenuSheet) {
            ModalBottomSheet(
                onDismissRequest = { showMenuSheet = false },
                containerColor = AmCard
            ) {
                Column(modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()) {
                    Text(
                        "Settings",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF3B30).copy(
                                alpha = 0.2f
                            ), contentColor = Color(0xFFFF3B30)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Logout Account", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// ==========================================
// DETAILS TAB CONTENT
// ==========================================
@Composable
fun DetailsTabContent(data: AmProfileData) {
    Column(modifier = Modifier.padding(16.dp)) {

        // ⚡ NEW: ENTITY SUMMARY CARD
        Text(
            "Entity Information",
            color = TextWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AmCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EntityDetailRow("Legal Name", data.legalEntityName)
                HorizontalDivider(color = BorderDark)
                EntityDetailRow("Entity Type", data.entityType)
                HorizontalDivider(color = BorderDark)
                EntityDetailRow("SEBI Registration", data.sebiRegNo)
                HorizontalDivider(color = BorderDark)
                EntityDetailRow("AUM Range", data.aum)
                HorizontalDivider(color = BorderDark)
                EntityDetailRow("Operating Cities", data.operatingCities)
                HorizontalDivider(color = BorderDark)
                EntityDetailRow("Years in Operation", data.yearsInOperation)
                HorizontalDivider(color = BorderDark)
                EntityDetailRow("Email", data.email)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 1. Stock Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AmCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            data.brandName,
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Asset Manager", color = TextGrey, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier
                            .border(1.dp, BorderDark, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(data.sebiRegNo, color = TextGrey, fontSize = 9.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "₹${data.currentPrice}",
                                color = TextWhite,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "+${data.priceChange}%",
                                color = PosGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Open Price: ₹${data.openPrice}", color = TextGrey, fontSize = 12.sp)
                        Text("Last Price: ₹${data.lastPrice}", color = TextGrey, fontSize = 12.sp)
                    }
                    SparklineGraph(
                        modifier = Modifier
                            .width(120.dp)
                            .height(40.dp),
                        color = PosGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Stats Grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AmCard, RoundedCornerShape(12.dp))
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCol("Market Cap", data.marketCap)
            VerticalBar()
            StatCol("Dividend", data.dividendYield)
            VerticalBar()
            StatCol("52-Week High", data.high52)
            VerticalBar()
            StatCol("52-Week Low", data.low52)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Portfolio Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AmCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Portfolio",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Total Portfolio Value", color = TextGrey, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        data.aum,
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Total Holdings: ${data.totalHoldings}",
                        color = PosGreen,
                        fontSize = 12.sp
                    )
                    Text("Under Development: ${data.underDev}", color = PosGreen, fontSize = 12.sp)
                    Text("Occupancy: ${data.occupancy}", color = PosGreen, fontSize = 12.sp)
                }
                PortfolioDonutChart(modifier = Modifier.size(90.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("All Properties", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        // 4. All Properties Cards
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(2) { index ->
                val title =
                    if (index == 0) "Manyata Business Park,\nBengaluru" else "TechVillage,\nBengaluru"
                PropertyMiniCard(title, data.openPrice.toString(), data.lastPrice.toString())
            }
        }
    }
}

// ==========================================
// NEWS TAB CONTENT
// ==========================================
@Composable
fun NewsTabContent() {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NewsItemCard(
            title = "Embassy REIT Q4 FY2024 Results: Strong Earnings Driven by Robust Leasing Activity",
            bullets = listOf(
                "Embassy REIT reported strong Q4 FY2024...",
                "Robust...",
                "Embassy Tech..."
            ),
            date = "12 Apr, 2024"
        )
        NewsItemCard(
            title = "Embassy REIT Q4 FY2024 Results: Strong Earnings Driven by Robust Leasing Activity",
            bullets = listOf(
                "Embassy REIT reported strong Q4 FY2024...",
                "Robust...",
                "Embassy Tech..."
            ),
            date = "12 Apr, 2024"
        )
    }
}

// ==========================================
// HELPER COMPONENTS
// ==========================================
@Composable
fun EntityDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextGrey, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(
            value,
            color = TextWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.5f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

@Composable
fun StatCol(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextGrey, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun VerticalBar() {
    Box(Modifier
        .width(1.dp)
        .height(30.dp)
        .background(BorderDark))
}

@Composable
fun SparklineGraph(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(0f, size.height * 0.9f)
            lineTo(size.width * 0.4f, size.height * 0.6f)
            lineTo(size.width, size.height * 0.1f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun PortfolioDonutChart(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawArc(
            color = Color(0xFFFFD1C1),
            startAngle = 90f,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(width = 30.dp.toPx())
        )
        drawArc(
            color = Color(0xFF2979FF),
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(width = 30.dp.toPx())
        )
    }
}

@Composable
fun PropertyMiniCard(title: String, openPrice: String, lastPrice: String) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(240.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AmCard)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFF0F4F8))
            ) // Image Placeholder
            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(12.dp)) {
                Text("Embassy", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(title, color = TextGrey, fontSize = 10.sp, lineHeight = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
                SparklineGraph(modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp), color = PosGreen)
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Open Price:", color = TextGrey, fontSize = 10.sp)
                    Text("₹$openPrice", color = PosGreen, fontSize = 10.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Last Price:", color = TextGrey, fontSize = 10.sp)
                    Text("₹$lastPrice", color = TextWhite, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun NewsItemCard(title: String, bullets: List<String>, date: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = AmCard),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight()
                    .background(Color(0xFFF0F4F8))
            ) // Image Placeholder
            Column(modifier = Modifier
                .weight(0.65f)
                .padding(12.dp)) {
                Text(
                    title,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                bullets.forEach { bullet ->
                    Text(
                        "- $bullet",
                        color = TextGrey,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    date,
                    color = TextGrey,
                    fontSize = 9.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}