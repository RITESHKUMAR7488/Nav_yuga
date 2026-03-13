// main/java/com/example/mahayuga/feature/navyuga/data/repository/MarketRepositoryImpl.kt
package com.example.mahayuga.feature.navyuga.data.repository

import com.example.mahayuga.feature.navyuga.data.remote.YahooFinanceApi
import com.example.mahayuga.feature.navyuga.domain.model.MarketQuote
import com.example.mahayuga.feature.navyuga.domain.repository.MarketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MarketRepositoryImpl @Inject constructor(
    private val api: YahooFinanceApi
) : MarketRepository {

    override suspend fun getLiveQuotes(symbols: List<String>): Result<List<MarketQuote>> {
        // ⚡ COROUTINE USAGE HERE ⚡
        // 'withContext(Dispatchers.IO)' forcibly shifts this block of code onto a background thread pool
        // optimized for Input/Output operations (like network calls).
        // Why it makes the code good: Even if the API takes 5 seconds to respond, the UI will not stutter
        // for a single frame because the heavy lifting is completely isolated on the IO dispatcher.
        return withContext(Dispatchers.IO) {
            try {
                // Join symbols into a comma-separated string (e.g., "^NSEI,^BSESN,PSTITANIA.BO")
                val symbolString = symbols.joinToString(",")
                val response = api.getMarketQuotes(symbols = symbolString)

                val quotes = response.quoteResponse?.result?.map { dto ->
                    val price = dto.regularMarketPrice ?: 0.0
                    val change = dto.regularMarketChange ?: 0.0

                    MarketQuote(
                        symbol = dto.symbol,
                        name = dto.shortName ?: dto.symbol,
                        currentPrice = price,
                        priceChange = change,
                        percentageChange = dto.regularMarketChangePercent ?: 0.0,
                        isPositive = change >= 0
                    )
                } ?: emptyList()

                Result.success(quotes)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}