package com.example.mahayuga.feature.navyuga.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class AccelPixQuoteDto(
    val tkr: String,
    val td: String,
    val op: Double,
    val hp: Double,
    val lp: Double,
    val cp: Double,
    val vol: Long,
    val oi: Long,
    val eod: Boolean
)

interface AccelPixApi {
    // Exact mapping of your GET URL structure
    @GET("api/fda/rest/{ticker}/{startDate}/{endDate}/{interval}")
    suspend fun getLiveQuote(
        @Path("ticker") ticker: String,
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String,
        @Path("interval") interval: String,
        @Query("api_token", encoded = true) apiToken: String // encoded=true prevents Retrofit from double-encoding your token
    ): AccelPixQuoteDto
}