package com.example.mahayuga.feature.navyuga.domain.repository

import android.content.SharedPreferences
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

    // Lisuns REST strictly requires BSE scrip codes
    private val bseScripMapper = mapOf(
        "EMBASSY" to "542602",
        "MINDSPACE" to "543217",
        "BIRET" to "543261",
        "NEXUS" to "543890",
        "PSTITANIA" to "544304",
        "PSPLATINA" to "544305"
    )

    // --- CACHING LOGIC ---
    private fun saveToCache(quotesMap: Map<String, MarketQuote>) {
        val jsonString = gson.toJson(quotesMap)
        prefs.edit().putString(CACHE_KEY, jsonString).apply()
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

    // --- VERIFIED POSTMAN REST FETCHER ---
    private suspend fun fetchVerifiedRestData(cleanSymbol: String, bseIdentifier: String): MarketQuote? {
        try {
            // Look back 15 days to guarantee we hit data, even on the test server
            val toTime = System.currentTimeMillis() / 1000
            val fromTime = toTime - (15 * 24 * 60 * 60)

            val response = api.getHistoricalData(
                instrumentIdentifier = bseIdentifier,
                from = fromTime,
                to = toTime
            )

            val snapshots = response.Value
            if (!snapshots.isNullOrEmpty()) {
                val lastSnapshot = snapshots.last()
                val previousSnapshot = if (snapshots.size > 1) snapshots[snapshots.size - 2] else lastSnapshot

                val currentPrice = lastSnapshot.Close ?: 0.0
                val previousClose = previousSnapshot.Close ?: currentPrice
                val priceChange = currentPrice - previousClose
                val percentageChange = if (previousClose > 0) (priceChange / previousClose) * 100 else 0.0

                return MarketQuote(
                    symbol = cleanSymbol,
                    name = cleanSymbol,
                    currentPrice = currentPrice,
                    priceChange = priceChange,
                    percentageChange = percentageChange,
                    isPositive = priceChange >= 0,
                    openPrice = lastSnapshot.Open ?: currentPrice,
                    previousClose = previousClose,
                    dayHigh = lastSnapshot.High ?: currentPrice,
                    dayLow = lastSnapshot.Low ?: currentPrice,
                    marketCap = lastSnapshot.MCap ?: 0L,
                    volume = lastSnapshot.Volume ?: 0L
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun getLiveQuotesFlow(symbols: List<String>): Flow<List<MarketQuote>> = flow {
        val cleanSymbols = symbols.map { it.replace(".NS", "").replace(".BO", "") }

        // ⚡ PHASE 1: INSTANT CACHE LOAD
        val currentQuotesMap = loadFromCache()

        if (currentQuotesMap.isNotEmpty()) {
            emit(currentQuotesMap.values.toList())
        }

        // ⚡ PHASE 2: VERIFIED REST FETCH
        coroutineScope {
            val deferredQuotes = cleanSymbols.map { sym ->
                async {
                    val bseId = bseScripMapper[sym] ?: sym
                    fetchVerifiedRestData(sym, bseId) // Uses the GetScripMCap we know works
                }
            }

            deferredQuotes.awaitAll().filterNotNull().forEach { quote ->
                currentQuotesMap[quote.symbol] = quote
            }
        }

        // Save fresh data to memory and update UI
        if (currentQuotesMap.isNotEmpty()) {
            saveToCache(currentQuotesMap)
            emit(currentQuotesMap.values.toList())
        } else {
            // Failsafe empty emission to kill loading spinners
            emit(emptyList())
        }

        // ⚡ PHASE 3: LIVE WEBSOCKET CONNECTION
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
                        dayHigh = if (liveQuote.dayHigh > 0) liveQuote.dayHigh else existing?.dayHigh ?: 0.0,
                        dayLow = if (liveQuote.dayLow > 0) liveQuote.dayLow else existing?.dayLow ?: 0.0
                    ) ?: liveQuote
                    updated = true
                }
            }
            if (updated) {
                saveToCache(currentQuotesMap)
                emit(currentQuotesMap.values.toList())
            }
        }
    }

    override suspend fun getHistoricalData(symbol: String): Result<MarketQuote> {
        return withContext(Dispatchers.IO) {
            val cleanSymbol = symbol.replace(".NS", "").replace(".BO", "")
            val bseIdentifier = bseScripMapper[cleanSymbol] ?: cleanSymbol

            // 1. Try REST API (GetScripMCap)
            val restData = fetchVerifiedRestData(cleanSymbol, bseIdentifier)
            if (restData != null && restData.currentPrice > 0.0) {
                return@withContext Result.success(restData)
            }

            // 2. Try Memory Cache
            val cachedMap = loadFromCache()
            if (cachedMap.containsKey(cleanSymbol)) {
                return@withContext Result.success(cachedMap[cleanSymbol]!!)
            }

            // 3. Complete Failure
            Result.failure(Exception("Lisuns server offline and no cached memory available."))
        }
    }
}