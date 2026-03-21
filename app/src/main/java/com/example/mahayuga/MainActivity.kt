package com.example.mahayuga

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.fragment.app.FragmentActivity
import com.example.mahayuga.core.data.local.PreferenceManager
import com.example.mahayuga.navigation.AppNavigation
import com.example.mahayuga.navigation.AssetManagerDestinations
import com.example.mahayuga.ui.theme.NavyugaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val initialDarkMode = preferenceManager.isDarkMode
        val initialLoggedIn = preferenceManager.isLoggedIn
        val userRole = preferenceManager.userRole
        val lastMode = preferenceManager.lastActiveMode // ⚡ FETCH LAST ACTIVE MODE

        setContent {
            var isDarkTheme by remember { mutableStateOf(initialDarkMode) }

            // ⚡ SEAMLESS STATE ROUTING LOGIC
            val startDestination = if (initialLoggedIn) {
                when (lastMode) {
                    "ADMIN" -> "admin_dashboard"
                    "AM_WORK" -> AssetManagerDestinations.DASHBOARD
                    "INVESTOR" -> "navyuga_splash" // ⚡ FIX: Route to Splash first instead of dashboard
                    else -> {
                        // Fallback if lastMode is missing (legacy support)
                        when (userRole) {
                            "admin" -> "admin_dashboard"
                            "asset_manager" -> AssetManagerDestinations.DASHBOARD
                            else -> "navyuga_splash" // Investors go to Splash -> Dashboard
                        }
                    }
                }
            } else {
                "welcome"
            }

            NavyugaTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    startDestination = startDestination,
                    isDarkTheme = isDarkTheme,
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