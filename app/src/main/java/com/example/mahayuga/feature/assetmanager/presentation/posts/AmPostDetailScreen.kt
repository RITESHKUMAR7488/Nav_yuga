package com.example.mahayuga.feature.assetmanager.presentation.posts

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.assetmanager.domain.model.AmPost

// Bricx Asset Manager Theme Colors
private val AmBackground = Color(0xFF061123)
private val AmSurface = Color(0xFF111c30)
private val AmAccent = Color(0xFF38a882)

@Composable
fun AmPostDetailScreen(
    viewModel: AmPostsViewModel,
    initialPostId: String,
    onNavigateBack: () -> Unit
) {
    val postsState by viewModel.postsState.collectAsState()
    val listState = rememberLazyListState()

    // Scroll directly to the tapped post
    LaunchedEffect(postsState) {
        if (postsState is UiState.Success) {
            val index = (postsState as UiState.Success).data.indexOfFirst { it.id == initialPostId }
            if (index >= 0) {
                listState.scrollToItem(index)
            }
        }
    }

    // Removed the Scaffold. Using a Column for edge-to-edge dark theme control.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmBackground)
    ) {
        // Custom Header matching the dark aesthetic
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Manager Content",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        }

        // Main Feed
        when (val state = postsState) {
            is UiState.Idle -> {}
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AmAccent)
                }
            }

            is UiState.Failure -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = Color.Red)
                }
            }

            is UiState.Success -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(24.dp), // More breathing room between posts
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(state.data, key = { it.id }) { post ->
                        FeedPostItem(
                            post = post,
                            onLikeClick = { viewModel.toggleLike(post.id) },
                            onCommentClick = { /* Handle Comments */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeedPostItem(
    post: AmPost,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    val heartColor by animateColorAsState(
        targetValue = if (post.isLikedByMe) Color.Red else Color.White,
        animationSpec = spring(),
        label = "heartColor"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AmSurface), // Dark surface for the card
        shape = RoundedCornerShape(0.dp) // Instagram edge-to-edge style
    ) {
        Column {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Picture Placeholder
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AmAccent)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = AmBackground,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Asset Manager",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                }
            }

            // Video/Image Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 5f)
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = post.thumbnailUrl,
                    contentDescription = "Post Content",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Play Button Overlay
                Icon(
                    imageVector = Icons.Default.PlayCircleOutline,
                    contentDescription = "Play",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center)
                )
            }

            // Engagement Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = if (post.isLikedByMe) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = heartColor
                    )
                }
                IconButton(onClick = onCommentClick) {
                    Icon(
                        Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Outlined.Send, contentDescription = "Share", tint = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.BookmarkBorder,
                        contentDescription = "Save",
                        tint = Color.White
                    )
                }
            }

            // Metadata & Description
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text(
                    text = "${post.likesCount} likes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))

                var isExpanded by remember { mutableStateOf(false) }

                Text(
                    text = post.description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { isExpanded = !isExpanded }
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "View all ${post.commentsCount} comments",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onCommentClick() }
                )
            }
        }
    }
}