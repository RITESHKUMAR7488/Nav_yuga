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
        // optimized for Input/Output operations (like parsing massive JSON strings).
        // Why it makes your code good: Even if parsing the Yahoo API takes time, the UI will not stutter
        // for a single frame because the heavy lifting is completely isolated on the IO dispatcher.
        return withContext(Dispatchers.IO) {
            try {
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
                        isPositive = change >= 0,

                        // MAPPING THE REAL API DATA HERE:
                        openPrice = dto.regularMarketOpen ?: 0.0,
                        previousClose = dto.regularMarketPreviousClose ?: 0.0,
                        dayHigh = dto.regularMarketDayHigh ?: 0.0,
                        dayLow = dto.regularMarketDayLow ?: 0.0,
                        fiftyTwoWeekHigh = dto.fiftyTwoWeekHigh ?: 0.0,
                        fiftyTwoWeekLow = dto.fiftyTwoWeekLow ?: 0.0,
                        marketCap = dto.marketCap ?: 0L,
                        dividendYield = dto.dividendYield ?: 0.0
                    )
                } ?: emptyList()

                Result.success(quotes)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}