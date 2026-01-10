package com.example.mahayuga.feature.navyuga.presentation.search

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.navyuga.presentation.home.InstagramStylePropertyCard
import com.example.mahayuga.feature.navyuga.presentation.home.SearchBarRow
import com.example.mahayuga.feature.navyuga.presentation.home.StoryState
import com.example.mahayuga.feature.navyuga.presentation.home.FilterOptionRow
import com.example.mahayuga.ui.theme.BrandBlue
import kotlinx.coroutines.launch

private val StoryGradientStart = Color(0xFF4361EE)
private val StoryGradientEnd = Color(0xFF3F37C9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    country: String,
    city: String,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onRoiClick: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    LaunchedEffect(country, city) {
        viewModel.performSearch(country, city)
    }

    val searchState by viewModel.searchResults.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeBudgets by viewModel.activeBudgets.collectAsStateWithLifecycle()
    val activeManagers by viewModel.activeManagers.collectAsStateWithLifecycle()
    val activeTypes by viewModel.activeTypes.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }

    // Scroll to Top Logic
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Funding",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Black)
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                // Scroll to Top FAB
                AnimatedVisibility(
                    visible = showScrollToTop,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    FloatingActionButton(
                        onClick = { scope.launch { listState.animateScrollToItem(0) } },
                        containerColor = Color.DarkGray,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, "Scroll Top")
                    }
                }

                Spacer(Modifier.height(16.dp))

                FloatingActionButton(
                    onClick = onRoiClick,
                    containerColor = Color(0xFF4361EE).copy(alpha = 0.8f),
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
                        Icon(
                            Icons.Default.Calculate,
                            "Calculate ROI",
                            modifier = Modifier.size(20.dp)
                        )
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            SearchBarRow(
                query = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onFilterClick = { showFilterSheet = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = searchState) {
                is UiState.Loading -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = Color(0xFF60A5FA)) }
                }

                is UiState.Failure -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Coming Soon",
                            color = Color.Gray,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                is UiState.Success -> {
                    val properties = state.data

                    if (properties.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No properties match.", color = Color.Gray)
                        }
                    } else {
                        val stories = properties.map { prop ->
                            StoryState(
                                id = prop.id,
                                imageUrl = if (prop.imageUrls.isNotEmpty()) prop.imageUrls[0] else "",
                                title = prop.title.take(10),
                                isSeen = false
                            )
                        }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            item {
                                Text(
                                    "Trending in $city",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White.copy(0.9f)
                                    ),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }

                            item {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(stories) { story ->
                                        StoryCircle(
                                            story = story,
                                            onClick = { onNavigateToDetail(story.id) })
                                    }
                                }
                            }

                            item { HorizontalDivider(color = Color.White.copy(0.1f)) }

                            items(properties) { property ->
                                InstagramStylePropertyCard(
                                    property = property,
                                    onItemClick = { onNavigateToDetail(property.id) },
                                    onLikeClick = {
                                        viewModel.toggleLike(
                                            property.id,
                                            property.isLiked
                                        )
                                    },
                                    onShareClick = { /* Share Logic */ },
                                    onInvestClick = { onNavigateToDetail(property.id) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                )
                            }
                            item { Spacer(Modifier.height(100.dp)) }
                        }
                    }
                }

                else -> {}
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
                            Text("Clear All", color = BrandBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    FilterOptionRow(
                        title = "Budget (Valuation)",
                        options = listOf("Upto 50L", "50L - 2 Cr", "Above 2 Cr"),
                        selectedOptions = activeBudgets,
                        onOptionSelected = { viewModel.toggleBudget(it) }
                    )

                    HorizontalDivider(
                        color = Color.White.copy(0.1f),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    FilterOptionRow(
                        title = "Asset Manager",
                        options = listOf("Mindspace", "Nuvama", "Brookfield"),
                        selectedOptions = activeManagers,
                        onOptionSelected = { viewModel.toggleManager(it) }
                    )

                    HorizontalDivider(
                        color = Color.White.copy(0.1f),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    FilterOptionRow(
                        title = "Type",
                        options = listOf("Office", "Retail", "Warehouse", "Industrial"),
                        selectedOptions = activeTypes,
                        onOptionSelected = { viewModel.toggleType(it) }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { showFilterSheet = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
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

@Composable
fun StoryCircle(story: StoryState, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }) {
        Box(modifier = Modifier.size(76.dp), contentAlignment = Alignment.Center) {
            if (!story.isSeen) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .border(
                            width = 2.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    StoryGradientStart,
                                    StoryGradientEnd
                                )
                            ),
                            shape = CircleShape
                        )
                )
            }
            AsyncImage(
                model = story.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(66.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = story.title,
            style = MaterialTheme.typography.bodySmall,
            color = if (story.isSeen) Color.Gray else Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(70.dp),
            textAlign = TextAlign.Center
        )
    }
}