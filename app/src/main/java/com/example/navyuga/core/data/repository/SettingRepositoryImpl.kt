package com.example.navyuga.core.data.repository

import com.example.navyuga.core.common.Constants
import com.example.navyuga.core.domain.repository.SettingsRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SettingsRepository {

    override fun getWhatsAppNumber(): Flow<String> = callbackFlow {
        // 1. Emit default immediately (Safety First)
        trySend(Constants.SUPPORT_WHATSAPP_NUMBER)

        // 2. Listen to Firestore real-time updates
        val listener = firestore.collection("app_settings")
            .document("contact_info")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null && snapshot.exists()) {
                    val remoteNumber = snapshot.getString("whatsapp")
                    if (!remoteNumber.isNullOrEmpty()) {
                        trySend(remoteNumber) // Emit new number if found
                    }
                }
            }

        awaitClose { listener.remove() }
    }
}