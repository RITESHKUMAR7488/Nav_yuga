// main/java/com/example/mahayuga/feature/navyuga/domain/model/ReitModels.kt
package com.example.mahayuga.feature.navyuga.domain.model

data class ReitModel(
    val id: String,
    val name: String,
    val assetManager: String = "Asset Manager",
    val currentPrice: Double,
    val priceChange: Double,
    val priceChangePercent: Double,
    val openPrice: Double,
    val lastPrice: Double,
    val marketCap: String,
    val dividendYield: String,
    val high52Week: Double,
    val low52Week: Double,
    val totalPortfolioValue: String,
    val totalHoldingsMsf: Double,
    val underDevelopmentMsf: Double,
    val occupancyPercent: Int,
    val priceHistory: List<Float>,
    val properties: List<ReitPropertyModel>,
    val news: List<ReitNewsModel>
)

data class ReitPropertyModel(
    val id: String,
    val name: String,
    val location: String,
    val openPrice: Double,
    val lastPrice: Double,
    val priceHistory: List<Float>
)

data class ReitNewsModel(
    val id: String,
    val title: String,
    val contentPreview: String,
    val bulletPoints: List<String>,
    val date: String,
    val imageUrl: String = ""
)