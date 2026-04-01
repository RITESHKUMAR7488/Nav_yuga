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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.mahayuga.core.common.BricxHubTopAppBar // ⚡ IMPORTED COMMON COMPONENT
import com.example.mahayuga.ui.theme.* // ⚡ IMPORTED BRICX THEME

// --- STORY SPECIFIC COLORS (Kept local as they are specific to the Instagram ring effect) ---
private val StoryGradientStart = Color(0xFFE1306C)
private val StoryGradientEnd = Color(0xFFF77737)

@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel = hiltViewModel(),
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Overlay States for Details
    var selectedReel by remember { mutableStateOf<DiscoverReel?>(null) }
    var selectedEducation by remember { mutableStateOf<DiscoverEducation?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BricxBackground) // ⚡ UPDATED
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- HEADER ---
            Column(
                modifier = Modifier
                    .background(BricxBackground) // ⚡ UPDATED
                    .statusBarsPadding()
            ) {
                // ⚡ REPLACED CUSTOM HEADER WITH BRICXHUBTOPAPPBAR
                BricxHubTopAppBar(
                    title = "Discover",
                    icon = Icons.Default.Search,
                    onSearchClick = { isSearchActive = !isSearchActive }, // Toggles inline search
                    onNotificationClick = onNavigateToNotifications,
                    onMessageClick = onNavigateToMessages
                )

                HorizontalDivider(color = BricxSurfaceCard.copy(alpha = 0.5f)) // ⚡ UPDATED

                if (isSearchActive) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = {
                            Text(
                                "Search reels, and education...",
                                color = BricxTextSecondary // ⚡ UPDATED
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BricxBrandTeal, // ⚡ UPDATED
                            unfocusedBorderColor = BricxSurfaceCard, // ⚡ UPDATED
                            focusedTextColor = BricxTextPrimary, // ⚡ UPDATED
                            unfocusedTextColor = BricxTextPrimary // ⚡ UPDATED
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // --- MAIN UNIFIED SCROLL CONTENT ---
            when (val state = uiState) {
                is DiscoverState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BricxBrandTeal) // ⚡ UPDATED
                    }
                }

                is DiscoverState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = BricxDangerRed) // ⚡ UPDATED
                    }
                }

                is DiscoverState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        // 1. STORIES
                        item {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.stories) { story ->
                                    StoryCircleItem(story = story)
                                }
                            }
                        }

                        // 2. ADVERTISING BOARD
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BricxSurfaceCard) // ⚡ UPDATED
                            ) {
                                AsyncImage(
                                    model = state.adBoard.imageUrl,
                                    contentDescription = "Advertisement",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    Color.Black.copy(alpha = 0.8f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        "Sponsored",
                                        color = BricxBrandTeal, // ⚡ UPDATED
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Invest in Grade-A\nWarehousing",
                                        color = BricxTextPrimary, // ⚡ UPDATED
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { },
                                        colors = ButtonDefaults.buttonColors(containerColor = BricxBrandTeal), // ⚡ UPDATED
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(
                                            horizontal = 12.dp,
                                            vertical = 0.dp
                                        ),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text(
                                            "Learn More",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BricxBackground // ⚡ UPDATED
                                        )
                                    }
                                }
                            }
                        }

                        // 3. REELS SECTION (4 Grid)
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Trending Reels",
                                color = BricxTextPrimary, // ⚡ UPDATED
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 12.dp
                                )
                            )

                            // Split 4 reels into 2 rows for grid layout
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    ReelGridCard(
                                        state.reels.getOrNull(0),
                                        Modifier.weight(1f)
                                    ) { reel -> selectedReel = reel }
                                    ReelGridCard(
                                        state.reels.getOrNull(1),
                                        Modifier.weight(1f)
                                    ) { reel -> selectedReel = reel }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    ReelGridCard(
                                        state.reels.getOrNull(2),
                                        Modifier.weight(1f)
                                    ) { reel -> selectedReel = reel }
                                    ReelGridCard(
                                        state.reels.getOrNull(3),
                                        Modifier.weight(1f)
                                    ) { reel -> selectedReel = reel }
                                }
                            }
                        }

                        // 4. EDUCATION SECTION
                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                "Learn & Grow",
                                color = BricxTextPrimary, // ⚡ UPDATED
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 12.dp
                                )
                            )
                        }

                        items(state.educationVideos) { video ->
                            EducationVideoCard(
                                video = video,
                                onClick = { selectedEducation = video })
                        }
                    }
                }
            }
        }

        // --- FULL SCREEN OVERLAYS ---

        if (selectedReel != null) {
            Dialog(
                onDismissRequest = { selectedReel = null },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = false
                )
            ) {
                FullScreenReelView(reel = selectedReel!!, onClose = { selectedReel = null })
            }
        }

        if (selectedEducation != null) {
            Dialog(
                onDismissRequest = { selectedEducation = null },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = false
                )
            ) {
                YouTubeStyleVideoDetail(
                    video = selectedEducation!!,
                    onClose = { selectedEducation = null })
            }
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun StoryCircleItem(story: DiscoverStory) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(
                    if (story.isBricx) Brush.sweepGradient(
                        listOf(
                            BricxBrandBlue, // ⚡ UPDATED
                            BricxBrandTeal, // ⚡ UPDATED
                            BricxBrandBlue  // ⚡ UPDATED
                        )
                    )
                    else if (!story.isSeen) Brush.sweepGradient(
                        listOf(
                            StoryGradientStart,
                            StoryGradientEnd,
                            StoryGradientStart
                        )
                    )
                    else Brush.sweepGradient(listOf(Color.Gray, Color.Gray))
                )
                .padding(3.dp)
                .clip(CircleShape)
                .background(BricxSurfaceCard), // ⚡ UPDATED
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = story.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
            // BRICX special badge
            if (story.isBricx) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
                Text("BRICX", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = story.name,
            color = if (story.isBricx) BricxBrandTeal else BricxTextPrimary, // ⚡ UPDATED
            fontSize = 12.sp,
            fontWeight = if (story.isBricx) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun ReelGridCard(reel: DiscoverReel?, modifier: Modifier, onClick: (DiscoverReel) -> Unit) {
    if (reel == null) {
        Box(modifier = modifier.aspectRatio(0.6f))
        return
    }

    Card(
        modifier = modifier
            .aspectRatio(0.6f)
            .clickable { onClick(reel) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard) // ⚡ UPDATED
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = reel.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Top Gradient for Views
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
            )
            // Bottom Gradient for Info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.9f)
                            )
                        )
                    )
            )

            // Views (Top Right)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    reel.views,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Info (Bottom)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    reel.subtitle,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = reel.assetManager,
                        color = BricxBrandTeal, // ⚡ UPDATED
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = " • AUM: ${reel.aum}",
                        color = BricxTextSecondary, // ⚡ UPDATED
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun EducationVideoCard(video: DiscoverEducation, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
                .background(BricxSurfaceCard) // ⚡ UPDATED
        ) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Duration badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    video.duration,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BricxSurfaceCard), // ⚡ UPDATED
                contentAlignment = Alignment.Center
            ) {
                Text(
                    video.assetManager.take(1),
                    color = BricxBrandTeal,
                    fontWeight = FontWeight.Bold
                ) // ⚡ UPDATED
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = video.title,
                    color = BricxTextPrimary, // ⚡ UPDATED
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        video.assetManager,
                        color = BricxBrandTeal, // ⚡ UPDATED
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        " • AUM: ${video.aum}",
                        color = BricxTextSecondary,
                        fontSize = 12.sp
                    ) // ⚡ UPDATED
                    Text(
                        " • ${video.views}",
                        color = BricxTextSecondary,
                        fontSize = 12.sp
                    ) // ⚡ UPDATED
                }
            }
        }
    }
}

// --- OVERLAY SCREENS ---

@Composable
fun FullScreenReelView(reel: DiscoverReel, onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Pseudo Video Player Background
        AsyncImage(
            model = reel.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        // Close Button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 16.dp)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                "Close",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        // Action Menu (Right)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 16.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ReelActionIcon(Icons.Default.FavoriteBorder, "12K")
            ReelActionIcon(Icons.Default.Email, "450")
            ReelActionIcon(Icons.AutoMirrored.Filled.Send, "Share")
            ReelActionIcon(Icons.Default.MoreVert, null)
        }

        // Bottom Info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = 16.dp, bottom = 40.dp, end = 80.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(BricxBrandTeal, CircleShape), // ⚡ UPDATED
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        reel.assetManager.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    reel.assetManager,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.White, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Follow",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(reel.subtitle, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "AUM: ${reel.aum} • ${reel.views} Views",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun ReelActionIcon(icon: ImageVector, label: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(32.dp))
        if (label != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun YouTubeStyleVideoDetail(video: DiscoverEducation, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BricxBackground) // ⚡ UPDATED
            .statusBarsPadding()
    ) {
        // Video Player Placeholder
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
                    Icons.Default.PlayArrow,
                    "Play",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            // Close Button over video
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

        // Details Column
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
            ) // ⚡ UPDATED
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "${video.views} • 2 days ago",
                color = BricxTextSecondary,
                fontSize = 12.sp
            ) // ⚡ UPDATED

            Spacer(modifier = Modifier.height(16.dp))

            // AM Info Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(BricxSurfaceCard, CircleShape), // ⚡ UPDATED
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        video.assetManager.take(1),
                        color = BricxBrandTeal, // ⚡ UPDATED
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        video.assetManager,
                        color = BricxTextPrimary, // ⚡ UPDATED
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        "AUM: ${video.aum}",
                        color = BricxTextSecondary,
                        fontSize = 12.sp
                    ) // ⚡ UPDATED
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = BricxTextPrimary), // ⚡ UPDATED
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        "Subscribe",
                        color = BricxBackground,
                        fontWeight = FontWeight.Bold
                    ) // ⚡ UPDATED
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Row
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
            HorizontalDivider(color = BricxSurfaceCard) // ⚡ UPDATED
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Suggested Videos",
                color = BricxTextPrimary, // ⚡ UPDATED
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Dummy Suggested List
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
                            .background(BricxSurfaceCard) // ⚡ UPDATED
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Understanding fractional real estate laws",
                            color = BricxTextPrimary, // ⚡ UPDATED
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
                        ) // ⚡ UPDATED
                        Text(
                            "34K views • 1 week ago",
                            color = BricxTextSecondary,
                            fontSize = 12.sp
                        ) // ⚡ UPDATED
                    }
                }
            }
        }
    }
}

@Composable
fun EduActionBtn(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = BricxTextPrimary, modifier = Modifier.size(24.dp)) // ⚡ UPDATED
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = BricxTextPrimary, fontSize = 12.sp) // ⚡ UPDATED
    }
}