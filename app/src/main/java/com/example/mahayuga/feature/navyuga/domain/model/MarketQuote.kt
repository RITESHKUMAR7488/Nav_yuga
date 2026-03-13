// main/java/com/example/mahayuga/feature/navyuga/domain/model/MarketQuote.kt
package com.example.mahayuga.feature.navyuga.domain.model

data class MarketQuote(
    val symbol: String,
    val name: String,
    val currentPrice: Double,
    val priceChange: Double,
    val percentageChange: Double,
    val isPositive: Boolean
)