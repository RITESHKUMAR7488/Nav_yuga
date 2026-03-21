package com.example.mahayuga.feature.navyuga.data.repository

import com.example.mahayuga.feature.navyuga.data.remote.AccelPixApi
import com.example.mahayuga.feature.navyuga.domain.model.AssetItemUI
import com.example.mahayuga.feature.navyuga.domain.model.LocalAssetDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AssetAggregatorRepositoryImpl(
    private val api: AccelPixApi
) {
    // Shifting thread to IO to protect the main UI thread from heavy parsing and network calls
    suspend fun getMergedWatchlist(): List<AssetItemUI> = withContext(Dispatchers.IO) {
        val staticAssets = LocalAssetDatabase.assets

        // Calculate dynamic dates for the API request so it isn't hardcoded to a dead date
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")
        val now = LocalDateTime.now()
        val endDateString = now.format(formatter)
        val startDateString = now.minusDays(5).format(formatter) // 5 days lookback

        // async scatters the GET requests concurrently
        val deferredQuotes = staticAssets.map { asset ->
            async {
                try {
                    api.getLiveQuote(
                        ticker = asset.ticker,
                        startDate = startDateString,
                        endDate = endDateString,
                        interval = "1",
                        apiToken = "%2F6FDlKysBUWiU9pZhqKcrt%2FL%2BFI%3D"
                    )
                } catch (e: Exception) {
                    null // If one fails, absorb the crash and return null
                }
            }
        }

        // awaitAll waits for the slowest request to finish, then we filter out the failed (null) ones
        val rawQuotes = deferredQuotes.awaitAll().filterNotNull()

        staticAssets.map { staticItem ->
            val liveQuote = rawQuotes.find { it.tkr == staticItem.ticker }
            if (liveQuote != null) {
                val calculatedChange = if (liveQuote.op > 0) {
                    ((liveQuote.cp - liveQuote.op) / liveQuote.op) * 100
                } else {
                    0.0
                }
                staticItem.copy(
                    livePrice = liveQuote.cp,
                    percentChange = calculatedChange
                )
            } else {
                staticItem
            }
        }
    }
}