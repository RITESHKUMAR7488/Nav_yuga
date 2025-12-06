package com.example.navyuga

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NavyugaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize things here if needed later (e.g. Analytics, Crashlytics)
    }
}