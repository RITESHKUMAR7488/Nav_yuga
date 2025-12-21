package com.example.mahayuga.feature.admin.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.admin.data.model.InvestmentModel
import com.example.mahayuga.feature.admin.domain.repository.AdminRepository
import com.example.mahayuga.feature.auth.data.model.UserModel
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.navyuga.domain.repository.PropertyRepository
import com.example.mahayuga.feature.profile.data.remote.ImageUploadApi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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
import kotlin.random.Random

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

    private val _requestsState = MutableStateFlow<UiState<List<UserModel>>>(UiState.Loading)
    val requestsState: StateFlow<UiState<List<UserModel>>> = _requestsState

    private val _selectedUserInvestments =
        MutableStateFlow<UiState<List<InvestmentModel>>>(UiState.Loading)
    val selectedUserInvestments: StateFlow<UiState<List<InvestmentModel>>> =
        _selectedUserInvestments

    private val _deleteOperationState = MutableSharedFlow<UiState<String>>()
    val deleteOperationState: SharedFlow<UiState<String>> = _deleteOperationState

    private val _investmentStatus = MutableSharedFlow<String>()
    val investmentStatus: SharedFlow<String> = _investmentStatus

    // Temporary variables
    var selectedUser: UserModel? = null
        private set
    var selectedProperty: PropertyModel? = null
        private set

    init {
        fetchUsers()
        fetchProperties()
        fetchPendingRequests()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            adminRepository.getAllUsers().collect { state -> _usersState.value = state }
        }
    }

    private fun fetchProperties() {
        viewModelScope.launch {
            propertyRepository.getAllProperties()
                .collect { state -> _propertiesState.value = state }
        }
    }

    private fun fetchPendingRequests() {
        viewModelScope.launch {
            adminRepository.getPendingRequests().collect { state -> _requestsState.value = state }
        }
    }

    // --- USER DETAIL & DELETE LOGIC ---
    fun fetchInvestmentsForUser(userId: String) {
        viewModelScope.launch {
            _selectedUserInvestments.value = UiState.Loading
            adminRepository.getUserInvestments(userId).collect { state ->
                _selectedUserInvestments.value = state
            }
        }
    }

    fun deleteSingleInvestment(investment: InvestmentModel) {
        viewModelScope.launch {
            val result = adminRepository.deleteInvestment(investment)
            _deleteOperationState.emit(result)
            fetchInvestmentsForUser(investment.userId)
        }
    }

    fun deleteUserPermanently(userId: String) {
        viewModelScope.launch {
            val result = adminRepository.deleteUserConstructively(userId)
            _deleteOperationState.emit(result)
        }
    }

    // --- INVESTMENT FLOW LOGIC ---
    fun selectUserForInvestment(user: UserModel) {
        selectedUser = user
    }

    fun selectPropertyForInvestment(property: PropertyModel) {
        selectedProperty = property
    }

    fun submitInvestment(amount: Long, mode: String, reference: String) {
        val user = selectedUser ?: return
        val property = selectedProperty ?: return
        viewModelScope.launch {
            _propertyUploadState.value = UiState.Loading
            val investment = InvestmentModel(
                userId = user.uid,
                userName = user.name,
                propertyId = property.id,
                propertyTitle = property.title,
                amount = amount,
                paymentMode = mode,
                paymentReference = reference
            )
            val result = adminRepository.registerInvestment(investment)
            when (result) {
                is UiState.Success -> {
                    _propertyUploadState.value = UiState.Success(result.data)
                    _investmentStatus.emit("Success: ${result.data}")
                }

                is UiState.Failure -> {
                    _propertyUploadState.value = UiState.Failure(result.message)
                    _investmentStatus.emit("Error: ${result.message}")
                }

                else -> {}
            }
        }
    }

    // --- USER MANAGEMENT ---
    fun toggleUserBlock(uid: String, currentStatus: Boolean) {
        viewModelScope.launch { adminRepository.toggleUserStatus(uid, !currentStatus) }
    }

    fun approveUser(uid: String, role: String) {
        viewModelScope.launch { adminRepository.approveUserRequest(uid, role) }
    }

    fun rejectUser(uid: String) {
        viewModelScope.launch { adminRepository.rejectUserRequest(uid) }
    }

    // --- PROPERTY MANAGEMENT ---
    fun deleteProperty(propertyId: String) {
        viewModelScope.launch {
            propertyRepository.deleteProperty(propertyId)
        }
    }

    fun updateProperty(
        originalProperty: PropertyModel,
        updatedFields: PropertyModel,
        keptImages: List<String>,
        newImageUris: List<Uri>
    ) {
        viewModelScope.launch {
            _propertyUploadState.value = UiState.Loading
            try {
                val newUploadedUrls = mutableListOf<String>()
                if (newImageUris.isNotEmpty()) {
                    val uploadJobs = newImageUris.map { uri ->
                        async { uploadSingleImage(uri) }
                    }
                    newUploadedUrls.addAll(uploadJobs.awaitAll().filterNotNull())
                }

                val finalImages = if ((keptImages + newUploadedUrls).isEmpty()) {
                    listOf("https://images.unsplash.com/photo-1486406146926-c627a92ad1ab")
                } else {
                    keptImages + newUploadedUrls
                }

                val finalProperty = updatedFields.copy(
                    id = originalProperty.id,
                    assetId = originalProperty.assetId,
                    // ⚡ Ensure trending status is carried over if edited
                    isTrending = updatedFields.isTrending,
                    imageUrls = finalImages
                )

                val result = propertyRepository.updateProperty(finalProperty)
                if (result is UiState.Success) {
                    _propertyUploadState.value = UiState.Success("Property Updated Successfully!")
                } else {
                    _propertyUploadState.value = UiState.Failure("Update Failed")
                }
            } catch (e: Exception) {
                _propertyUploadState.value = UiState.Failure("Error: ${e.message}")
            }
        }
    }

    // ⚡ UPDATED: Accept isTrending parameter
    fun listNewProperty(
        title: String, description: String, type: String, status: String,
        address: String, city: String, state: String,
        age: String, area: String, floor: String, carPark: String,
        totalValuation: String, minInvest: String, roi: Double, fundedPercent: Int,
        monthlyRent: String, grossAnnualRent: String, annualPropertyTax: String,
        tenantName: String, occupationPeriod: String, escalation: String,
        exitPrice: String, totalProfit: String,
        legalWrapper: String, totalUnits: String, liquidityRules: String,
        isTrending: Boolean, // ⚡ NEW PARAM
        imageUris: List<Uri>
    ) {
        viewModelScope.launch {
            _propertyUploadState.value = UiState.Loading
            try {
                val uploadedImageUrls = mutableListOf<String>()
                if (imageUris.isNotEmpty()) {
                    val uploadJobs = imageUris.map { uri -> async { uploadSingleImage(uri) } }
                    uploadedImageUrls.addAll(uploadJobs.awaitAll().filterNotNull())
                }
                if (uploadedImageUrls.isEmpty()) {
                    uploadedImageUrls.add("https://images.unsplash.com/photo-1486406146926-c627a92ad1ab")
                }

                val generatedAssetId = "NAV-${System.currentTimeMillis().toString().takeLast(6)}"

                val newProperty = PropertyModel(
                    id = UUID.randomUUID().toString(),
                    assetId = generatedAssetId,
                    title = title,
                    description = description,
                    type = type,
                    status = status,
                    address = address,
                    city = city,
                    state = state,
                    location = "$city, $state",
                    age = age,
                    area = area,
                    floor = floor,
                    carPark = carPark,
                    totalValuation = totalValuation,
                    minInvest = minInvest,
                    roi = roi,
                    fundedPercent = fundedPercent,
                    monthlyRent = monthlyRent,
                    grossAnnualRent = grossAnnualRent,
                    annualPropertyTax = annualPropertyTax,
                    tenantName = tenantName,
                    occupationPeriod = occupationPeriod,
                    escalation = escalation,
                    exitPrice = exitPrice,
                    totalProfit = totalProfit,
                    legalWrapper = legalWrapper,
                    totalUnits = totalUnits,
                    liquidityRules = liquidityRules,
                    isTrending = isTrending, // ⚡ SAVE TRENDING STATUS
                    imageUrls = uploadedImageUrls
                )

                val result = propertyRepository.addProperty(newProperty)
                if (result is UiState.Success) {
                    _propertyUploadState.value =
                        UiState.Success("Listed! Asset ID: $generatedAssetId")
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
            val key =
                "6d207e02198a847aa98d0a2a901485a5".toRequestBody("text/plain".toMediaTypeOrNull())
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

    fun resetUploadState() {
        _propertyUploadState.value = UiState.Idle
    }
}