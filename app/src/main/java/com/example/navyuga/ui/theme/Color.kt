package com.example.navyuga.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Base Colors
val MidnightBg = Color(0xFF0B0E14)        // Deepest Background
val MidnightCard = Color(0xFF151A23)      // Card Background
val MidnightSurface = Color(0xFF1C2331)   // Input Fields
val BorderStroke = Color(0xFF2C3B4E)      // Subtle Borders

// Accents
val BrandBlue = Color(0xFF2979FF)
val CyanAccent = Color(0xFF00E5FF)
val ErrorRed = Color(0xFFFF3B30)
val SuccessGreen = Color(0xFF00E676)

// Text
val TextWhiteHigh = Color(0xDEFFFFFF)
val TextWhiteMedium = Color(0x99FFFFFF)
val TextWhiteLow = Color(0x61FFFFFF)

// Gradients
val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))
)