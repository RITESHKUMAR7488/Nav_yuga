package com.example.mahayuga.feature.navyuga.presentation.home

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.auth.presentation.components.formatIndian

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
    val supportNumber by viewModel.supportNumber.collectAsState()
    val context = LocalContext.current

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
                // ⚡ REMOVED STORIES SECTION HERE

                // Filter Buttons
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

                // Property Feed
                items(uiState.properties, key = { it.id }) { property ->
                    InstagramStylePropertyCard(
                        property = property,
                        onItemClick = { onNavigateToDetail(property.id) },
                        onLikeClick = { viewModel.toggleLike(property.id, property.isLiked) },
                        onShareClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Check out this property: ${property.title} in ${property.city}. Expected ROI: ${property.roi}%"
                                )
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        },
                        onInvestClick = {
                            try {
                                val message =
                                    "Hello, I am interested in investing in *${property.title}*."
                                val url =
                                    "https://api.whatsapp.com/send?phone=$supportNumber&text=${
                                        Uri.encode(message)
                                    }"
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse(url)
                                    setPackage("com.whatsapp")
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "WhatsApp not found", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }

                item { Spacer(Modifier.height(100.dp)) }
            }
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
            // Header
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

            // Image
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(320.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = property.mainImage,
                    contentDescription = "Property Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Stats Row
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isExited) {
                    PropertyStat("Entry", "₹${formatIndian(property.totalValuation)}")
                    VerticalBar()
                    PropertyStat("Exit", "₹${formatIndian(property.exitPrice)}")
                    VerticalBar()
                    PropertyStat("Profit", "₹${formatIndian(property.totalProfit)}", true)
                } else {
                    PropertyStat("Price", "₹${formatIndian(property.totalValuation)}")
                    VerticalBar()
                    val rentToShow =
                        if (property.monthlyRent.isNotEmpty()) property.monthlyRent else "0"
                    PropertyStat("Rent", "₹${formatIndian(rentToShow)}")
                    VerticalBar()
                    PropertyStat("ROI", "${property.roi}%", true)
                }
            }

            // ⚡ FIX: "Own in min investment" Text Moved HERE (Middle)
            if (property.status == "Funding") {
                Text(
                    text = "Now you can own this property in min investment ₹${formatIndian(property.minInvest)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = FabColor, // Brand Blue
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .padding(bottom = 8.dp)
                )
            }

            HorizontalDivider(color = Color.White.copy(0.1f))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LIKE
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

                // INVEST
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

                // SHARE
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

// Helpers
@Composable
fun VerticalBar() {
    Box(Modifier
        .width(1.dp)
        .height(32.dp)
        .background(Color.Gray.copy(0.2f)))
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
        ); IconButton(
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
    }; IconButton(
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

@Composable
fun PropertyStat(label: String, value: String, isHighlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        ); Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    }
}