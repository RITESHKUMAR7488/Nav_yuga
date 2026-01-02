package com.example.mahayuga

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.fragment.app.FragmentActivity
import com.example.mahayuga.core.data.local.PreferenceManager
import com.example.mahayuga.navigation.AppNavigation
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
        // ⚡ NEW: Check role for routing
        val userRole = preferenceManager.userRole

        setContent {
            var isDarkTheme by remember { mutableStateOf(initialDarkMode) }

            // ⚡ REQUEST 5: Direct routing based on Role
            val startDestination = if (initialLoggedIn) {
                if (userRole == "admin") "admin_dashboard" else "super_app_hub"
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