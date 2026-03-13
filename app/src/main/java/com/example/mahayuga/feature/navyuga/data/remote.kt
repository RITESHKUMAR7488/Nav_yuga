// main/java/com/example/mahayuga/feature/navyuga/data/remote/YahooFinanceApi.kt
package com.example.mahayuga.feature.navyuga.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface YahooFinanceApi {
    // ⚡ COROUTINE USAGE HERE ⚡
    // The 'suspend' keyword tells Kotlin this function will execute inside a Coroutine.
    // It makes the code good because Retrofit natively supports 'suspend', allowing it
    // to perform the network call off the main thread automatically without complex callbacks.
    @GET("market/v2/get-quotes")
    suspend fun getMarketQuotes(
        @Query("region") region: String = "IN",
        @Query("symbols") symbols: String
    ): QuoteResponseDto
}

// Data Transfer Objects (DTOs) to parse the Yahoo Finance JSON
data class QuoteResponseDto(val quoteResponse: QuoteResultDto?)
data class QuoteResultDto(val result: List<QuoteDto>?)
data class QuoteDto(
    val symbol: String,
    val shortName: String?,
    val regularMarketPrice: Double?,
    val regularMarketChange: Double?,
    val regularMarketChangePercent: Double?
)