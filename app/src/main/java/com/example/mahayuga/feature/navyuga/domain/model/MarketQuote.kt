package com.example.mahayuga.feature.navyuga.domain.model

data class MarketQuote(
    val symbol: String,
    val name: String,
    val currentPrice: Double,
    val priceChange: Double,
    val percentageChange: Double,
    val isPositive: Boolean,

    // REAL DATA FIELDS:
    val openPrice: Double = 0.0,
    val previousClose: Double = 0.0,
    val dayHigh: Double = 0.0,
    val dayLow: Double = 0.0,
    val fiftyTwoWeekHigh: Double = 0.0,
    val fiftyTwoWeekLow: Double = 0.0,
    val marketCap: Long = 0L,
    val dividendYield: Double = 0.0,

    // NEW LISUNS DATA:
    val volume: Long = 0L
)