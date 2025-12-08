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

        // ⚡ FIX: Extract values here to prevent compiler crash inside setContent
        val initialDarkMode = preferenceManager.isDarkMode
        val initialLoggedIn = preferenceManager.isLoggedIn

        setContent {
            // 1. Read Theme Preference using the extracted value as default
            var isDarkTheme by remember {
                mutableStateOf(initialDarkMode)
            }

            // 2. Determine Start Destination
            val startDestination = if (initialLoggedIn) {
                "super_app_hub"
            } else {
                "login"
            }

            NavyugaTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    startDestination = startDestination,
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = {
                        val newMode = !isDarkTheme
                        isDarkTheme = newMode
                        // It is safe to call the method inside the callback
                        preferenceManager.saveThemeMode(newMode)
                    }
                )
            }
        }
    }
}