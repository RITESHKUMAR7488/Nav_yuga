// main/java/com/example/mahayuga/feature/navyuga/presentation/discover/DiscoverScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.discover

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
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
import com.example.mahayuga.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel = hiltViewModel(),
    onNavigateToSmReitDetail: (String) -> Unit = {},
    onNavigateToReitDetail: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    // Navigation States within the Discover section
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedFlashIndex by remember { mutableIntStateOf(-1) } // -1 means no flash is open
    var isMenuOpen by remember { mutableStateOf(false) } // Placeholder for Menu

    // Handle system back button to close Flash pager or Search
    BackHandler(enabled = isSearchActive || selectedFlashIndex != -1) {
        if (selectedFlashIndex != -1) {
            selectedFlashIndex = -1
        } else if (isSearchActive) {
            isSearchActive = false
        }
    }

    if (selectedFlashIndex != -1) {
        // ⚡ FULL SCREEN FLASH PAGER (Requirement 9)
        FlashVerticalPager(
            initialPage = selectedFlashIndex,
            flashes = state.flashes,
            onClose = { selectedFlashIndex = -1 },
            onLikeToggle = { viewModel.toggleFlashLike(it) },
            onSaveToggle = { viewModel.toggleFlashSave(it) }
        )
    } else if (isSearchActive) {
        // ⚡ DEDICATED SEARCH PAGE (Requirement 3 & 13)
        DiscoverSearchScreen(
            state = state,
            onQueryChange = { viewModel.onSearchQueryChange(it) },
            onBackClick = { isSearchActive = false },
            onFlashClick = { flashId ->
                selectedFlashIndex = state.flashes.indexOfFirst { it.id == flashId }
            }
        )
    } else {
        // ⚡ MAIN DISCOVER FEED
        Scaffold(
            containerColor = BricxBackground,
            topBar = {
                // Header matching Home Screen (Requirement 1 & 2)
                Column(
                    modifier = Modifier
                        .background(BricxBackground)
                        .statusBarsPadding()
                ) {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Explore,
                                    contentDescription = "Discover",
                                    tint = BricxBrandTeal,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Discover",
                                    color = BricxTextPrimary,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(Icons.Outlined.Search, "Search", tint = BricxTextPrimary)
                            }
                            IconButton(onClick = { isMenuOpen = !isMenuOpen }) {
                                Icon(Icons.Outlined.Menu, "Menu", tint = BricxTextPrimary)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = BricxBackground)
                    )
                    HorizontalDivider(color = BricxSurfaceCardLight.copy(alpha = 0.5f))
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Full Size Advertisement Board (Requirement 4)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(BricxSurfaceCardLight),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        // Simulated Background Image
                        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray.copy(alpha=0.5f)))

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Featured Content", color = BricxBrandTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(state.topBannerTitle, color = BricxTextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text(state.topBannerSubtitle, color = BricxTextSecondary, fontSize = 14.sp)
                        }
                    }
                }

                // Flash Section (Requirement 5)
                item {
                    Text(
                        "Flash",
                        color = BricxTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 12.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.flashes.size) { index ->
                            val flash = state.flashes[index]
                            // Thumbnail Card for Flash
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BricxSurfaceCard)
                                    .clickable { selectedFlashIndex = index },
                                contentAlignment = Alignment.BottomStart
                            ) {
                                // Simulated Video Thumbnail Background
                                Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1E293B)))

                                Icon(
                                    Icons.Filled.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White.copy(alpha=0.7f),
                                    modifier = Modifier.align(Alignment.Center).size(36.dp)
                                )

                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        flash.title,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text("${flash.likesCount} likes", color = Color.LightGray, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Long Videos Section (Requirement 11 & 12)
                item {
                    Text(
                        "Long Videos", // Replaced Learn & Grow
                        color = BricxTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                }

                items(state.longVideos) { video ->
                    LongVideoCard(video = video)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ---------------- SUB-SCREENS & COMPONENTS ----------------

@Composable
fun DiscoverSearchScreen(
    state: DiscoverState,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onFlashClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BricxBackground)
            .statusBarsPadding()
    ) {
        // Search Header (Requirement 3)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = BricxTextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onQueryChange,
                placeholder = { Text("Search videos, managers, properties etc", color = BricxTextSecondary, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BricxSurfaceCardLight,
                    unfocusedContainerColor = BricxSurfaceCardLight,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = BricxTextPrimary,
                    unfocusedTextColor = BricxTextPrimary
                ),
                singleLine = true
            )
        }

        if (state.searchQuery.isEmpty()) {
            // Suggestions
            Text(
                "Suggestions",
                color = BricxTextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            state.searchSuggestions.forEach { suggestion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onQueryChange(suggestion) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Search, null, tint = BricxTextSecondary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(suggestion, color = BricxTextPrimary)
                }
            }
        } else {
            // Search Results
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(state.searchResults) { result ->
                    when (result) {
                        is FlashVideo -> {
                            SearchResultFlashItem(result, onClick = { onFlashClick(result.id) })
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        is LongVideo -> {
                            SearchResultLongVideoItem(result)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultFlashItem(flash: FlashVideo, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.background(BricxSurfaceCard, RoundedCornerShape(8.dp)).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(50.dp, 80.dp).clip(RoundedCornerShape(4.dp)).background(Color.DarkGray)) {
            Icon(Icons.Filled.PlayArrow, null, tint = Color.White, modifier = Modifier.align(Alignment.Center))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("Flash", color = BricxBrandTeal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(flash.title, color = BricxTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(flash.authorName, color = BricxTextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
fun SearchResultLongVideoItem(video: LongVideo) {
    Row(
        modifier = Modifier.fillMaxWidth().background(BricxSurfaceCard, RoundedCornerShape(8.dp)).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(100.dp, 60.dp).clip(RoundedCornerShape(4.dp)).background(Color.DarkGray)) {
            Text(video.duration, color = Color.White, fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp).background(Color.Black.copy(0.7f)))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("Video", color = BricxBrandBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(video.title, color = BricxTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(video.authorName, color = BricxTextSecondary, fontSize = 12.sp)
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun FlashVerticalPager(
    initialPage: Int,
    flashes: List<FlashVideo>,
    onClose: () -> Unit,
    onLikeToggle: (String) -> Unit,
    onSaveToggle: (String) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { flashes.size })

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val flash = flashes[page]
            Box(modifier = Modifier.fillMaxSize()) {
                // Background simulated video
                Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212))) {
                    Text("Simulated Playing Video\n${flash.title}", color = Color.White.copy(0.2f), modifier = Modifier.align(Alignment.Center))
                }

                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                    Text("Flash", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 12.dp))
                    IconButton(onClick = { /* Mute/Unmute */ }) {
                        Icon(Icons.Outlined.VolumeUp, "Volume", tint = Color.White)
                    }
                }

                // Right Side Actions (Requirement 6, 7, 8)
                Column(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 100.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Like
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { onLikeToggle(flash.id) }) {
                            Icon(
                                if (flash.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                "Like",
                                tint = if (flash.isLiked) BricxDangerRed else Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(flash.likesCount.toString(), color = Color.White, fontSize = 12.sp)
                    }

                    // Comment (Requirement 7)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { /* Open Comments */ }) {
                            Icon(Icons.Outlined.ChatBubbleOutline, "Comment", tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                        Text(flash.commentsCount.toString(), color = Color.White, fontSize = 12.sp)
                    }

                    // Share (Requirement 8)
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Outlined.Share, "Share", tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    // Save
                    IconButton(onClick = { onSaveToggle(flash.id) }) {
                        Icon(
                            if (flash.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            "Save",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Bottom Left Text Info (Requirement 10 - Bold and Visible)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(0.8f) // Leave room for right icons
                        .padding(start = 16.dp, bottom = 40.dp)
                ) {
                    Text(
                        text = "@${flash.authorName}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = flash.title,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold, // Very Bold
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = flash.description,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold, // Ensure visibility
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LongVideoCard(video: LongVideo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { /* Play Video */ },
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Thumbnail / Preview Area (Requirement 12)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // If it was actually playing, you'd load a VideoPlayer here based on a scroll state listener.
                // For now, it's a simulated thumbnail.
                Icon(Icons.Filled.PlayArrow, null, tint = Color.White.copy(0.7f), modifier = Modifier.size(48.dp))

                Text(
                    video.duration,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(0.7f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    video.title,
                    color = BricxTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(BricxSurfaceCardLight))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(video.authorName, color = BricxTextSecondary, fontSize = 12.sp)
                    Text(" • ", color = BricxTextSecondary, fontSize = 12.sp)
                    Text(video.views, color = BricxTextSecondary, fontSize = 12.sp)
                    Text(" • ", color = BricxTextSecondary, fontSize = 12.sp)
                    Text(video.postedTime, color = BricxTextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}