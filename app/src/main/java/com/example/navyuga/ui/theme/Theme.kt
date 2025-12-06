package com.example.navyuga.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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

// ⚡ Added a Light Scheme for the toggle to work
private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    secondary = CyanAccent,
    background = Color(0xFFF4F4F4),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    outline = Color(0xFFE0E0E0)
)

@Composable
fun NavyugaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // ⚡ Now accepts a parameter
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set Status bar color to match background
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            // ⚡ Fix Icon Colors: If theme is Light, icons should be Dark (and vice versa)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}