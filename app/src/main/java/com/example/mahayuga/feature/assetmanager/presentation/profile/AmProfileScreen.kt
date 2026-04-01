// main/java/com/example/mahayuga/feature/assetmanager/presentation/profile/AmProfileScreen.kt
package com.example.mahayuga.feature.assetmanager.presentation.profile

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mahayuga.core.common.* // ⚡ IMPORTED COMMON COMPONENTS
import com.example.mahayuga.ui.theme.* // ⚡ IMPORTED BRICX THEME

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToListings: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AmProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showMenuSheet by remember { mutableStateOf(false) }
    val tabs = listOf("Details", "Portfolio", "News", "Media")

    Scaffold(
        containerColor = BricxBackground, // ⚡ UPDATED
        topBar = {
            // ⚡ REPLACED RAW ROW WITH BRICXTOPAPPBAR
            BricxTopAppBar(
                title = "Manager Profile",
                onNavigateBack = onNavigateBack,
                showTrailingIcons = false
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            if (uiState is UiState.Loading) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = BricxBrandTeal) }
                return@Scaffold
            }

            val data = (uiState as UiState.Success).data

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(BricxSurfaceCard)
                        .border(2.dp, BricxBorder, CircleShape)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AddAPhoto,
                        contentDescription = "Upload Picture",
                        tint = BricxTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    data.brandName,
                    color = BricxTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Managed by: ${data.contactName}",
                    color = BricxTextSecondary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onNavigateToListings,
                    colors = ButtonDefaults.buttonColors(containerColor = BricxBrandTeal),
                    shape = RoundedCornerShape(8.dp), modifier = Modifier.height(40.dp)
                ) {
                    Icon(
                        Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View My Listings", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BricxBackground,
                contentColor = BricxTextPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(
                            tabPositions[selectedTab]
                        ), color = BricxBrandTeal
                    )
                },
                divider = { HorizontalDivider(color = BricxBorder) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index, onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontSize = 14.sp,
                                color = if (selectedTab == index) BricxTextPrimary else BricxTextSecondary
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                when (selectedTab) {
                    0 -> item { DetailsTabContent(data) }
                    2 -> item { NewsTabContent() }
                    else -> item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) { Text("Content updating soon", color = BricxTextSecondary) }
                    }
                }
            }
        }

        if (showMenuSheet) {
            ModalBottomSheet(
                onDismissRequest = { showMenuSheet = false },
                containerColor = BricxSurfaceCard
            ) {
                Column(modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()) {
                    Text(
                        "Settings",
                        color = BricxTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BricxDangerRed.copy(
                                alpha = 0.2f
                            ), contentColor = BricxDangerRed
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

@Composable
fun DetailsTabContent(data: AmProfileData) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Entity Information",
            color = BricxTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EntityDetailRow("Legal Name", data.legalEntityName)
                HorizontalDivider(color = BricxBorder)
                EntityDetailRow("Entity Type", data.entityType)
                HorizontalDivider(color = BricxBorder)
                EntityDetailRow("SEBI Registration", data.sebiRegNo)
                HorizontalDivider(color = BricxBorder)
                EntityDetailRow("AUM Range", data.aum)
                HorizontalDivider(color = BricxBorder)
                EntityDetailRow("Operating Cities", data.operatingCities)
                HorizontalDivider(color = BricxBorder)
                EntityDetailRow("Years in Operation", data.yearsInOperation)
                HorizontalDivider(color = BricxBorder)
                EntityDetailRow("Email", data.email)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
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
                            color = BricxTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Asset Manager", color = BricxTextSecondary, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier
                            .border(1.dp, BricxBorder, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(data.sebiRegNo, color = BricxTextSecondary, fontSize = 9.sp)
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
                                color = BricxTextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "+${data.priceChange}%",
                                color = BricxSuccessGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Open Price: ₹${data.openPrice}",
                            color = BricxTextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            "Last Price: ₹${data.lastPrice}",
                            color = BricxTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    // ⚡ REPLACED LOCAL COMPONENT WITH IMPORTED ONE
                    SparklineGraph(
                        data = listOf(10f, 15f, 12f, 20f, 18f, 25f, 30f),
                        color = BricxSuccessGreen,
                        modifier = Modifier
                            .width(120.dp)
                            .height(40.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BricxSurfaceCard, RoundedCornerShape(12.dp))
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

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
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
                        color = BricxTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Total Portfolio Value", color = BricxTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        data.aum,
                        color = BricxTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Total Holdings: ${data.totalHoldings}",
                        color = BricxSuccessGreen,
                        fontSize = 12.sp
                    )
                    Text(
                        "Under Development: ${data.underDev}",
                        color = BricxSuccessGreen,
                        fontSize = 12.sp
                    )
                    Text(
                        "Occupancy: ${data.occupancy}",
                        color = BricxSuccessGreen,
                        fontSize = 12.sp
                    )
                }
                // ⚡ REPLACED LOCAL COMPONENT WITH IMPORTED ONE
                PortfolioDonutChart(
                    values = listOf(70f, 30f),
                    colors = listOf(ChartBlue, ChartPeach),
                    modifier = Modifier.size(90.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "All Properties",
            color = BricxTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(2) { index ->
                val title =
                    if (index == 0) "Manyata Business Park,\nBengaluru" else "TechVillage,\nBengaluru"
                PropertyMiniCard(title, data.openPrice.toString(), data.lastPrice.toString())
            }
        }
    }
}

@Composable
fun NewsTabContent() {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NewsItemCard(
            "Embassy REIT Q4 FY2024 Results: Strong Earnings Driven by Robust Leasing Activity",
            listOf("Embassy REIT reported strong Q4 FY2024...", "Robust...", "Embassy Tech..."),
            "12 Apr, 2024"
        )
        NewsItemCard(
            "Embassy REIT Q4 FY2024 Results: Strong Earnings Driven by Robust Leasing Activity",
            listOf("Embassy REIT reported strong Q4 FY2024...", "Robust...", "Embassy Tech..."),
            "12 Apr, 2024"
        )
    }
}

@Composable
fun EntityDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = BricxTextSecondary, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(
            value,
            color = BricxTextPrimary,
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
        Text(label, color = BricxTextSecondary, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = BricxTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun VerticalBar() {
    Box(Modifier
        .width(1.dp)
        .height(30.dp)
        .background(BricxBorder))
}

@Composable
fun PropertyMiniCard(title: String, openPrice: String, lastPrice: String) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(240.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFF0F4F8)))
            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(12.dp)) {
                Text(
                    "Embassy",
                    color = BricxTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(title, color = BricxTextSecondary, fontSize = 10.sp, lineHeight = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
                SparklineGraph(
                    data = listOf(10f, 15f, 12f, 20f, 18f, 25f, 30f),
                    color = BricxSuccessGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Open Price:", color = BricxTextSecondary, fontSize = 10.sp)
                    Text("₹$openPrice", color = BricxSuccessGreen, fontSize = 10.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Last Price:", color = BricxTextSecondary, fontSize = 10.sp)
                    Text("₹$lastPrice", color = BricxTextPrimary, fontSize = 10.sp)
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
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier
                .weight(0.35f)
                .fillMaxHeight()
                .background(BricxSurfaceCardLight))
            Column(modifier = Modifier
                .weight(0.65f)
                .padding(12.dp)) {
                Text(
                    title,
                    color = BricxTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                bullets.forEach { bullet ->
                    Text(
                        "- $bullet",
                        color = BricxTextSecondary,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    date,
                    color = BricxTextSecondary,
                    fontSize = 9.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}