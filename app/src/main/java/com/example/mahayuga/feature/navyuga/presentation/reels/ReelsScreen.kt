// main/java/com/example/mahayuga/feature/navyuga/presentation/reels/ReelsScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.reels

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mahayuga.core.common.BricxHubTopAppBar
import com.example.mahayuga.ui.theme.*

@Composable
fun ReelsScreen(
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    Scaffold(
        containerColor = BricxBackground,
        topBar = {
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(BricxBackground)) {
                BricxHubTopAppBar(
                    title = "Discover",
                    icon = Icons.Outlined.Explore,
                    onSearchClick = {
                        Toast.makeText(context, "Search Discover", Toast.LENGTH_SHORT).show()
                    },
                    onNotificationClick = onNavigateToNotifications,
                    onMessageClick = onNavigateToMessages
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
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
                .background(BricxBackground)
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
        targetValue = if (isSelected) BricxBrandBlue else Color.Transparent,
        label = "bgColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) BricxTextPrimary else BricxTextSecondary,
        label = "textColor"
    )
    val borderColor = if (isSelected) Color.Transparent else BricxBorder

    Box(
        modifier = modifier
            .height(40.dp)
            .clip(buttonShape)
            .background(backgroundColor)
            .border(width = 1.dp, color = borderColor, shape = buttonShape)
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