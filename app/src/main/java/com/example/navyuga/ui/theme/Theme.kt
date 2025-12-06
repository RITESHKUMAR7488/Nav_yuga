package com.example.navyuga.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ⚡ Define our Dark Scheme
private val DarkColorScheme = darkColorScheme(
    primary = BrandBlue,
    secondary = CyanAccent,
    background = MidnightBg,
    surface = MidnightCard,
    onPrimary = Color.White,
    onBackground = TextWhiteHigh,
    onSurface = TextWhiteHigh,
    outline = BorderStroke
)

@Composable
fun NavyugaTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            // ⚡ Force Status Bar to be Transparent/Midnight
            val window = (view.context as Activity).window
            window.statusBarColor = MidnightBg.toArgb()
            window.navigationBarColor = MidnightBg.toArgb()

            // Make icons light (since background is dark)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Default Material 3 Typography
        content = content
    )
}