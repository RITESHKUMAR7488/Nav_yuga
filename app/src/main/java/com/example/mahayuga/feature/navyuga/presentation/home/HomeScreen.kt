package com.example.mahayuga.feature.navyuga.presentation.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.auth.presentation.components.formatIndian // ⚡ IMPORT

private val DeepDarkBlue = Color(0xFF0F172A)
private val StoryGradientStart = Color(0xFF4361EE)
private val StoryGradientEnd = Color(0xFF3F37C9)
private val FabColor = Color(0xFF4361EE)

@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onRoiClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                containerColor = FabColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(80.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Calculate, "Calculate ROI", modifier = Modifier.size(28.dp))
                    Text(
                        "ROI\nCalculator",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 12.sp),
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
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // REMOVED: Greeting & Welcome Message

                // Stories
                item {
                    Column(Modifier.fillMaxWidth()) {
                        Text(
                            "Trending Properties",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = Color.White.copy(0.9f)),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(uiState.stories, key = { it.id }) { story ->
                                StoryCircle(story = story, onClick = {
                                    viewModel.markStoryAsSeen(story.id)
                                    onNavigateToDetail(story.id)
                                })
                            }
                        }
                    }
                }

                // Filter Buttons
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilterButton(
                            text = "Funding",
                            isSelected = uiState.selectedFilter == "Funding",
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.updateFilter("Funding") }
                        )
                        FilterButton(
                            text = "Funded",
                            isSelected = uiState.selectedFilter == "Funded",
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.updateFilter("Funded") }
                        )
                        FilterButton(
                            text = "Exited",
                            isSelected = uiState.selectedFilter == "Exited",
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.updateFilter("Exited") }
                        )
                    }
                }

                item { HorizontalDivider(color = Color.White.copy(0.1f)) }

                // Property Feed
                items(uiState.properties, key = { it.id }) { property ->
                    InstagramStylePropertyCard(
                        property = property,
                        onItemClick = { onNavigateToDetail(property.id) },
                        onLikeClick = { viewModel.toggleLike(property.id, property.isLiked) },
                        onShareClick = { /* Handle Share */ },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                }

                item { Spacer(Modifier.height(100.dp)) }
            }
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
            containerColor = if (isSelected) FabColor else Color.White.copy(0.1f),
            contentColor = Color.White
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
fun HomeTopBar(
    onBackClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    // ⚡ CHANGED: Using Box to center title absolutely while keeping buttons on edges
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Centered Title
        Text(
            text = "Navyuga",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier.align(Alignment.Center)
        )

        // Back Button (Left)
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

        // Notification Button (Right)
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
fun StoryCircle(
    story: StoryState,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.size(76.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!story.isSeen) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .border(
                            width = 2.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(StoryGradientStart, StoryGradientEnd)
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
            color = if(story.isSeen) Color.Gray else Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(70.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InstagramStylePropertyCard(
    property: PropertyModel,
    onItemClick: () -> Unit,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(if (property.isLiked) 1.2f else 1.0f, label = "like")

    Card(
        modifier = modifier.clickable { onItemClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = property.mainImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.Gray)
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(property.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    Text(property.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(Icons.Default.MoreVert, "Options", tint = MaterialTheme.colorScheme.onSurface)
            }

            Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp).height(320.dp).clip(RoundedCornerShape(16.dp))) {
                AsyncImage(
                    model = property.mainImage,
                    contentDescription = "Property Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ⚡ FORMATTED
                PropertyStat("Price", "₹${formatIndian(property.minInvest)}")
                Box(Modifier.width(1.dp).height(32.dp).background(Color.Gray.copy(0.2f)))

                // ⚡ FORMATTED
                val displayRent = if (property.rentReturn.isEmpty()) "₹15k" else "₹${formatIndian(property.rentReturn)}"
                PropertyStat("Rent", displayRent)

                Box(Modifier.width(1.dp).height(32.dp).background(Color.Gray.copy(0.2f)))

                PropertyStat("ROI", "${property.roi}%", true)
            }

            Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        if (property.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        "Like",
                        tint = if (property.isLiked) Color.Red else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.scale(scale)
                    )
                }
                IconButton(onClick = onShareClick) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Share", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.rotate(-45f).padding(bottom = 4.dp))
                }
            }
        }
    }
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