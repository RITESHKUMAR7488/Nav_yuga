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
    val volume: Long = 0L,
    val isDummy: Boolean = false,
    val averageTradedPrice: Double = 0.0,
    val buyPrice: Double = 0.0,
    val buyQty: Long = 0L,
    val sellPrice: Double = 0.0,
    val sellQty: Long = 0L,
    val lastTradeQty: Long = 0L,
    val openInterest: Long = 0L,
    val quotationLot: Double = 0.0,
    val tradedValue: Double = 0.0
)