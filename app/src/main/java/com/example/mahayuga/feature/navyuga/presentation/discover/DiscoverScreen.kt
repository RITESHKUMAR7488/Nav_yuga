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

// --- THEME COLORS ---
private val BackgroundDark = Color(0xFF080F18)
private val CardBackground = Color(0xFF0F1722)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF8B9BB4)
private val AccentTeal = Color(0xFF00BFA5)
private val StoryGradientStart = Color(0xFFE1306C)
private val StoryGradientEnd = Color(0xFFF77737)
private val BricxStoryColor = Color(0xFF2979FF)

@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Overlay States for Details
    var selectedReel by remember { mutableStateOf<DiscoverReel?>(null) }
    var selectedEducation by remember { mutableStateOf<DiscoverEducation?>(null) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(BackgroundDark)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- HEADER ---
            Column(
                modifier = Modifier
                    .background(BackgroundDark)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Discover Icon",
                            tint = AccentTeal,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Discover",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DiscoverHeaderIcon(Icons.Default.Search, "Search") {
                            isSearchActive = !isSearchActive
                        }
                        DiscoverHeaderIcon(Icons.Default.Send, "Messages") {
                            Toast.makeText(context, "Messages coming soon", Toast.LENGTH_SHORT)
                                .show()
                        }
                        DiscoverHeaderIcon(Icons.Outlined.Notifications, "Notifications") {
                            Toast.makeText(context, "No new notifications", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

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
                                color = Color.Gray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentTeal,
                            unfocusedBorderColor = CardBackground,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
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
                        CircularProgressIndicator(color = AccentTeal)
                    }
                }

                is DiscoverState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = Color.Red)
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
                                    .background(CardBackground)
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
                                        color = AccentTeal,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Invest in Grade-A\nWarehousing",
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
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
                                            color = BackgroundDark
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
                                color = TextPrimary,
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
                                color = TextPrimary,
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
fun DiscoverHeaderIcon(icon: ImageVector, desc: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .shadow(elevation = 6.dp, shape = CircleShape, spotColor = Color.Black)
            .clip(CircleShape)
            .background(CardBackground)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = desc,
            tint = TextPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}

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
                            BricxStoryColor,
                            AccentTeal,
                            BricxStoryColor
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
                .background(CardBackground),
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
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)))
                Text("BRICX", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = story.name,
            color = if (story.isBricx) AccentTeal else TextPrimary,
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
        colors = CardDefaults.cardColors(containerColor = CardBackground)
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
                        color = AccentTeal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = " • AUM: ${reel.aum}",
                        color = TextSecondary,
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
                .background(CardBackground)
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
                    .background(CardBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(video.assetManager.take(1), color = AccentTeal, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = video.title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        video.assetManager,
                        color = AccentTeal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(" • AUM: ${video.aum}", color = TextSecondary, fontSize = 12.sp)
                    Text(" • ${video.views}", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}

// --- OVERLAY SCREENS ---

@Composable
fun FullScreenReelView(reel: DiscoverReel, onClose: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        // Pseudo Video Player Background
        AsyncImage(
            model = reel.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)))

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
                        .background(AccentTeal, CircleShape),
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
    Column(modifier = Modifier
        .fillMaxSize()
        .background(BackgroundDark)
        .statusBarsPadding()) {
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
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Text(video.title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("${video.views} • 2 days ago", color = TextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // AM Info Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(CardBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        video.assetManager.take(1),
                        color = AccentTeal,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        video.assetManager,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text("AUM: ${video.aum}", color = TextSecondary, fontSize = 12.sp)
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = TextPrimary),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Subscribe", color = BackgroundDark, fontWeight = FontWeight.Bold)
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
            HorizontalDivider(color = CardBackground)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Suggested Videos",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Dummy Suggested List
            repeat(3) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)) {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardBackground)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Understanding fractional real estate laws",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Navyuga Assets", color = TextSecondary, fontSize = 12.sp)
                        Text("34K views • 1 week ago", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun EduActionBtn(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = TextPrimary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = TextPrimary, fontSize = 12.sp)
    }
}