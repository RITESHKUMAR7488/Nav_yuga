package com.example.mahayuga.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// --- DARK MODE PALETTE (Midnight Blue) ---
val MidnightBg = Color(0xFF0B0E14)
val MidnightCard = Color(0xFF151A23)
val MidnightSurface = Color(0xFF1C2331)
val TextWhiteHigh = Color(0xDEFFFFFF)
val TextWhiteMedium = Color(0x99FFFFFF)
val TextWhiteLow = Color(0x61FFFFFF)

// --- LIGHT MODE PALETTE (Clean Fintech) ---
val LightBg = Color(0xFFF5F7FA)         // Very light gray/blue tint
val LightCard = Color(0xFFFFFFFF)       // Pure white cards
val LightSurface = Color(0xFFEEEFF2)    // Input fields background
val TextBlackHigh = Color(0xFF1A1C1E)   // Near black for headings
val TextBlackMedium = Color(0xFF44474E) // Dark gray for body
val TextBlackLow = Color(0xFF74777F)

// --- SHARED ACCENTS ---
val BrandBlue = Color(0xFF2979FF)       // Primary Action Color
val CyanAccent = Color(0xFF00E5FF)      // Dark Mode Highlight
val DeepBlueAccent = Color(0xFF0044CC)  // Light Mode Highlight (Darker for contrast)
val ErrorRed = Color(0xFFFF3B30)
val SuccessGreen = Color(0xFF00E676)
val BorderStroke = Color(0xFF2C3B4E)
val LightBorderStroke = Color(0xFFE0E2E5)

// Gradients
val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))
)