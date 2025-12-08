package com.example.navyuga.feature.arthyuga.data

import com.example.navyuga.feature.arthyuga.domain.model.PropertyModel
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
                id = "1", title = "Reliance Hub", location = "Kolkata, Park Street",
                minInvest = "5000", roi = 8.5, fundedPercent = 65,
                imageUrls = listOf("https://images.unsplash.com/photo-1486406146926-c627a92ad1ab"), status = "Available"
            ),
            PropertyModel(
                id = "2", title = "Tanishq Plaza", location = "Bangalore, Indiranagar",
                minInvest = "10000", roi = 9.2, fundedPercent = 80,
                imageUrls = listOf("https://images.unsplash.com/photo-1582037928769-181f2422677e"), status = "Available"
            ),
            PropertyModel(
                id = "3", title = "Starbucks Cyber Hub", location = "Gurugram, DLF",
                minInvest = "15000", roi = 7.5, fundedPercent = 100,
                imageUrls = listOf("https://images.unsplash.com/photo-1554118811-1e0d58224f24"), status = "Funded"
            ),
            PropertyModel(
                id = "4", title = "Domino's Point", location = "Mumbai, Bandra",
                minInvest = "5000", roi = 14.5, fundedPercent = 100,
                imageUrls = listOf("https://images.unsplash.com/photo-1552566626-52f8b828add9"), status = "Exited"
            )
        )
    )

    val properties: StateFlow<List<PropertyModel>> = _properties.asStateFlow()

    // ⚡ Add Property
    fun addProperty(property: PropertyModel) {
        val currentList = _properties.value
        _properties.value = listOf(property) + currentList
    }

    // ⚡ Get Single Property
    fun getPropertyById(id: String): PropertyModel? {
        return _properties.value.find { it.id == id }
    }

    // ⚡ FIX: Search Logic Added Back
    fun searchProperties(query: String, city: String): List<PropertyModel> {
        return _properties.value.filter {
            (it.title.contains(query, ignoreCase = true) || query.isEmpty()) &&
                    (it.location.contains(city, ignoreCase = true) || city == "All Cities")
        }
    }
}