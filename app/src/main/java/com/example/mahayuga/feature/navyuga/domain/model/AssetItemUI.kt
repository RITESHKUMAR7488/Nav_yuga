package com.example.mahayuga.feature.navyuga.domain.model

data class AssetItemUI(
    val cleanName: String,
    val ticker: String,
    val minInvestment: String,
    val livePrice: Double = 0.0,
    val percentChange: Double = 0.0
)

object LocalAssetDatabase {
    val assets = listOf(
        AssetItemUI(
            cleanName = "Embassy Office Parks",
            ticker = "EMBASSY",
            minInvestment = "₹ 350.00"
        ),
        AssetItemUI(
            cleanName = "Mindspace Business Parks",
            ticker = "MINDSPACE",
            minInvestment = "₹ 320.00"
        ),
        AssetItemUI(
            cleanName = "Brookfield India",
            ticker = "BIRET",
            minInvestment = "₹ 260.00"
        )
    )
}