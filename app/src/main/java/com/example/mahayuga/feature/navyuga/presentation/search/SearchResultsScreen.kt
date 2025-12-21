package com.example.mahayuga.feature.navyuga.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.navyuga.presentation.home.InstagramStylePropertyCard
import com.example.mahayuga.feature.navyuga.presentation.home.StoryCircle
import com.example.mahayuga.feature.navyuga.presentation.home.StoryState

@Composable
fun SearchResultsScreen(
    country: String,
    city: String,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onRoiClick: () -> Unit, // Passed from NavHost
    viewModel: SearchViewModel = hiltViewModel()
) {
    LaunchedEffect(country, city) {
        viewModel.performSearch(country, city)
    }

    val searchState by viewModel.searchResults.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        },
        floatingActionButton = {
            // ⚡ EXACT REPLICA FAB
            FloatingActionButton(
                onClick = onRoiClick,
                containerColor = Color(0xFF4361EE),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .size(80.dp)
                    .offset(y = 20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Calculate, "Calculate ROI", modifier = Modifier.size(28.dp))
                    Text(
                        "ROI\nCalculator",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            lineHeight = 12.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
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
                    ) { Text(state.message, color = Color.Red) }
                }

                is UiState.Success -> {
                    val properties = state.data
                    val stories = properties.map { prop ->
                        StoryState(
                            id = prop.id,
                            imageUrl = if (prop.imageUrls.isNotEmpty()) prop.imageUrls[0] else "",
                            title = prop.title.take(10),
                            isSeen = false
                        )
                    }

                    LazyColumn(
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

                        // ⚡ RESTORED STORIES
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
                                onShareClick = { /* Handle Share */ },
                                onInvestClick = { onNavigateToDetail(property.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                        item { Spacer(Modifier.height(100.dp)) }
                    }
                }

                else -> {}
            }
        }
    }
}