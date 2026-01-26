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

    // Saves: "user", "admin", or "asset_manager"
    fun saveUserRole(role: String) {
        sharedPreferences.edit().putString(KEY_USER_ROLE, role).apply()
    }

    val userRole: String
        get() = sharedPreferences.getString(KEY_USER_ROLE, "user") ?: "user"

    // ⚡ NEW: Session Persistence for Dual-Role Users
    // Values: "ADMIN", "INVESTOR", "AM_WORK"
    fun saveLastActiveMode(mode: String) {
        sharedPreferences.edit().putString(KEY_LAST_ACTIVE_MODE, mode).apply()
    }

    val lastActiveMode: String
        get() = sharedPreferences.getString(KEY_LAST_ACTIVE_MODE, "") ?: ""

    // Track if onboarding wizard is done (useful for multi-stage processes)
    fun saveOnboardingState(isCompleted: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, isCompleted).apply()
    }

    val isOnboardingCompleted: Boolean
        get() = sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)

    // Theme: true = Dark, false = Light, null = System Default
    fun saveThemeMode(isDark: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_DARK_MODE, isDark).apply()
    }

    // Default to true (Dark) if not set
    val isDarkMode: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_DARK_MODE, true)

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_DARK_MODE = "is_dark_mode"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_LAST_ACTIVE_MODE = "last_active_mode"
    }
}