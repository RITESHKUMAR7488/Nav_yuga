package com.example.mahayuga.feature.navyuga.domain.repository

import com.example.mahayuga.feature.navyuga.domain.model.MarketQuote
import kotlinx.coroutines.flow.Flow

interface MarketRepository {
    // ⚡ Migrated to Flow for WebSocket real-time capabilities
    fun getLiveQuotesFlow(symbols: List<String>): Flow<List<MarketQuote>>

    // ⚡ REST API for Historical/M-Cap Data
    suspend fun getHistoricalData(symbol: String): Result<MarketQuote>
}