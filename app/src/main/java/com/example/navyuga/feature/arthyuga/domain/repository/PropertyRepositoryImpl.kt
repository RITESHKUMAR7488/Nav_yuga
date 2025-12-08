package com.example.navyuga.feature.arthyuga.data.repository

import android.util.Log
import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.arthyuga.domain.model.PropertyModel
import com.example.navyuga.feature.arthyuga.domain.repository.PropertyRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PropertyRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PropertyRepository {

    private val collection = firestore.collection("properties")

    override fun getAllProperties(): Flow<UiState<List<PropertyModel>>> = callbackFlow {
        trySend(UiState.Loading)
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(UiState.Failure(error.message ?: "Unknown Error"))
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val properties = snapshot.toObjects(PropertyModel::class.java)
                trySend(UiState.Success(properties))
            }
        }
        awaitClose { listener.remove() }
    }

    // ⚡ FIX: Added Logging & Error Handling
    override fun getPropertyById(id: String): Flow<PropertyModel?> = callbackFlow {
        Log.d("Repo", "Fetching Property ID: $id")

        if (id.isEmpty()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = collection.document(id).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("Repo", "Error fetching detail: ${error.message}")
                trySend(null)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val property = snapshot.toObject(PropertyModel::class.java)
                Log.d("Repo", "Property Found: ${property?.title}")
                trySend(property)
            } else {
                Log.e("Repo", "Document does not exist")
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun addProperty(property: PropertyModel): UiState<String> {
        return try {
            collection.document(property.id).set(property).await()
            UiState.Success("Property Listed Successfully")
        } catch (e: Exception) {
            UiState.Failure(e.message ?: "Failed to add property")
        }
    }

    override suspend fun deleteProperty(propertyId: String): UiState<String> {
        return try {
            collection.document(propertyId).delete().await()
            UiState.Success("Property Deleted")
        } catch (e: Exception) {
            UiState.Failure(e.message ?: "Delete Failed")
        }
    }
}