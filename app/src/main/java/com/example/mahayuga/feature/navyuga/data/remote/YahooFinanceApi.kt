// main/java/com/example/mahayuga/feature/navyuga/data/remote/YahooFinanceApi.kt
package com.example.mahayuga.feature.navyuga.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface YahooFinanceApi {
    // ⚡ COROUTINE USAGE HERE ⚡
    // The 'suspend' keyword tells Kotlin this function will execute inside a Coroutine.
    // Why it makes your code good: Retrofit natively supports 'suspend', allowing it
    // to perform the network call off the main thread automatically without complex callbacks,
    // ensuring your app never drops frames while waiting for the network.
    @GET("market/v2/get-quotes")
    suspend fun getMarketQuotes(
        @Query("region") region: String = "IN",
        @Query("symbols") symbols: String
    ): QuoteResponseDto
}

data class QuoteResponseDto(val quoteResponse: QuoteResultDto?)
data class QuoteResultDto(val result: List<QuoteDto>?)

data class QuoteDto(
    val symbol: String,
    val shortName: String?,
    val regularMarketPrice: Double?,
    val regularMarketChange: Double?,
    val regularMarketChangePercent: Double?,
    // REAL DATA FIELDS ADDED HERE:
    val regularMarketOpen: Double?,
    val regularMarketPreviousClose: Double?,
    val regularMarketDayHigh: Double?,
    val regularMarketDayLow: Double?,
    val fiftyTwoWeekHigh: Double?,
    val fiftyTwoWeekLow: Double?,
    val marketCap: Long?,
    val dividendYield: Double?
)