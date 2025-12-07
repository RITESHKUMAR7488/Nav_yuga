package com.example.navyuga.feature.arthyuga.data

import com.example.navyuga.feature.arthyuga.domain.model.PropertyModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakePropertyRepository @Inject constructor() {

    val properties = listOf(
        PropertyModel(
            id = "1",
            title = "Reliance Hub",
            location = "Kolkata, Park Street",
            minInvest = "5000",
            roi = 8.5,
            fundedPercent = 65,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab",
                "https://images.unsplash.com/photo-1497366216548-37526070297c",
                "https://images.unsplash.com/photo-1497215728101-856f4ea42174"
            ),
            status = "Available"
        ),
        PropertyModel(
            id = "2",
            title = "Tanishq Plaza",
            location = "Bangalore, Indiranagar",
            minInvest = "10000",
            roi = 9.2,
            fundedPercent = 80,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582037928769-181f2422677e",
                "https://images.unsplash.com/photo-1560179707-f14e90ef3623"
            ),
            status = "Available"
        ),
        PropertyModel(
            id = "3",
            title = "Starbucks Cyber Hub",
            location = "Gurugram, DLF",
            minInvest = "15000",
            roi = 7.5,
            fundedPercent = 100,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1554118811-1e0d58224f24"
            ),
            status = "Funded"
        ),
        PropertyModel(
            id = "4",
            title = "Domino's Point",
            location = "Mumbai, Bandra",
            minInvest = "5000",
            roi = 14.5,
            fundedPercent = 100,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1552566626-52f8b828add9"
            ),
            status = "Exited"
        )
    )

    fun getPropertyById(id: String): PropertyModel? {
        return properties.find { it.id == id }
    }

    fun searchProperties(query: String, city: String): List<PropertyModel> {
        return properties.filter {
            (it.title.contains(query, ignoreCase = true) || query.isEmpty()) &&
                    (it.location.contains(city, ignoreCase = true) || city == "All Cities")
        }
    }
}