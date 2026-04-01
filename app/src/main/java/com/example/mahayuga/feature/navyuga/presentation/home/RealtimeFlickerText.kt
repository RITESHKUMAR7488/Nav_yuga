package com.example.mahayuga.feature.navyuga.presentation.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay

@Composable
fun RealtimeFlickerText(
    text: String,
    currentValue: Double,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    defaultColor: Color = Color.White
) {
    var previousValue by remember { mutableDoubleStateOf(currentValue) }
    var targetColor by remember { mutableStateOf(defaultColor) }

    // Smoothly animate between the target color and default color
    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 400),
        label = "priceFlicker"
    )

    LaunchedEffect(currentValue) {
        if (currentValue > previousValue) {
            targetColor = Color(0xFF00E676) // Bright Green for Uptick
        } else if (currentValue < previousValue) {
            targetColor = Color(0xFFFF1744) // Bright Red for Downtick
        }

        previousValue = currentValue

        // Wait a moment so the user registers the flash, then return to default color
        delay(800)
        targetColor = defaultColor
    }

    Text(
        text = text,
        color = animatedColor,
        style = textStyle,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}