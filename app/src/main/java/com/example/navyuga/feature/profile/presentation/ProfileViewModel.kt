package com.example.navyuga.feature.profile.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navyuga.core.common.UiState
import com.example.navyuga.core.data.local.PreferenceManager
import com.example.navyuga.feature.profile.data.model.DocumentModel
import com.example.navyuga.feature.profile.data.model.ProfileStat
import com.example.navyuga.feature.profile.data.remote.ImageUploadApi
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val api: ImageUploadApi,
    private val preferenceManager: PreferenceManager,
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _stats = MutableStateFlow<List<ProfileStat>>(emptyList())
    val stats: StateFlow<List<ProfileStat>> = _stats

    private val _documents = MutableStateFlow<List<DocumentModel>>(emptyList())
    val documents: StateFlow<List<DocumentModel>> = _documents

    private val _uploadState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val uploadState: StateFlow<UiState<String>> = _uploadState

    init {
        loadData()
    }

    private fun loadData() {
        // ⚡ EXACT 5 CARDS REQUESTED
        _stats.value = listOf(
            ProfileStat("My Properties", "12", 0.65f, 0xFF00E5FF),       // Cyan
            ProfileStat("Annual Rent", "₹8.5L", 0.85f, 0xFF2979FF),      // Blue
            ProfileStat("Total Sqft", "12,500", 0.50f, 0xFFAA00FF),      // Purple
            ProfileStat("Total P/L", "+₹12.4L", 0.92f, 0xFF00E676),      // Green
            ProfileStat("Avg ROI", "11.5%", 0.75f, 0xFFFF9800)           // Orange
        )

        _documents.value = listOf(
            DocumentModel("1", "PAN Card", "Verified"),
            DocumentModel("2", "Aadhaar Front", "Pending"),
            DocumentModel("3", "Bank Proof", "Rejected"),
            DocumentModel("4", "Voter ID", "Pending")
        )
    }

    // Logout Logic
    fun logout() {
        auth.signOut()
        preferenceManager.saveLoginState(false)
    }

    // Image Upload Logic
    fun uploadDocument(uri: Uri, docId: String) {
        viewModelScope.launch {
            _uploadState.value = UiState.Loading
            try {
                val file = getFileFromUri(uri) ?: throw Exception("File error")

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("source", file.name, requestFile)
                val key = "6d207e02198a847aa98d0a2a901485a5".toRequestBody("text/plain".toMediaTypeOrNull())
                val format = "json".toRequestBody("text/plain".toMediaTypeOrNull())

                val response = api.uploadImage(key, body, format)

                if (response.status_code == 200) {
                    _uploadState.value = UiState.Success("Uploaded: ${response.image.url}")
                    val updatedList = _documents.value.map {
                        if (it.id == docId) it.copy(status = "Verified", imageUrl = response.image.url) else it
                    }
                    _documents.value = updatedList
                } else {
                    _uploadState.value = UiState.Failure("Upload Failed")
                }
            } catch (e: Exception) {
                _uploadState.value = UiState.Failure(e.message ?: "Error")
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "temp_upload.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()
            file
        } catch (e: Exception) {
            null
        }
    }
}