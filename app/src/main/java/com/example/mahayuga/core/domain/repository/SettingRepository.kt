package com.example.mahayuga.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getWhatsAppNumber(): Flow<String>
}