package com.example.mahayuga.ui.theme

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

// 1. Dark Scheme
private val DarkColorScheme = darkColorScheme(
    primary = BrandBlue,
    secondary = CyanAccent,
    background = MidnightBg,
    surface = MidnightCard,
    surfaceVariant = MidnightSurface, // Use for Input Fields
    onPrimary = Color.White,
    onBackground = TextWhiteHigh,
    onSurface = TextWhiteHigh,
    onSurfaceVariant = TextWhiteMedium,
    outline = BorderStroke
)

// 2. Light Scheme
private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    secondary = DeepBlueAccent,
    background = LightBg,
    surface = LightCard,
    surfaceVariant = LightSurface,  // Use for Input Fields
    onPrimary = Color.White,
    onBackground = TextBlackHigh,
    onSurface = TextBlackHigh,
    onSurfaceVariant = TextBlackMedium,
    outline = LightBorderStroke
)

@Composable
fun NavyugaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            // Status Bar Icons: Dark Mode -> Light Icons; Light Mode -> Dark Icons
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