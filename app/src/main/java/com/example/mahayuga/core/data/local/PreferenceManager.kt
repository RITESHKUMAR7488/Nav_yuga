package com.example.mahayuga.core.data.local

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    private val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("NavyugaPrefs", Context.MODE_PRIVATE)

    fun saveLoginState(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    val isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)

    // Theme: true = Dark, false = Light, null = System Default
    fun saveThemeMode(isDark: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_DARK_MODE, isDark).apply()
    }

    // Default to true (Dark) if not set, or you can make it follow system
    val isDarkMode: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_DARK_MODE, true)

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_DARK_MODE = "is_dark_mode"
    }
}