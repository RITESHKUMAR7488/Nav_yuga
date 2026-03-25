package com.example.mahayuga.feature.navyuga.domain.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.mahayuga.feature.navyuga.data.remote.LisunsApi
import com.example.mahayuga.feature.navyuga.data.remote.LisunsWebSocketClient
import com.example.mahayuga.feature.navyuga.domain.model.MarketQuote
import com.example.mahayuga.feature.navyuga.domain.repository.MarketRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MarketRepositoryImpl @Inject constructor(
    private val api: LisunsApi,
    private val wsClient: LisunsWebSocketClient,
    private val prefs: SharedPreferences,
    private val gson: Gson
) : MarketRepository {

    private val CACHE_KEY = "cached_market_quotes"

    private val bseScripMapper = mapOf(
        "EMBASSY" to "542602",
        "MINDSPACE" to "543217",
        "BIRET" to "543261",
        "NEXUS" to "543890",
        "PSTITANIA" to "544304",
        "PSPLATINA" to "544305"
    )

    // ⚡ SEED DATA: Used ONLY if the app is freshly installed and the network is dead.
// ⚡ SEED DATA: Flagged as isDummy = true
    private val seedData = mapOf(
        "EMBASSY" to MarketQuote(symbol = "EMBASSY", name = "Embassy REIT", currentPrice = 380.5, priceChange = -2.1, percentageChange = -0.5, isPositive = false, openPrice = 382.0, previousClose = 382.6, dayHigh = 385.0, dayLow = 379.0, fiftyTwoWeekHigh = 405.0, fiftyTwoWeekLow = 295.0, marketCap = 36000000000L, dividendYield = 5.2, volume = 120000L, isDummy = true),
        "MINDSPACE" to MarketQuote(symbol = "MINDSPACE", name = "Mindspace REIT", currentPrice = 345.2, priceChange = 1.5, percentageChange = 0.4, isPositive = true, openPrice = 343.0, previousClose = 343.7, dayHigh = 348.0, dayLow = 340.0, fiftyTwoWeekHigh = 360.0, fiftyTwoWeekLow = 270.0, marketCap = 20000000000L, dividendYield = 4.8, volume = 85000L, isDummy = true),
        "BIRET" to MarketQuote(symbol = "BIRET", name = "Brookfield REIT", currentPrice = 255.0, priceChange = 3.2, percentageChange = 1.2, isPositive = true, openPrice = 252.0, previousClose = 251.8, dayHigh = 258.0, dayLow = 250.0, fiftyTwoWeekHigh = 280.0, fiftyTwoWeekLow = 210.0, marketCap = 15000000000L, dividendYield = 6.1, volume = 60000L, isDummy = true),
        "NEXUS" to MarketQuote(symbol = "NEXUS", name = "Nexus Select", currentPrice = 135.8, priceChange = -0.5, percentageChange = -0.3, isPositive = false, openPrice = 136.0, previousClose = 136.3, dayHigh = 137.0, dayLow = 134.0, fiftyTwoWeekHigh = 145.0, fiftyTwoWeekLow = 95.0, marketCap = 10000000000L, dividendYield = 5.5, volume = 45000L, isDummy = true),
        "PSTITANIA" to MarketQuote(symbol = "PSTITANIA", name = "PropShare Titania", currentPrice = 1020.0, priceChange = 5.0, percentageChange = 0.5, isPositive = true, openPrice = 1015.0, previousClose = 1015.0, dayHigh = 1025.0, dayLow = 1010.0, fiftyTwoWeekHigh = 1050.0, fiftyTwoWeekLow = 990.0, marketCap = 500000000L, dividendYield = 7.0, volume = 5000L, isDummy = true),
        "PSPLATINA" to MarketQuote(symbol = "PSPLATINA", name = "PropShare Platina", currentPrice = 1540.0, priceChange = -10.0, percentageChange = -0.6, isPositive = false, openPrice = 1550.0, previousClose = 1550.0, dayHigh = 1560.0, dayLow = 1530.0, fiftyTwoWeekHigh = 1600.0, fiftyTwoWeekLow = 1500.0, marketCap = 800000000L, dividendYield = 6.8, volume = 3000L, isDummy = true)
    )
    private fun saveToCache(quotesMap: Map<String, MarketQuote>) {
        prefs.edit().putString(CACHE_KEY, gson.toJson(quotesMap)).apply()
    }

    private fun loadFromCache(): MutableMap<String, MarketQuote> {
        val jsonString = prefs.getString(CACHE_KEY, null) ?: return mutableMapOf()
        return try {
            val type = object : TypeToken<Map<String, MarketQuote>>() {}.type
            gson.fromJson(jsonString, type) ?: mutableMapOf()
        } catch (e: Exception) {
            mutableMapOf()
        }
    }

    private suspend fun fetchVerifiedRestData(cleanSymbol: String, bseIdentifier: String): MarketQuote? {
        try {
            val toTime = System.currentTimeMillis() / 1000
            val fromTime = toTime - (15 * 24 * 60 * 60)

            val response = api.getHistoricalData(instrumentIdentifier = bseIdentifier, from = fromTime, to = toTime)
            val snapshots = response.Value

            if (!snapshots.isNullOrEmpty()) {
                val lastSnapshot = snapshots.last()
                val previousSnapshot = if (snapshots.size > 1) snapshots[snapshots.size - 2] else lastSnapshot

                val currentPrice = lastSnapshot.Close ?: 0.0
                if (currentPrice == 0.0) return null // Reject empty data

                val previousClose = previousSnapshot.Close ?: currentPrice
                val priceChange = currentPrice - previousClose
                val percentageChange = if (previousClose > 0) (priceChange / previousClose) * 100 else 0.0

                return MarketQuote(
                    symbol = cleanSymbol, name = cleanSymbol, currentPrice = currentPrice,
                    priceChange = priceChange, percentageChange = percentageChange, isPositive = priceChange >= 0,
                    openPrice = lastSnapshot.Open ?: currentPrice, previousClose = previousClose,
                    dayHigh = lastSnapshot.High ?: currentPrice, dayLow = lastSnapshot.Low ?: currentPrice,
                    marketCap = lastSnapshot.MCap ?: 0L, volume = lastSnapshot.Volume ?: 0L
                )
            }
        } catch (e: Exception) {
            Log.e("MarketRepo", "API Fetch Failed for $cleanSymbol", e)
        }
        return null
    }

    override fun getLiveQuotesFlow(symbols: List<String>): Flow<List<MarketQuote>> = flow {
        val cleanSymbols = symbols.map { it.replace(".NS", "").replace(".BO", "") }

        // 1. INSTANT CACHE LOAD
        val currentQuotesMap = loadFromCache()
        if (currentQuotesMap.isNotEmpty()) {
            emit(currentQuotesMap.values.toList())
        }

        // 2. NETWORK FETCH
        coroutineScope {
            val deferredQuotes = cleanSymbols.map { sym ->
                async { fetchVerifiedRestData(sym, bseScripMapper[sym] ?: sym) }
            }
            deferredQuotes.awaitAll().filterNotNull().forEach { quote ->
                currentQuotesMap[quote.symbol] = quote
            }
        }

        // ⚡ 3. THE BOOTSTRAPPER (Failsafe)
        // If the cache is empty AND the API failed (expired key/server dead), inject the Seed Data.
        if (currentQuotesMap.isEmpty()) {
            cleanSymbols.forEach { sym ->
                seedData[sym]?.let { currentQuotesMap[sym] = it }
            }
        }

        // Save state and update UI
        saveToCache(currentQuotesMap)
        emit(currentQuotesMap.values.toList())

        // 4. LIVE WEBSOCKET CONNECTION
        wsClient.subscribe(cleanSymbols)

        wsClient.quotesFlow.collect { liveQuotes ->
            var updated = false
            liveQuotes.forEach { liveQuote ->
                if (cleanSymbols.contains(liveQuote.symbol)) {
                    val existing = currentQuotesMap[liveQuote.symbol]
                    currentQuotesMap[liveQuote.symbol] = existing?.copy(
                        currentPrice = liveQuote.currentPrice,
                        priceChange = liveQuote.priceChange,
                        percentageChange = liveQuote.percentageChange,
                        isPositive = liveQuote.isPositive,
                        dayHigh = if (liveQuote.dayHigh > 0) liveQuote.dayHigh else existing.dayHigh,
                        dayLow = if (liveQuote.dayLow > 0) liveQuote.dayLow else existing.dayLow,
                        openPrice = if (liveQuote.openPrice > 0) liveQuote.openPrice else existing.openPrice,
                        previousClose = if (liveQuote.previousClose > 0) liveQuote.previousClose else existing.previousClose,
                        volume = liveQuote.volume,

                        // ⚡ Syncing the new fields to the active map
                        averageTradedPrice = liveQuote.averageTradedPrice,
                        buyPrice = liveQuote.buyPrice,
                        buyQty = liveQuote.buyQty,
                        sellPrice = liveQuote.sellPrice,
                        sellQty = liveQuote.sellQty,
                        lastTradeQty = liveQuote.lastTradeQty,
                        openInterest = liveQuote.openInterest,
                        quotationLot = liveQuote.quotationLot,
                        tradedValue = liveQuote.tradedValue,
                        isDummy = false // It's a real live tick, remove the dummy flag
                    ) ?: liveQuote
                    updated = true
                }
            }
            if (updated) {
                saveToCache(currentQuotesMap)
                emit(currentQuotesMap.values.toList())
            }
        }    }

    override suspend fun getHistoricalData(symbol: String): Result<MarketQuote> {
        return withContext(Dispatchers.IO) {
            val cleanSymbol = symbol.replace(".NS", "").replace(".BO", "")
            val bseIdentifier = bseScripMapper[cleanSymbol] ?: cleanSymbol

            // 1. Try REST API
            val restData = fetchVerifiedRestData(cleanSymbol, bseIdentifier)
            if (restData != null) return@withContext Result.success(restData)

            // 2. Try Cache
            val cachedMap = loadFromCache()
            if (cachedMap.containsKey(cleanSymbol)) return@withContext Result.success(cachedMap[cleanSymbol]!!)

            // 3. Try Seed Data
            if (seedData.containsKey(cleanSymbol)) return@withContext Result.success(seedData[cleanSymbol]!!)

            Result.failure(Exception("Lisuns server offline and no data available."))
        }
    }
}