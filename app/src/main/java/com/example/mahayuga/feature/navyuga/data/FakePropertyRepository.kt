package com.example.mahayuga.feature.navyuga.data

import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakePropertyRepository @Inject constructor() {

    // ⚡ Reactive List (StateFlow)
    private val _properties = MutableStateFlow(
        listOf(
            PropertyModel(
                id = "1",
                title = "Reliance Hub",
                location = "Kolkata, Park Street", // Added for compatibility
                minInvest = "5000",
                roi = 8.5,
                fundedPercent = 65,
                imageUrls = listOf("https://images.unsplash.com/photo-1486406146926-c627a92ad1ab"),
                status = "Available",
                description = "A premium commercial space located in the heart of Kolkata's business district. High footfall and long-term lease with Reliance.",
                totalValuation = "₹2.5 Cr",
                rentReturn = "8.5%",
                address = "12A Park Street",
                city = "Kolkata",
                state = "West Bengal"
            ),
            PropertyModel(
                id = "2",
                title = "Tanishq Plaza",
                location = "Bangalore, Indiranagar",
                minInvest = "10000",
                roi = 9.2,
                fundedPercent = 80,
                imageUrls = listOf("https://images.unsplash.com/photo-1582037928769-181f2422677e"),
                status = "Available",
                description = "Luxury retail space leased to Tanishq. Located in Indiranagar, Bangalore's most sought-after commercial hub.",
                totalValuation = "₹5.1 Cr",
                rentReturn = "9.2%",
                address = "100 Feet Road, Indiranagar",
                city = "Bangalore",
                state = "Karnataka"
            ),
            PropertyModel(
                id = "3",
                title = "Starbucks Cyber Hub",
                location = "Gurugram, DLF",
                minInvest = "15000",
                roi = 7.5,
                fundedPercent = 100,
                imageUrls = listOf("https://images.unsplash.com/photo-1554118811-1e0d58224f24"),
                status = "Funded",
                description = "Prime coffee shop location in Cyber Hub. Fully funded and operational.",
                totalValuation = "₹1.8 Cr",
                rentReturn = "7.5%",
                address = "DLF Cyber City",
                city = "Gurugram",
                state = "Haryana"
            ),
            PropertyModel(
                id = "4",
                title = "Domino's Point",
                location = "Mumbai, Bandra",
                minInvest = "5000",
                roi = 14.5,
                fundedPercent = 100,
                imageUrls = listOf("https://images.unsplash.com/photo-1552566626-52f8b828add9"),
                status = "Exited",
                description = "Quick service restaurant outlet with high delivery volume.",
                totalValuation = "₹85 Lakhs",
                rentReturn = "14.5%",
                address = "Hill Road, Bandra West",
                city = "Mumbai",
                state = "Maharashtra"
            )
        )
    )

    val properties: StateFlow<List<PropertyModel>> = _properties.asStateFlow()

    fun addProperty(property: PropertyModel) {
        val currentList = _properties.value
        _properties.value = listOf(property) + currentList
    }

    fun getPropertyById(id: String): PropertyModel? {
        return _properties.value.find { it.id == id }
    }

    fun searchProperties(query: String, city: String): List<PropertyModel> {
        return _properties.value.filter {
            (it.title.contains(query, ignoreCase = true) || query.isEmpty()) &&
                    (it.location.contains(city, ignoreCase = true) || city == "All Cities")
        }
    }
}