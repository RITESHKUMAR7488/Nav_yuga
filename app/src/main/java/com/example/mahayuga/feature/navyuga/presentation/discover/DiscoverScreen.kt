// main/java/com/example/mahayuga/feature/navyuga/presentation/discover/DiscoverScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.discover

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
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

    var isSearchActive by remember { mutableStateOf(false) }
    var selectedFlashIndex by remember { mutableIntStateOf(-1) }
    var selectedLongVideo by remember { mutableStateOf<LongVideo?>(null) }
    var isMenuExpanded by remember { mutableStateOf(false) }

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
                                    Icons.Outlined.Explore,
                                    "Discover",
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
                            Box {
                                IconButton(onClick = { isMenuExpanded = true }) {
                                    Icon(Icons.Outlined.Menu, "Menu", tint = BricxTextPrimary)
                                }
                                DropdownMenu(
                                    expanded = isMenuExpanded,
                                    onDismissRequest = { isMenuExpanded = false },
                                    modifier = Modifier.background(BricxSurfaceCardLight)
                                ) {
                                    DropdownMenuItem(text = {
                                        Text(
                                            "Saved Videos",
                                            color = BricxTextPrimary
                                        )
                                    }, onClick = { isMenuExpanded = false })
                                    DropdownMenuItem(text = {
                                        Text(
                                            "Liked Flashes",
                                            color = BricxTextPrimary
                                        )
                                    }, onClick = { isMenuExpanded = false })
                                    DropdownMenuItem(text = {
                                        Text(
                                            "History",
                                            color = BricxTextPrimary
                                        )
                                    }, onClick = { isMenuExpanded = false })
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
                                    "Play",
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
                    LongVideoCard(video = video, onClick = { selectedLongVideo = video })
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ---------------- ROBUST LIFECYCLE AWARE EXOPLAYER ----------------
@Composable
fun DirectVideoPlayer(videoUrl: String, isShort: Boolean = false, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
            setMediaItem(mediaItem)
            repeatMode = if (isShort) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            prepare()
            playWhenReady = true
        }
    }

    // Handles playing and pausing based on App Lifecycle (Background/Foreground)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> exoPlayer.play()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(
        AndroidView(
            modifier = modifier.background(Color.Black),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = !isShort // Hide controls for Shorts
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            }
        )
    ) {
        onDispose {
            exoPlayer.release() // Prevents memory leaks
        }
    }
}

// ---------------- SUB-SCREENS & COMPONENTS ----------------

@OptIn(ExperimentalFoundationApi::class)
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

                DirectVideoPlayer(
                    videoUrl = flash.mp4Url,
                    isShort = true,
                    modifier = Modifier.fillMaxSize()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.background(Color.Black.copy(0.4f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 100.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { onLikeToggle(flash.id) },
                            modifier = Modifier.background(Color.Black.copy(0.4f), CircleShape)
                        ) {
                            Icon(
                                if (flash.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                "Like",
                                tint = if (flash.isLiked) BricxDangerRed else Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(flash.likesCount.toString(), color = Color.White, fontSize = 12.sp)
                    }
                    IconButton(
                        onClick = { onSaveToggle(flash.id) },
                        modifier = Modifier.background(Color.Black.copy(0.4f), CircleShape)
                    ) {
                        Icon(
                            if (flash.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            "Save",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(0.8f)
                        .padding(start = 16.dp, bottom = 40.dp)
                        .background(Color.Black.copy(0.4f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        "@${flash.authorName}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        flash.title,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LongVideoDetailScreen(video: LongVideo, onClose: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(BricxBackground)
        .statusBarsPadding()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black)) {

            DirectVideoPlayer(
                videoUrl = video.mp4Url,
                isShort = false,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 8.dp, start = 8.dp)
                    .background(Color.Black.copy(0.5f), CircleShape)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    "Close",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
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
                    Text("AUM: ${video.aum}", color = BricxTextSecondary, fontSize = 12.sp)
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = BricxTextPrimary),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Subscribe", color = BricxBackground, fontWeight = FontWeight.Bold)
                }
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
        }
    }
}

// ---------------- REST OF UI CONTROLS ----------------

@Composable
fun DiscoverSearchScreen(
    state: DiscoverState,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onFlashClick: (String) -> Unit,
    onVideoClick: (String) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(BricxBackground)
        .statusBarsPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    "Back",
                    tint = BricxTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        "Search videos...",
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
                Text(
                    "${video.authorName} • ${video.views} • ${video.postedTime}",
                    color = BricxTextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}