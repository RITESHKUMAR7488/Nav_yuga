package com.example.mahayuga.feature.navyuga.presentation.home

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.auth.presentation.components.formatIndian

private val DeepDarkBlue = Color(0xFF0F172A)
private val FabColor = Color(0xFF4361EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onRoiClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val supportNumber by viewModel.supportNumber.collectAsState()
    val context = LocalContext.current

    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            HomeTopBar(
                onBackClick = onNavigateBack,
                onNotificationClick = { /* Handle Notification */ }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onRoiClick,
                containerColor = FabColor.copy(alpha = 0.8f),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(60.dp)
                    .offset(y = 20.dp)
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
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FabColor)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    SearchBarRow(
                        query = uiState.searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onFilterClick = { showFilterSheet = true }
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilterButton(
                            "Funding",
                            uiState.selectedFilter == "Funding",
                            Modifier.weight(1f)
                        ) { viewModel.updateFilter("Funding") }
                        FilterButton(
                            "Funded",
                            uiState.selectedFilter == "Funded",
                            Modifier.weight(1f)
                        ) { viewModel.updateFilter("Funded") }
                        FilterButton(
                            "Exited",
                            uiState.selectedFilter == "Exited",
                            Modifier.weight(1f)
                        ) { viewModel.updateFilter("Exited") }
                    }
                }

                item { HorizontalDivider(color = Color.White.copy(0.1f)) }

                if (uiState.properties.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No properties match your filters.", color = Color.Gray)
                        }
                    }
                } else {
                    items(uiState.properties, key = { it.id }) { property ->
                        InstagramStylePropertyCard(
                            property = property,
                            onItemClick = { onNavigateToDetail(property.id) },
                            onLikeClick = { viewModel.toggleLike(property.id, property.isLiked) },
                            onShareClick = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Check out this property: ${property.title}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, null))
                            },
                            onInvestClick = {
                                try {
                                    val message = "Hello, I am interested in investing in *${property.title}*."
                                    val url = "https://api.whatsapp.com/send?phone=$supportNumber&text=${Uri.encode(message)}"
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse(url); setPackage("com.whatsapp")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "WhatsApp not found", Toast.LENGTH_SHORT).show()
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

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                containerColor = Color(0xFF1E1E1E)
            ) {
                Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
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
                            Text("Clear All", color = FabColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    FilterOptionRow(
                        title = "Location",
                        options = listOf("Mumbai", "Bangalore", "Delhi", "Kolkata", "Gurugram"),
                        selectedOptions = uiState.activeLocations,
                        onOptionSelected = { viewModel.toggleLocation(it) }
                    )

                    HorizontalDivider(color = Color.White.copy(0.1f), modifier = Modifier.padding(vertical = 16.dp))

                    FilterOptionRow(
                        title = "Budget (Valuation)",
                        options = listOf("Upto 50L", "50L - 2 Cr", "Above 2 Cr"),
                        selectedOptions = uiState.activeBudgets,
                        onOptionSelected = { viewModel.toggleBudget(it) }
                    )

                    HorizontalDivider(color = Color.White.copy(0.1f), modifier = Modifier.padding(vertical = 16.dp))

                    FilterOptionRow(
                        title = "Asset Manager",
                        options = listOf("Mindspace", "Nuvama", "Brookfield"),
                        selectedOptions = uiState.activeManagers,
                        onOptionSelected = { viewModel.toggleManager(it) }
                    )

                    HorizontalDivider(color = Color.White.copy(0.1f), modifier = Modifier.padding(vertical = 16.dp))

                    FilterOptionRow(
                        title = "Type",
                        options = listOf("Office", "Retail", "Warehouse", "Industrial"),
                        selectedOptions = uiState.activeTypes,
                        onOptionSelected = { viewModel.toggleType(it) }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { showFilterSheet = false },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FabColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Show Results", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// ... SearchBarRow (Already Correct from previous turn) ...

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
                    label = { Text(option, color = if(isSelected) Color.White else Color.White.copy(0.7f)) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (isSelected) FabColor else Color.White.copy(alpha = 0.05f)
                    ),
                    border = if (isSelected) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                )
            }
        }
    }
}

// ... InstagramStylePropertyCard, VerticalBar, FilterButton, HomeTopBar, PropertyStat ...
// (These remain unchanged from previous correct versions)
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
            placeholder = { Text("Search properties...", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.6f)) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White.copy(alpha = 0.6f)) },
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

@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) FabColor else Color.White.copy(
                0.1f
            ), contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier.height(50.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
fun HomeTopBar(onBackClick: () -> Unit, onNotificationClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = "Navyuga",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier.align(Alignment.Center)
        )
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Color.White
            )
        }
    }
}

@Composable
fun InstagramStylePropertyCard(
    property: PropertyModel,
    onItemClick: () -> Unit,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    onInvestClick: () -> Unit,
    modifier: Modifier = Modifier,
    showInvestButton: Boolean = true
) {
    val scale by animateFloatAsState(if (property.isLiked) 1.2f else 1.0f, label = "like")
    val isExited = property.status == "Exited"

    Card(
        modifier = modifier.clickable { onItemClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = property.mainImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        property.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        property.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(Icons.Default.MoreVert, "Options", tint = MaterialTheme.colorScheme.onSurface)
            }

            Box(Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .height(320.dp)) {
                AsyncImage(
                    model = property.mainImage,
                    contentDescription = "Property Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(60.dp)
                        .background(Color.Black.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { property.fundedPercent / 100f },
                        modifier = Modifier.fillMaxSize(),
                        color = FabColor,
                        trackColor = Color.White.copy(alpha = 0.2f),
                        strokeWidth = 4.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${property.fundedPercent}%",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Funded",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp)
                        )
                    }
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) { PropertyStat("Tenant", property.tenantName.ifEmpty { "-" }) }
                VerticalBar()
                Box(
                    Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) { PropertyStat("Sq ft", property.area.ifEmpty { "-" }) }
                VerticalBar()
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    PropertyStat(
                        "Tenure",
                        if (property.occupationPeriod.isNotEmpty()) "${property.occupationPeriod} Yrs" else "-"
                    )
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isExited) {
                    Box(
                        Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) { PropertyStat("Entry", "₹${formatIndian(property.totalValuation)}") }
                    VerticalBar()
                    Box(
                        Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) { PropertyStat("Exit", "₹${formatIndian(property.exitPrice)}") }
                    VerticalBar()
                    Box(
                        Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) { PropertyStat("Profit", "₹${formatIndian(property.totalProfit)}", true) }
                } else {
                    Box(
                        Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) { PropertyStat("Price", "₹${formatIndian(property.totalValuation)}") }
                    VerticalBar()

                    val annualRent =
                        if (property.grossAnnualRent.isNotEmpty()) property.grossAnnualRent else {
                            val monthly =
                                property.monthlyRent.replace(",", "").toDoubleOrNull() ?: 0.0
                            (monthly * 12).toString()
                        }
                    Box(
                        Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) { PropertyStat("Rent/Year", "₹${formatIndian(annualRent)}") }

                    VerticalBar()
                    Box(
                        Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) { PropertyStat("ROI", "${property.roi}%", true) }
                }
            }

            if (property.status == "Funding") {
                Text(
                    text = "Min Investment - ₹${formatIndian(property.minInvest)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = FabColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .padding(bottom = 8.dp)
                )
            }

            HorizontalDivider(color = Color.White.copy(0.1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onLikeClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (property.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        "Like",
                        tint = if (property.isLiked) Color.Red else Color.White,
                        modifier = Modifier.scale(scale)
                    )
                }
                if (!isExited && showInvestButton && property.status != "Funded") {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onInvestClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "INVEST",
                            color = FabColor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                } else {
                    Spacer(Modifier.weight(1f))
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onShareClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        "Share",
                        tint = Color.White,
                        modifier = Modifier
                            .rotate(-45f)
                            .padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun VerticalBar() {
    Box(Modifier
        .width(1.dp)
        .height(32.dp)
        .background(Color.Gray.copy(0.2f)))
}

@Composable
fun PropertyStat(label: String, value: String, isHighlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}