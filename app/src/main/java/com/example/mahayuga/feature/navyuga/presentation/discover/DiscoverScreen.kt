// main/java/com/example/mahayuga/feature/navyuga/presentation/discover/DiscoverScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.discover

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mahayuga.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel = hiltViewModel(),
    onNavigateToSmReitDetail: (String) -> Unit = {},
    onNavigateToReitDetail: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Navigation States within the Discover section
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedFlashIndex by remember { mutableIntStateOf(-1) }
    var selectedLongVideo by remember { mutableStateOf<LongVideo?>(null) }
    var isMenuExpanded by remember { mutableStateOf(false) }

    // Handle system back button to close pagers or Search
    BackHandler(enabled = isSearchActive || selectedFlashIndex != -1 || selectedLongVideo != null) {
        if (selectedFlashIndex != -1) {
            selectedFlashIndex = -1
        } else if (selectedLongVideo != null) {
            selectedLongVideo = null
        } else if (isSearchActive) {
            isSearchActive = false
        }
    }

    if (selectedFlashIndex != -1) {
        // ⚡ Dialog completely covers Bottom Nav Bar
        Dialog(
            onDismissRequest = { selectedFlashIndex = -1 },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            FlashVerticalPager(
                initialPage = selectedFlashIndex,
                flashes = state.flashes,
                onClose = { selectedFlashIndex = -1 },
                onLikeToggle = { viewModel.toggleFlashLike(it) },
                onSaveToggle = { viewModel.toggleFlashSave(it) }
            )
        }
    } else if (selectedLongVideo != null) {
        // ⚡ Dialog completely covers Bottom Nav Bar
        Dialog(
            onDismissRequest = { selectedLongVideo = null },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            LongVideoDetailScreen(
                video = selectedLongVideo!!,
                onClose = { selectedLongVideo = null }
            )
        }
    } else if (isSearchActive) {
        DiscoverSearchScreen(
            state = state,
            onQueryChange = { viewModel.onSearchQueryChange(it) },
            onBackClick = { isSearchActive = false },
            onFlashClick = { flashId ->
                selectedFlashIndex = state.flashes.indexOfFirst { it.id == flashId }
            },
            onVideoClick = { videoId ->
                selectedLongVideo = state.longVideos.find { it.id == videoId }
            }
        )
    } else {
        Scaffold(
            containerColor = BricxBackground,
            topBar = {
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
                            // ⚡ Menu Functionality Added
                            Box {
                                IconButton(onClick = { isMenuExpanded = true }) {
                                    Icon(Icons.Outlined.Menu, "Menu", tint = BricxTextPrimary)
                                }
                                DropdownMenu(
                                    expanded = isMenuExpanded,
                                    onDismissRequest = { isMenuExpanded = false },
                                    modifier = Modifier.background(BricxSurfaceCardLight)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Saved Videos", color = BricxTextPrimary) },
                                        onClick = {
                                            isMenuExpanded = false
                                            Toast.makeText(
                                                context,
                                                "Saved Videos",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Liked Flashes", color = BricxTextPrimary) },
                                        onClick = {
                                            isMenuExpanded = false
                                            Toast.makeText(
                                                context,
                                                "Liked Flashes",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("History", color = BricxTextPrimary) },
                                        onClick = {
                                            isMenuExpanded = false
                                            Toast.makeText(context, "History", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    )
                                }
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
                // Top Banner
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
                        AsyncImage(
                            model = state.topBannerUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                        )

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Featured Content",
                                color = BricxBrandTeal,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                state.topBannerTitle,
                                color = BricxTextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                state.topBannerSubtitle,
                                color = BricxTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Flash Section
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
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BricxSurfaceCard)
                                    .clickable { selectedFlashIndex = index },
                                contentAlignment = Alignment.BottomStart
                            ) {
                                AsyncImage(
                                    model = flash.thumbnailUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.4f))
                                )

                                Icon(
                                    Icons.Filled.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(36.dp)
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
                                    Text(
                                        "${flash.likesCount} likes",
                                        color = Color.LightGray,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Long Videos
                item {
                    Text(
                        "Long Videos",
                        color = BricxTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                }

                items(state.longVideos) { video ->
                    LongVideoCard(
                        video = video,
                        onClick = { selectedLongVideo = video }
                    )
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
    onFlashClick: (String) -> Unit,
    onVideoClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BricxBackground)
            .statusBarsPadding()
    ) {
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
                placeholder = {
                    Text(
                        "Search videos, managers, properties etc",
                        color = BricxTextSecondary,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
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
                    Icon(
                        Icons.Outlined.Search,
                        null,
                        tint = BricxTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(suggestion, color = BricxTextPrimary)
                }
            }
        } else {
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
                            SearchResultLongVideoItem(result, onClick = { onVideoClick(result.id) })
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(BricxSurfaceCard, RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier
            .size(50.dp, 80.dp)
            .clip(RoundedCornerShape(4.dp))) {
            AsyncImage(
                model = flash.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)))
            Icon(
                Icons.Filled.PlayArrow,
                null,
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("Flash", color = BricxBrandTeal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(
                flash.title,
                color = BricxTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(flash.authorName, color = BricxTextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
fun SearchResultLongVideoItem(video: LongVideo, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(BricxSurfaceCard, RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier
            .size(100.dp, 60.dp)
            .clip(RoundedCornerShape(4.dp))) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                video.duration,
                color = Color.White,
                fontSize = 10.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .background(Color.Black.copy(0.7f))
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("Video", color = BricxBrandBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(
                video.title,
                color = BricxTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
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

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val flash = flashes[page]
            Box(modifier = Modifier.fillMaxSize()) {

                // Actual Background
                AsyncImage(
                    model = flash.thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(0.4f))
                ) // Darken for text

                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                    Text(
                        "Flash",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    IconButton(onClick = { /* Mute/Unmute */ }) {
                        Icon(Icons.Outlined.VolumeUp, "Volume", tint = Color.White)
                    }
                }

                // Right Side Actions
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 100.dp, end = 16.dp),
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

                    // Comment
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { /* Open Comments */ }) {
                            Icon(
                                Icons.Outlined.ChatBubbleOutline,
                                "Comment",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(flash.commentsCount.toString(), color = Color.White, fontSize = 12.sp)
                    }

                    // Message / Share (Paper Plane Icon)
                    IconButton(onClick = { /* Share */ }) {
                        Icon(
                            Icons.AutoMirrored.Outlined.Send,
                            "Message",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
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

                // Bottom Left Text Info
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(0.8f)
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
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = flash.description,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LongVideoCard(video: LongVideo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.3f)))

                Icon(
                    Icons.Filled.PlayArrow,
                    null,
                    tint = Color.White.copy(0.9f),
                    modifier = Modifier.size(48.dp)
                )

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
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(BricxSurfaceCardLight)
                    )
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

// ⚡ Restored YouTube Style Video Detail
@Composable
fun LongVideoDetailScreen(video: LongVideo, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BricxBackground)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
        ) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    "Play",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 8.dp, start = 8.dp)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    "Close",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                video.title,
                color = BricxTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "${video.views} • ${video.postedTime}",
                color = BricxTextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(BricxSurfaceCard, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        video.authorName.take(1),
                        color = BricxBrandTeal,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        video.authorName,
                        color = BricxTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        "AUM: ${video.aum}",
                        color = BricxTextSecondary,
                        fontSize = 12.sp
                    )
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = BricxTextPrimary),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        "Subscribe",
                        color = BricxBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                EduActionBtn(Icons.Default.KeyboardArrowUp, "1.4K")
                EduActionBtn(Icons.Default.KeyboardArrowDown, "Dislike")
                EduActionBtn(Icons.AutoMirrored.Filled.Send, "Share")
                EduActionBtn(Icons.Default.Add, "Save")
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = BricxSurfaceCard)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Suggested Videos",
                color = BricxTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            repeat(3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BricxSurfaceCard)
                    ) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1560518883-ce09059eeffa?auto=format&fit=crop&q=80",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Understanding fractional real estate laws",
                            color = BricxTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Navyuga Assets",
                            color = BricxTextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            "34K views • 1 week ago",
                            color = BricxTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EduActionBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = BricxTextPrimary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = BricxTextPrimary, fontSize = 12.sp)
    }
}