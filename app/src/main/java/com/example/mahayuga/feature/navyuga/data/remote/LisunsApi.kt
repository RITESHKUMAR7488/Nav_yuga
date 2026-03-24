package com.example.mahayuga.feature.navyuga.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

// REST API for Fundamental / Historical Data
interface LisunsApi {
    @GET("GetScripMCap")
    suspend fun getHistoricalData(
        @Query("accessKey") accessKey: String = "d7b5bf2f-a635-4f01-bb6d-3cc6e3e3984f",
        @Query("exchange") exchange: String = "BSE",
        @Query("instrumentIdentifier") instrumentIdentifier: String,
        @Query("format") format: String = "json",
        @Query("from") from: Long,
        @Query("to") to: Long,
        @Query("dtformat") dtformat: String = "string"
    ): LisunsRestResponse
}

data class LisunsRestResponse(
    val Value: List<LisunsHistoricalQuote>?
)

data class LisunsHistoricalQuote(
    val Close: Double?,
    val High: Double?,
    val Low: Double?,
    val Open: Double?,
    val Volume: Long?,
    val MCap: Long?
)