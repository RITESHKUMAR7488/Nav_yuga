// main/java/com/example/mahayuga/feature/navyuga/domain/repository/MarketRepository.kt
package com.example.mahayuga.feature.navyuga.domain.repository

import com.example.mahayuga.feature.navyuga.domain.model.MarketQuote

interface MarketRepository {
    suspend fun getLiveQuotes(symbols: List<String>): Result<List<MarketQuote>>
}