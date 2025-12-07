package com.example.navyuga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.navyuga.core.data.local.PreferenceManager
import com.example.navyuga.navigation.AppNavigation
import com.example.navyuga.ui.theme.NavyugaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // 1. Read Theme Preference
            var isDarkTheme by remember {
                mutableStateOf(preferenceManager.isDarkMode)
            }

            // 2. Determine Start Destination
            val startDestination = if (preferenceManager.isLoggedIn) {
                "super_app_hub"
            } else {
                "login"
            }

            NavyugaTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    startDestination = startDestination,
                    isDarkTheme = isDarkTheme, // Pass state down
                    onThemeToggle = {
                        val newMode = !isDarkTheme
                        isDarkTheme = newMode
                        preferenceManager.saveThemeMode(newMode)
                    }
                )
            }
        }
    }
}