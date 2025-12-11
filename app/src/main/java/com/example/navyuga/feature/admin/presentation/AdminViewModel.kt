package com.example.navyuga.feature.admin.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.admin.domain.repository.AdminRepository
import com.example.navyuga.feature.arthyuga.domain.model.PropertyModel
import com.example.navyuga.feature.arthyuga.domain.repository.PropertyRepository
import com.example.navyuga.feature.auth.data.model.UserModel
import com.example.navyuga.feature.profile.data.remote.ImageUploadApi
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
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val propertyRepository: PropertyRepository,
    private val api: ImageUploadApi,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _usersState = MutableStateFlow<UiState<List<UserModel>>>(UiState.Loading)
    val usersState: StateFlow<UiState<List<UserModel>>> = _usersState

    private val _propertiesState = MutableStateFlow<UiState<List<PropertyModel>>>(UiState.Loading)
    val propertiesState: StateFlow<UiState<List<PropertyModel>>> = _propertiesState

    private val _propertyUploadState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val propertyUploadState: StateFlow<UiState<String>> = _propertyUploadState

    init {
        fetchUsers()
        fetchProperties()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            adminRepository.getAllUsers().collect { state -> _usersState.value = state }
        }
    }

    private fun fetchProperties() {
        viewModelScope.launch {
            propertyRepository.getAllProperties().collect { state -> _propertiesState.value = state }
        }
    }

    fun toggleUserBlock(uid: String, currentStatus: Boolean) {
        viewModelScope.launch {
            adminRepository.toggleUserStatus(uid, !currentStatus)
        }
    }

    // ⚡ UPDATED: Accepts all new fields for the detailed PropertyModel
    fun listNewProperty(
        title: String,
        totalValuation: String, // Replaces minInvest for display
        minInvest: String,      // Keep for logic/search
        rentReturn: String,
        roi: Double,
        description: String,
        address: String,
        city: String,
        state: String,
        country: String = "India",
        status: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _propertyUploadState.value = UiState.Loading

            try {
                var finalImageUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab"

                // 1. Upload Image
                if (imageUri != null) {
                    val file = getFileFromUri(imageUri)
                    if (file != null) {
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        val body = MultipartBody.Part.createFormData("source", file.name, requestFile)
                        val key = "6d207e02198a847aa98d0a2a901485a5".toRequestBody("text/plain".toMediaTypeOrNull())
                        val format = "json".toRequestBody("text/plain".toMediaTypeOrNull())

                        val response = api.uploadImage(key, body, format)
                        if (response.status_code == 200) {
                            finalImageUrl = response.image.url
                        }
                    }
                }

                // 2. Create Updated Model
                val newProperty = PropertyModel(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    location = "$city, $state", // Auto-generate location string
                    minInvest = minInvest,
                    roi = roi,
                    fundedPercent = 0,
                    imageUrls = listOf(finalImageUrl),
                    status = status,
                    // New Fields mapped here
                    description = description,
                    totalValuation = totalValuation,
                    rentReturn = rentReturn,
                    address = address,
                    city = city,
                    state = state,
                    country = country
                )

                // 3. Save to Firestore
                val result = propertyRepository.addProperty(newProperty)
                if (result is UiState.Success) {
                    _propertyUploadState.value = UiState.Success("Property Listed Successfully!")
                } else {
                    _propertyUploadState.value = UiState.Failure("Failed to save to DB")
                }

            } catch (e: Exception) {
                _propertyUploadState.value = UiState.Failure("Upload Error: ${e.message}")
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "prop_upload_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()
            file
        } catch (e: Exception) {
            null
        }
    }

    fun resetUploadState() {
        _propertyUploadState.value = UiState.Idle
    }
}