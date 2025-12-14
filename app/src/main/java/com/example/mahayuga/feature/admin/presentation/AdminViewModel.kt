package com.example.mahayuga.feature.admin.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.admin.domain.repository.AdminRepository
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.navyuga.domain.repository.PropertyRepository
import com.example.mahayuga.feature.auth.data.model.UserModel
import com.example.mahayuga.feature.profile.data.remote.ImageUploadApi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    // --- STATES ---
    private val _usersState = MutableStateFlow<UiState<List<UserModel>>>(UiState.Loading)
    val usersState: StateFlow<UiState<List<UserModel>>> = _usersState

    private val _propertiesState = MutableStateFlow<UiState<List<PropertyModel>>>(UiState.Loading)
    val propertiesState: StateFlow<UiState<List<PropertyModel>>> = _propertiesState

    private val _propertyUploadState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val propertyUploadState: StateFlow<UiState<String>> = _propertyUploadState

    // ⚡ NEW STATE: Registration Requests
    private val _requestsState = MutableStateFlow<UiState<List<UserModel>>>(UiState.Loading)
    val requestsState: StateFlow<UiState<List<UserModel>>> = _requestsState

    init {
        fetchUsers()
        fetchProperties()
        fetchPendingRequests()
    }

    // --- DATA FETCHING ---
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

    private fun fetchPendingRequests() {
        viewModelScope.launch {
            adminRepository.getPendingRequests().collect { state ->
                _requestsState.value = state
            }
        }
    }

    // --- USER MANAGEMENT ---
    fun approveUser(uid: String, role: String) {
        viewModelScope.launch {
            adminRepository.approveUserRequest(uid, role)
        }
    }

    fun rejectUser(uid: String) {
        viewModelScope.launch {
            adminRepository.rejectUserRequest(uid)
        }
    }

    fun toggleUserBlock(uid: String, currentStatus: Boolean) {
        viewModelScope.launch {
            adminRepository.toggleUserStatus(uid, !currentStatus)
        }
    }

    // --- PROPERTY MANAGEMENT ---
    fun listNewProperty(
        // Basic
        title: String,
        description: String,
        type: String,
        status: String,

        // Location
        address: String,
        city: String,
        state: String,

        // Property Specs
        age: String,
        area: String,
        floor: String,
        carPark: String,

        // Financials (Overview)
        totalValuation: String,
        minInvest: String,
        roi: Double,
        fundedPercent: Int,

        // Financials (Deep Dive)
        monthlyRent: String,
        grossAnnualRent: String,
        annualPropertyTax: String,

        // Lease Info
        tenantName: String,
        occupationPeriod: String,
        escalation: String,

        // Images
        imageUris: List<Uri>
    ) {
        viewModelScope.launch {
            _propertyUploadState.value = UiState.Loading

            try {
                val uploadedImageUrls = mutableListOf<String>()

                // Parallel Image Upload
                if (imageUris.isNotEmpty()) {
                    val uploadJobs = imageUris.map { uri ->
                        async { uploadSingleImage(uri) }
                    }
                    uploadedImageUrls.addAll(uploadJobs.awaitAll().filterNotNull())
                }

                // Fallback image
                if (uploadedImageUrls.isEmpty()) {
                    uploadedImageUrls.add("https://images.unsplash.com/photo-1486406146926-c627a92ad1ab")
                }

                val newProperty = PropertyModel(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    type = type,
                    status = status,

                    // Location
                    address = address,
                    city = city,
                    state = state,
                    location = "$city, $state",

                    // Specs
                    age = age,
                    area = area,
                    floor = floor,
                    carPark = carPark,

                    // Financials
                    totalValuation = totalValuation,
                    minInvest = minInvest,
                    roi = roi,
                    fundedPercent = fundedPercent,
                    monthlyRent = monthlyRent,
                    grossAnnualRent = grossAnnualRent,
                    annualPropertyTax = annualPropertyTax,

                    // Lease
                    tenantName = tenantName,
                    occupationPeriod = occupationPeriod,
                    escalation = escalation,

                    imageUrls = uploadedImageUrls
                )

                // Save to DB
                val result = propertyRepository.addProperty(newProperty)
                if (result is UiState.Success) {
                    _propertyUploadState.value = UiState.Success("Property Listed Successfully!")
                    fetchProperties()
                } else {
                    _propertyUploadState.value = UiState.Failure("Failed to save to DB")
                }

            } catch (e: Exception) {
                _propertyUploadState.value = UiState.Failure("Error: ${e.message}")
            }
        }
    }

    private suspend fun uploadSingleImage(uri: Uri): String? {
        return try {
            val file = getFileFromUri(uri) ?: return null
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("source", file.name, requestFile)
            val key = "6d207e02198a847aa98d0a2a901485a5".toRequestBody("text/plain".toMediaTypeOrNull())
            val format = "json".toRequestBody("text/plain".toMediaTypeOrNull())

            val response = api.uploadImage(key, body, format)
            if (response.status_code == 200) response.image.url else null
        } catch (e: Exception) {
            null
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "upload_${UUID.randomUUID()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()
            file
        } catch (e: Exception) {
            null
        }
    }

    // ⚡ MISSING FUNCTION RESTORED
    fun resetUploadState() {
        _propertyUploadState.value = UiState.Idle
    }
}