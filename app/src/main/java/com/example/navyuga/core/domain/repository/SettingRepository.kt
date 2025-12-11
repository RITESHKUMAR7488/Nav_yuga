package com.example.navyuga.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getWhatsAppNumber(): Flow<String>
}