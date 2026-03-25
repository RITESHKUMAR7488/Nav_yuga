package com.example.mahayuga.feature.navyuga.data.remote

import android.util.Log
import com.example.mahayuga.feature.navyuga.domain.model.MarketQuote
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LisunsWebSocketClient @Inject constructor(
    private val client: OkHttpClient,
    private val gson: Gson
) : WebSocketListener() {

    private var webSocket: WebSocket? = null
    private var isSocketOpen = false
    private val activeSymbols = mutableSetOf<String>() // ⚡ QUEUE for race conditions
    private val latestQuotes = ConcurrentHashMap<String, MarketQuote>()

    private val _quotesFlow = MutableStateFlow<List<MarketQuote>>(emptyList())
    val quotesFlow: StateFlow<List<MarketQuote>> = _quotesFlow.asStateFlow()

    init {
        connect()
    }

    private fun connect() {
        if (webSocket != null) return
        val request = Request.Builder().url("wss://test.lisuns.com:4576/").build()
        webSocket = client.newWebSocket(request, this)
    }

    fun subscribe(symbols: List<String>) {
        activeSymbols.addAll(symbols) // Save what we want to listen to
        if (isSocketOpen) {
            sendSubscriptions(symbols)
        } else {
            connect() // Ensure we are trying to connect
        }
    }

    private fun sendSubscriptions(symbols: Iterable<String>) {
        symbols.forEach { symbol ->
            val wsSymbol = if (symbol.contains(".")) symbol else "$symbol.RR"
            val msg = """{"MessageType":"SubscribeRealtime","Exchange":"NSE","InstrumentIdentifier":"$wsSymbol"}"""
            webSocket?.send(msg)
            Log.d("LisunsWS", "Subscribed to: $wsSymbol")
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        isSocketOpen = true
        // 1. Authenticate FIRST
        val authMsg = """{"MessageType":"Authenticate","Password":"28a186ec-a3d9-4d7e-a007-e14532300c56"}"""
        webSocket.send(authMsg)
        Log.d("LisunsWS", "Connected and Authenticated")

        // 2. Now it is safe to fire off any queued subscriptions
        sendSubscriptions(activeSymbols)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        try {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val map: Map<String, Any> = gson.fromJson(text, type)

            if (map["MessageType"] == "RealtimeResult" || map["MessageType"] == "QuoteResult") {
                val rawSymbol = map["InstrumentIdentifier"] as? String ?: return
                val symbol = rawSymbol.replace(".RR", "")

                // Extracting all the real data from the payload
                val lastPrice = (map["LastTradePrice"] as? Number)?.toDouble() ?: 0.0
                val change = (map["PriceChange"] as? Number)?.toDouble() ?: 0.0
                val changePct = (map["PriceChangePercentage"] as? Number)?.toDouble() ?: 0.0
                val high = (map["High"] as? Number)?.toDouble() ?: 0.0
                val low = (map["Low"] as? Number)?.toDouble() ?: 0.0
                val open = (map["Open"] as? Number)?.toDouble() ?: 0.0
                val close = (map["Close"] as? Number)?.toDouble() ?: 0.0
                val volume = (map["TotalQtyTraded"] as? Number)?.toLong() ?: 0L

                // ⚡ NEW FIELDS
                val avgPrice = (map["AverageTradedPrice"] as? Number)?.toDouble() ?: 0.0
                val buyPrice = (map["BuyPrice"] as? Number)?.toDouble() ?: 0.0
                val buyQty = (map["BuyQty"] as? Number)?.toLong() ?: 0L
                val sellPrice = (map["SellPrice"] as? Number)?.toDouble() ?: 0.0
                val sellQty = (map["SellQty"] as? Number)?.toLong() ?: 0L
                val lastTradeQty = (map["LastTradeQty"] as? Number)?.toLong() ?: 0L
                val openInterest = (map["OpenInterest"] as? Number)?.toLong() ?: 0L
                val quotationLot = (map["QuotationLot"] as? Number)?.toDouble() ?: 0.0
                val tradedValue = (map["Value"] as? Number)?.toDouble() ?: 0.0

                val quote = MarketQuote(
                    symbol = symbol,
                    name = symbol,
                    currentPrice = lastPrice,
                    priceChange = change,
                    percentageChange = changePct,
                    isPositive = change >= 0,
                    dayHigh = high,
                    dayLow = low,
                    openPrice = open,
                    previousClose = close,
                    volume = volume,
                    averageTradedPrice = avgPrice,
                    buyPrice = buyPrice,
                    buyQty = buyQty,
                    sellPrice = sellPrice,
                    sellQty = sellQty,
                    lastTradeQty = lastTradeQty,
                    openInterest = openInterest,
                    quotationLot = quotationLot,
                    tradedValue = tradedValue
                )

                latestQuotes[symbol] = quote
                _quotesFlow.update { latestQuotes.values.toList() }
                Log.d("LisunsWS", "TICK RECEIVED: $symbol @ $lastPrice")
            }
        } catch (e: Exception) {
            Log.e("LisunsWS", "Parsing Error", e)
        }
    }
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        isSocketOpen = false
        this.webSocket = null
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("LisunsWS", "WebSocket Failure", t)
        isSocketOpen = false
        this.webSocket = null
        Thread.sleep(5000)
        connect()
    }
}