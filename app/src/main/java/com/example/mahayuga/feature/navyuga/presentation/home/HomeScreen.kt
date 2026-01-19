package com.example.mahayuga.feature.navyuga.presentation.home

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.ui.theme.BrandBlue
import kotlin.math.roundToInt


private val NavyBlue = Color(0xFF0F172A)
private val FabColor = Color(0xFF4361EE)

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
    var showFilterSheet by remember { mutableStateOf(false) }

    // ⚡ Sticky Search/Filter Logic
    // Height of Title Bar (which collapses)
    val titleHeight = 60.dp
    val titleHeightPx = with(LocalDensity.current) { titleHeight.toPx() }

    // Total Header Height (Title + Search + Filter + Padding) used for initial content padding
    val searchRowHeight = 64.dp
    val filterRowHeight = 56.dp
    val totalHeaderHeight = titleHeight + searchRowHeight + filterRowHeight

    var headerOffsetHeightPx by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = headerOffsetHeightPx + delta
                // ⚡ Only collapse up to the Title height, so Search+Filters remain visible
                headerOffsetHeightPx = newOffset.coerceIn(-titleHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger) {
            listState.animateScrollToItem(0)
            headerOffsetHeightPx = 0f
        }
    }

    Scaffold(
        containerColor = NavyBlue,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onRoiClick,
                containerColor = FabColor.copy(alpha = 0.8f),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(60.dp)
                    .offset(y = (-10).dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(Icons.Default.Calculate, "Calculate ROI", modifier = Modifier.size(20.dp))
                    Text(
                        "ROI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .nestedScroll(nestedScrollConnection)
        ) {
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = FabColor)
                }
            } else {
                LazyColumn(
                    state = listState,
                    // ⚡ Push list down by total header height
                    contentPadding = PaddingValues(top = totalHeaderHeight + 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (uiState.properties.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No properties match your filters.", color = Color.Gray)
                            }
                        }
                    } else {
                        items(uiState.properties, key = { it.id }) { property ->
                            InstagramStylePropertyCard(
                                property = property,
                                onItemClick = { onNavigateToDetail(property.id) },
                                onLikeClick = {
                                    viewModel.toggleLike(
                                        property.id,
                                        property.isLiked
                                    )
                                },
                                onShareClick = {
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Check out this property: ${property.title}"
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, null))
                                },
                                onInvestClick = {
                                    try {
                                        val message =
                                            "Hello, I am interested in investing in *${property.title}*."
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }

            // ⚡ COLLAPSIBLE + STICKY HEADER
            // This Box contains Title, Search, and Filters
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(x = 0, y = headerOffsetHeightPx.roundToInt()) }
                    .background(NavyBlue)
            ) {
                Column {
                    // 1. Title (Collapses away)
                    HomeTopBar(modifier = Modifier.height(titleHeight))

                    // 2. Search Bar (Sticks)
                    // ⚡ Search is now a BUTTON triggering navigation
                    SearchBarButton(
                        modifier = Modifier.height(searchRowHeight),
                        onClick = onNavigateToSearch,
                        onFilterClick = { showFilterSheet = true }
                    )

                    // 3. Filter Buttons (Sticks)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(filterRowHeight)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterButtonOutline(
                            text = "Funding",
                            count = uiState.fundingCount, // ⚡ Dynamic filtered counts
                            isSelected = uiState.selectedFilter == "Funding",
                            modifier = Modifier.weight(1f)
                        ) { viewModel.updateFilter("Funding") }

                        FilterButtonOutline(
                            text = "Funded",
                            count = uiState.fundedCount,
                            isSelected = uiState.selectedFilter == "Funded",
                            modifier = Modifier.weight(1f)
                        ) { viewModel.updateFilter("Funded") }

                        FilterButtonOutline(
                            text = "Exited",
                            count = uiState.exitedCount,
                            isSelected = uiState.selectedFilter == "Exited",
                            modifier = Modifier.weight(1f)
                        ) { viewModel.updateFilter("Exited") }
                    }
                }
            }
        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                containerColor = Color(0xFF1E1E1E)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Filter Properties",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { viewModel.clearAllFilters() }) {
                            Text(
                                "Clear All",
                                color = FabColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    FilterOptionRow(
                        "Location",
                        listOf("Mumbai", "Bangalore", "Delhi", "Kolkata", "Gurugram"),
                        uiState.activeLocations
                    ) { viewModel.toggleLocation(it) }
                    HorizontalDivider(
                        color = Color.White.copy(0.1f),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    FilterOptionRow(
                        "Budget (Valuation)",
                        listOf("Upto 50L", "50L - 2 Cr", "Above 2 Cr"),
                        uiState.activeBudgets
                    ) { viewModel.toggleBudget(it) }
                    HorizontalDivider(
                        color = Color.White.copy(0.1f),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    FilterOptionRow(
                        "Asset Manager",
                        listOf("Mindspace", "Nuvama", "Brookfield"),
                        uiState.activeManagers
                    ) { viewModel.toggleManager(it) }
                    HorizontalDivider(
                        color = Color.White.copy(0.1f),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    FilterOptionRow(
                        "Type",
                        listOf("Office", "Retail", "Warehouse", "Industrial"),
                        uiState.activeTypes
                    ) { viewModel.toggleType(it) }
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { showFilterSheet = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FabColor),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Show Results", fontWeight = FontWeight.Bold) }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun HomeTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Navyuga",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
        // Only Notification Icon
        Icon(
            Icons.Default.Notifications,
            "Notifications",
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
fun SearchBarButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mock Search Field (Clickable Box)
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.1f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    null,
                    tint = Color.White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Search properties...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .clickable { onFilterClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.FilterList, "Filter", tint = Color.White)
        }
    }
}

@Composable
fun FilterButtonOutline(
    text: String,
    count: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) FabColor else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) FabColor else Color.Gray.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
        modifier = modifier.height(40.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.White.copy(0.9f) else Color.Gray
            )
        }
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
            title,
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
                            option,
                            color = if (isSelected) Color.White else Color.White.copy(0.7f)
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (isSelected) FabColor else Color.White.copy(alpha = 0.05f)
                    ),
                    border = if (isSelected) null else BorderStroke(
                        1.dp,
                        Color.White.copy(alpha = 0.2f)
                    )
                )
            }
        }
    }
}