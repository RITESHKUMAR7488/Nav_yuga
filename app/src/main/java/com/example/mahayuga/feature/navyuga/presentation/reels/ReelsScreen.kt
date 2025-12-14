package com.example.mahayuga.feature.navyuga.presentation.reels

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReelsScreen() {
    // State to track the selected tab: 0 -> Reel, 1 -> Education, 2 -> Movies
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Color.Black, // Instagram-like dark background
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = "Discover",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // ⚡ Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReelTypeButton(
                        text = "Reels",
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    ReelTypeButton(
                        text = "Education",
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                    ReelTypeButton(
                        text = "Movies",
                        isSelected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { innerPadding ->
        // ⚡ Empty Content Area (Blank)
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Content is intentionally left blank as requested.
            // You can add content switching here later (e.g., when selectedTab == 0).
        }
    }
}

@Composable
fun ReelTypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ⚡ Custom Shape: Rectangular with small Corner Radius (8.dp)
    val buttonShape = RoundedCornerShape(8.dp)

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF2979FF) else Color.Transparent, // Brand Blue or Transparent
        label = "bgColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Gray,
        label = "textColor"
    )
    val borderColor = if (isSelected) Color.Transparent else Color.Gray.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .height(40.dp)
            .clip(buttonShape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = buttonShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}