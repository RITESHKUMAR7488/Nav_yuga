package com.example.navyuga.feature.arthyuga.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.arthyuga.domain.model.PropertyModel
import com.example.navyuga.feature.arthyuga.domain.model.TenantStory
import com.example.navyuga.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val stories by viewModel.stories.collectAsState()
    val propertiesState by viewModel.properties.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBg)
    ) {
        // 1. Stories Section
        Text(
            text = "Trending Tenants",
            style = MaterialTheme.typography.titleLarge,
            color = TextWhiteHigh,
            modifier = Modifier.padding(16.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(stories) { story ->
                StoryItem(story)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Tabs (Available / Funded / Exited)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(MidnightSurface, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            listOf("Available", "Funded", "Exited").forEach { tab ->
                val isSelected = tab == selectedTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) BrandBlue else Color.Transparent)
                        .clickable { viewModel.selectTab(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) Color.White else TextWhiteMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Properties List
        when (val state = propertiesState) {
            is UiState.Success -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.data) { property ->
                        PropertyCard(property)
                    }
                }
            }
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyanAccent)
                }
            }
            else -> {}
        }
    }
}

@Composable
fun StoryItem(story: TenantStory) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .border(2.dp, CyanAccent, CircleShape)
                .padding(4.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            AsyncImage(
                model = story.logoUrl,
                contentDescription = story.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = story.name, style = MaterialTheme.typography.labelSmall, color = TextWhiteHigh)
    }
}

@Composable
fun PropertyCard(property: PropertyModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MidnightCard),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Placeholder Image (In real app, this would be property.imageUrl)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.Gray) // Placeholder color
            ) {
                // Overlay for ROI
                Surface(
                    color = MidnightSurface.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "${property.roi}% Yield",
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhiteHigh,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = property.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextWhiteMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { property.fundedPercent / 100f },
                        modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = BrandBlue,
                        trackColor = MidnightSurface,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${property.fundedPercent}%",
                        color = CyanAccent,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Min: ${property.minInvest}", color = TextWhiteHigh, fontWeight = FontWeight.Bold)
                    Text("Invest >", color = BrandBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    NavyugaTheme {
        HomeScreen()
    }
}