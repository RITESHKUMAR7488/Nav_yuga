package com.example.mahayuga.feature.admin.presentation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.auth.presentation.components.NavyugaGradientButton
import com.example.mahayuga.feature.auth.presentation.components.NavyugaTextField
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.ui.theme.ErrorRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPropertyScreen(
    navController: NavController,
    propertyId: String,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val propertiesState by viewModel.propertiesState.collectAsState()
    val uploadState by viewModel.propertyUploadState.collectAsState()
    val context = LocalContext.current

    // State Fields (Same as Add Screen)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Commercial") }
    var status by remember { mutableStateOf("Open") }

    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }

    var age by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("") }
    var carPark by remember { mutableStateOf("") }

    var totalValuation by remember { mutableStateOf("") }
    var minInvest by remember { mutableStateOf("") }
    var roi by remember { mutableStateOf("") }
    var fundedPercent by remember { mutableStateOf("") }

    var monthlyRent by remember { mutableStateOf("") }
    var grossAnnualRent by remember { mutableStateOf("") }
    var annualPropertyTax by remember { mutableStateOf("") }

    var tenantName by remember { mutableStateOf("") }
    var occupationPeriod by remember { mutableStateOf("") }
    var escalation by remember { mutableStateOf("") }

    // ⚡ IMAGE MANAGEMENT
    // We maintain two lists:
    // 1. keptImages: URLs of images already on server that user kept
    // 2. newImageUris: Local Uris of new images user added
    var keptImages by remember { mutableStateOf<List<String>>(emptyList()) }
    var newImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var originalProperty by remember { mutableStateOf<PropertyModel?>(null) }

    // ⚡ LOAD DATA
    LaunchedEffect(propertyId, propertiesState) {
        if (propertiesState is UiState.Success) {
            val property = (propertiesState as UiState.Success).data.find { it.id == propertyId }
            property?.let {
                originalProperty = it
                title = it.title
                description = it.description
                type = it.type
                status = it.status
                address = it.address
                city = it.city
                state = it.state
                age = it.age
                area = it.area
                floor = it.floor
                carPark = it.carPark
                totalValuation = it.totalValuation
                minInvest = it.minInvest
                roi = it.roi.toString()
                fundedPercent = it.fundedPercent.toString()
                monthlyRent = it.monthlyRent
                grossAnnualRent = it.grossAnnualRent
                annualPropertyTax = it.annualPropertyTax
                tenantName = it.tenantName
                occupationPeriod = it.occupationPeriod
                escalation = it.escalation
                keptImages = it.imageUrls // Load existing images
            }
        }
    }

    // Image Launcher
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) { uris ->
        newImageUris = newImageUris + uris
    }

    // Handle Upload/Update Result
    LaunchedEffect(uploadState) {
        if (uploadState is UiState.Success) {
            Toast.makeText(context, (uploadState as UiState.Success).data, Toast.LENGTH_LONG).show()
            viewModel.resetUploadState()
            navController.popBackStack()
        } else if (uploadState is UiState.Failure) {
            Toast.makeText(context, (uploadState as UiState.Failure).message, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Property") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // IMAGES SECTION
            Text("Property Images", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // 1. Existing Images (Server)
                items(keptImages) { url ->
                    Box(modifier = Modifier.size(100.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(url),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { keptImages = keptImages - url },
                            modifier = Modifier.align(Alignment.TopEnd).background(ErrorRed.copy(alpha = 0.7f), androidx.compose.foundation.shape.CircleShape).size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // 2. New Images (Local)
                items(newImageUris) { uri ->
                    Box(modifier = Modifier.size(100.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { newImageUris = newImageUris - uri },
                            modifier = Modifier.align(Alignment.TopEnd).background(ErrorRed.copy(alpha = 0.7f), androidx.compose.foundation.shape.CircleShape).size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // 3. Add Button
                item {
                    Button(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.size(100.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.AddAPhoto, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // FORM FIELDS (Grouped for Brevity)
            SectionHeader("Basic Info")
            NavyugaTextField(value = title, onValueChange = { title = it }, label = "Title", icon = Icons.Default.Title)
            Spacer(modifier = Modifier.height(8.dp))
            NavyugaTextField(value = description, onValueChange = { description = it }, label = "Description", icon = Icons.Default.Description)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { NavyugaTextField(value = type, onValueChange = { type = it }, label = "Type", icon = Icons.Default.Category) }
                Box(Modifier.weight(1f)) { NavyugaTextField(value = status, onValueChange = { status = it }, label = "Status", icon = Icons.Default.Info) }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader("Location")
            NavyugaTextField(value = address, onValueChange = { address = it }, label = "Address", icon = Icons.Default.Place)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { NavyugaTextField(value = city, onValueChange = { city = it }, label = "City", icon = Icons.Default.LocationCity) }
                Box(Modifier.weight(1f)) { NavyugaTextField(value = state, onValueChange = { state = it }, label = "State", icon = Icons.Default.Map) }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader("Specifications")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { NavyugaTextField(value = area, onValueChange = { area = it }, label = "Area (sqft)", icon = Icons.Default.AspectRatio) }
                Box(Modifier.weight(1f)) { NavyugaTextField(value = age, onValueChange = { age = it }, label = "Age", icon = Icons.Default.Schedule) }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader("Financials")
            NavyugaTextField(value = totalValuation, onValueChange = { totalValuation = it }, label = "Valuation (₹)", icon = Icons.Default.AttachMoney)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { NavyugaTextField(value = roi, onValueChange = { roi = it }, label = "ROI %", icon = Icons.Default.TrendingUp) }
                Box(Modifier.weight(1f)) { NavyugaTextField(value = fundedPercent, onValueChange = { fundedPercent = it }, label = "Funded %", icon = Icons.Default.PieChart) }
            }

            Spacer(modifier = Modifier.height(32.dp))

            NavyugaGradientButton(
                text = "Update Property",
                isLoading = uploadState is UiState.Loading,
                onClick = {
                    originalProperty?.let { orig ->
                        // Create a temporary object to hold fields
                        val updatedFields = orig.copy(
                            title = title, description = description, type = type, status = status,
                            address = address, city = city, state = state, location = "$city, $state",
                            age = age, area = area, floor = floor, carPark = carPark,
                            totalValuation = totalValuation, minInvest = minInvest,
                            roi = roi.toDoubleOrNull() ?: 0.0,
                            fundedPercent = fundedPercent.toIntOrNull() ?: 0,
                            monthlyRent = monthlyRent, grossAnnualRent = grossAnnualRent, annualPropertyTax = annualPropertyTax,
                            tenantName = tenantName, occupationPeriod = occupationPeriod, escalation = escalation
                        )

                        viewModel.updateProperty(
                            originalProperty = orig,
                            updatedFields = updatedFields,
                            keptImages = keptImages,
                            newImageUris = newImageUris
                        )
                    }
                }
            )
        }
    }
}