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
import kotlin.math.roundToInt

private val DeepDarkBlue = Color(0xFF0F172A)
private val FabColor = Color(0xFF4361EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    onRoiClick: () -> Unit,
    scrollToTopTrigger: Boolean, // ⚡ Trigger from Dashboard
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val supportNumber by viewModel.supportNumber.collectAsState()
    val context = LocalContext.current

    // Scroll Control
    val listState = rememberLazyListState()
    var showFilterSheet by remember { mutableStateOf(false) }

    // ⚡ Header Hiding Logic (Collapsing Toolbar)
    val headerHeight = 140.dp // Approx height of TopBar + Filter Row
    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }
    var headerOffsetHeightPx by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = headerOffsetHeightPx + delta
                headerOffsetHeightPx = newOffset.coerceIn(-headerHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    // ⚡ Handle Scroll to Top Trigger
    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger) {
            listState.animateScrollToItem(0)
            headerOffsetHeightPx = 0f // Reset header
        }
    }

    Scaffold(
        containerColor = Color.Black,
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
                    // Push content down by header height to avoid overlap initially
                    contentPadding = PaddingValues(top = headerHeight + 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // ⚡ Search Bar (Scrolls with cards)
                    item {
                        SearchBarRow(
                            query = uiState.searchQuery,
                            onQueryChange = { viewModel.updateSearchQuery(it) },
                            onFilterClick = { showFilterSheet = true }
                        )
                    }

                    item { HorizontalDivider(color = Color.White.copy(0.1f)) }

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

            // ⚡ Collapsible Header
            Box(
                modifier = Modifier
                    .height(headerHeight)
                    .fillMaxWidth()
                    .offset { IntOffset(x = 0, y = headerOffsetHeightPx.roundToInt()) }
                    .background(Color.Black)
            ) {
                Column {
                    // Row 1: Header (Left Align Title, Wallet, Currency, Notif)
                    HomeTopBar()

                    // Row 2: Filter Buttons (Outlined with Counts)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterButtonOutline(
                            text = "Funding",
                            count = uiState.fundingCount,
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
                Column(modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())) {
                    Text(
                        "Filter Properties",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { showFilterSheet = false },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Apply") }
                }
            }
        }
    }
}

@Composable
fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Aligned Title
        Text(
            text = "Navyuga",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )

        // Icons: Wallet, Currency, Notification
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.AccountBalanceWallet,
                "Wallet",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
            Icon(
                Icons.Default.CurrencyRupee,
                "Currency",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
            Icon(
                Icons.Default.Notifications,
                "Notifications",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
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
        border = BorderStroke(1.dp, if (isSelected) FabColor else Color.Gray.copy(alpha = 0.5f)),
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

@Composable
fun SearchBarRow(
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            placeholder = {
                Text(
                    "Search properties...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    null,
                    tint = Color.White.copy(alpha = 0.6f)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.1f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

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