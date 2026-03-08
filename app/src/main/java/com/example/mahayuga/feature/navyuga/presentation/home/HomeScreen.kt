package com.example.mahayuga.feature.navyuga.presentation.home

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.auth.presentation.components.formatIndian

// --- Trading UI Theme Colors ---
private val TradeBg = Color(0xFF040C17)
private val TradeCardBg = Color(0xFF0B1624)
private val TradeGreen = Color(0xFF00BFA5)
private val TradeRed = Color(0xFFD32F2F)
private val TradeCyan = Color(0xFF00E5FF)
private val TextWhite = Color(0xFFFFFFFF)
private val TextGrey = Color(0xFF8B9BB4)
private val BorderDark = Color(0xFF1A2A40)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    onRoiClick: () -> Unit,
    onNavigateToSearch: () -> Unit,
    scrollToTopTrigger: Boolean,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val supportNumber by viewModel.supportNumber.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("SM REITS", "REITS")

    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        containerColor = TradeBg,
        topBar = {
            Column(modifier = Modifier.background(TradeBg)) {
                // 1. Top Bar (Home & Bell)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Home",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = TextWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 2. Market Ticker
                MarketTickerRow()

                // 3. Search & Filter Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Search Field
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clickable { onNavigateToSearch() },
                        shape = RoundedCornerShape(8.dp),
                        color = TradeBg,
                        border = borderStroke()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Search,
                                null,
                                tint = TextGrey,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Search by Asset Manager, Location, etc.",
                                color = TextGrey,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Filter Button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(borderStroke(), RoundedCornerShape(8.dp))
                            .clickable { /* Show Filters */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FilterList, "Filter", tint = TextGrey)
                    }
                }

                // 4. Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = TradeBg,
                    contentColor = TextWhite,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = TradeGreen
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
                                    text = title,
                                    fontSize = 16.sp,
                                    color = if (selectedTab == index) TextWhite else TextGrey,
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
                CircularProgressIndicator(color = TradeGreen)
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(TradeBg),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.properties.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No assets available right now.", color = TextGrey)
                        }
                    }
                } else {
                    // Filter based on Tab for realism
                    val filteredProperties = if (selectedTab == 0) {
                        uiState.properties.filter { it.type != "Office" } // Dummy SM REIT filter
                    } else {
                        uiState.properties.filter { it.type == "Office" } // Dummy REIT filter
                    }

                    items(filteredProperties, key = { it.id }) { property ->
                        PropertyTradingCard(
                            property = property,
                            isSmReit = selectedTab == 0,
                            onCardClick = { onNavigateToDetail(property.id) },
                            onBuyClick = {
                                // Direct to Whatsapp or detail
                                try {
                                    val message =
                                        "Hello, I want to BUY units of *${property.title}*."
                                    val url =
                                        "https://api.whatsapp.com/send?phone=$supportNumber&text=${
                                            Uri.encode(message)
                                        }"
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse(url); setPackage("com.whatsapp")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "WhatsApp not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onSellClick = {
                                Toast.makeText(
                                    context,
                                    "Sell orders available from Portfolio",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun MarketTickerRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TradeCardBg)
            .border(borderStroke())
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TickerItem("NIFTY", "100", TradeCyan)
        TickerDivider()
        TickerItem("SENSEX", "120", TradeRed)
        TickerDivider()
        TickerItem("TITANIA", "140", TradeGreen)
        TickerDivider()
        TickerItem("EMBASSY", "135", TradeCyan)
    }
}

@Composable
fun TickerItem(name: String, value: String, valueColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(name, color = TextGrey, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(" : ", color = TextGrey, fontSize = 12.sp)
        Text(value, color = valueColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TickerDivider() {
    Text(
        text = "  |  ",
        color = TextGrey.copy(alpha = 0.5f),
        fontSize = 12.sp
    )
}

@Composable
fun PropertyTradingCard(
    property: PropertyModel,
    isSmReit: Boolean,
    onCardClick: () -> Unit,
    onBuyClick: () -> Unit,
    onSellClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = TradeCardBg),
        shape = RoundedCornerShape(12.dp),
        border = borderStroke()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Name, NSE badge, Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = property.title,
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(BorderDark, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "NSE",
                                color = TextGrey,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = property.fullLocation,
                        color = TextGrey,
                        fontSize = 12.sp
                    )
                }
                Icon(
                    Icons.Default.BookmarkBorder,
                    contentDescription = "Save",
                    tint = TextGrey,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price & Change
            Text(
                text = "₹${formatIndian(property.totalValuation)}",
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Dummy positive change for UI accuracy based on screenshot
                Text(
                    "₹346.47",
                    color = TradeGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(" | ", color = TextGrey, fontSize = 12.sp)
                Text("+0.54%", color = TradeGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // White Chart Box Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("Chart Area", color = Color.LightGray, fontSize = 12.sp) // Optional label
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Asset Manager & SEBI Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val label =
                        if (isSmReit) property.assetManager.ifEmpty { "Asset Manager Name" } else "Total AUM: ₹4500 Cr"
                    Text(
                        text = label,
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (isSmReit) {
                        Text("Asset Manager", color = TextGrey, fontSize = 10.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .border(1.dp, BorderDark, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    val tag =
                        if (isSmReit) "Registered under SEBI SM REIT" else "Registered under SEBI REIT"
                    Text(tag, color = TextGrey, fontSize = 8.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BorderDark, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // 4x2 Stats Grid
            Row(modifier = Modifier.fillMaxWidth()) {
                StatGridCol(
                    label1 = "Brick Price",
                    val1 = "₹${formatIndian(property.minInvest)}",
                    valColor1 = TextWhite,
                    label2 = "Running Dividend",
                    val2 = "${property.roi}%",
                    valColor2 = TextWhite,
                    modifier = Modifier.weight(1f)
                )
                StatGridCol(
                    label1 = "Today's High", val1 = "₹450 Cr", valColor1 = TextWhite,
                    label2 = "Today's Low", val2 = "₹10 L", valColor2 = TradeGreen,
                    modifier = Modifier.weight(1f)
                )
                StatGridCol(
                    label1 = "Open Price", val1 = "₹11 L", valColor1 = TradeGreen,
                    label2 = "Previous Closed", val2 = "₹950,000", valColor2 = TradeGreen,
                    modifier = Modifier.weight(1f)
                )
                StatGridCol(
                    label1 = "52-Week High", val1 = "₹1 L", valColor1 = TextWhite,
                    label2 = "52-Week Low", val2 = "₹95,000", valColor2 = TextWhite,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onBuyClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TradeGreen),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("BUY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Button(
                    onClick = onSellClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TradeRed),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        "SELL",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatGridCol(
    label1: String, val1: String, valColor1: Color,
    label2: String, val2: String, valColor2: Color,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Row 1
        Text(label1, color = TextGrey, fontSize = 10.sp, textAlign = TextAlign.Center)
        Text(
            val1,
            color = valColor1,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2
        Text(label2, color = TextGrey, fontSize = 10.sp, textAlign = TextAlign.Center, maxLines = 1)
        Text(
            val2,
            color = valColor2,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterOptionRow(
    title: String,
    options: List<String>,
    selectedOptions: Set<String>,
    onOptionSelected: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = selectedOptions.contains(option)
                SuggestionChip(
                    onClick = { onOptionSelected(option) },
                    label = {
                        Text(
                            text = option,
                            color = if (isSelected) Color.White else Color.White.copy(0.7f)
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (isSelected) Color(0xFF00BFA5) else Color.White.copy(alpha = 0.05f)
                    ),
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color.White.copy(alpha = 0.2f)
                    )
                )
            }
        }
    }
}

@Composable
private fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)