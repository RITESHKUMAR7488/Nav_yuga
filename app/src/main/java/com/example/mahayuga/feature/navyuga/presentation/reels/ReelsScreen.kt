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
import com.example.mahayuga.ui.theme.* // ⚡ IMPORTED BRICX THEME

@Composable
fun ReelsScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = BricxBackground, // ⚡ UPDATED
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BricxBackground) // ⚡ UPDATED
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = "Discover",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = BricxTextPrimary, // ⚡ UPDATED
                    modifier = Modifier.padding(bottom = 16.dp)
                )
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(BricxBackground) // ⚡ UPDATED
        ) {}
    }
}

@Composable
fun ReelTypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonShape = RoundedCornerShape(8.dp)

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) BricxBrandBlue else Color.Transparent, // ⚡ UPDATED
        label = "bgColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) BricxTextPrimary else BricxTextSecondary, // ⚡ UPDATED
        label = "textColor"
    )
    val borderColor = if (isSelected) Color.Transparent else BricxBorder // ⚡ UPDATED

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