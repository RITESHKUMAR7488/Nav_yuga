package com.example.mahayuga.feature.navyuga.domain.repository

import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun getAllProperties(): Flow<UiState<List<PropertyModel>>>
    fun getPropertyById(id: String): Flow<PropertyModel?>
    suspend fun addProperty(property: PropertyModel): UiState<String>
    suspend fun deleteProperty(propertyId: String): UiState<String>
}