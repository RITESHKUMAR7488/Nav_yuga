package com.example.navyuga.feature.admin.presentation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.arthyuga.presentation.search.NavyugaDropdown
import com.example.navyuga.feature.auth.presentation.components.NavyugaGradientButton
import com.example.navyuga.feature.auth.presentation.components.NavyugaTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    // Basic Info
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Location Details
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }

    // Financials
    var totalValuation by remember { mutableStateOf("") } // e.g., ₹2.5 Cr
    var minInvest by remember { mutableStateOf("") }      // e.g., 5000
    var rentReturn by remember { mutableStateOf("") }     // e.g., 8.5% or ₹50k/mo
    var roi by remember { mutableStateOf("") }            // e.g., 12.5

    var selectedStatus by remember { mutableStateOf("Available") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val uploadState by viewModel.propertyUploadState.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // ⚡ Listen for Success
    LaunchedEffect(uploadState) {
        if (uploadState is UiState.Success) {
            Toast.makeText(context, "Property Published Successfully!", Toast.LENGTH_LONG).show()
            viewModel.resetUploadState()
            navController.popBackStack()
        } else if (uploadState is UiState.Failure) {
            Toast.makeText(context, (uploadState as UiState.Failure).message, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("List New Property") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- 1. IMAGE PICKER ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { launcher.launch("image/*") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Property Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                            Text("Tap to upload cover image", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 2. BASIC INFO ---
            Text("Basic Details", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            NavyugaTextField(value = title, onValueChange = { title = it }, label = "Property Title", icon = Icons.Default.Home)
            Spacer(modifier = Modifier.height(16.dp))

            // Description (Using standard OutlinedTextField for multiline support if wrapper doesn't support it,
            // but sticking to NavyugaTextField for consistency if it handles it, otherwise:)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- 3. LOCATION ---
            Text("Location", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            NavyugaTextField(value = address, onValueChange = { address = it }, label = "Street Address", icon = Icons.Default.LocationOn)
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    NavyugaTextField(value = city, onValueChange = { city = it }, label = "City", icon = Icons.Default.LocationCity)
                }
                Box(modifier = Modifier.weight(1f)) {
                    NavyugaTextField(value = state, onValueChange = { state = it }, label = "State", icon = Icons.Default.Map)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 4. FINANCIALS ---
            Text("Financials", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    NavyugaTextField(value = totalValuation, onValueChange = { totalValuation = it }, label = "Total Val (₹ Cr)", icon = Icons.Default.MonetizationOn)
                }
                Box(modifier = Modifier.weight(1f)) {
                    NavyugaTextField(value = minInvest, onValueChange = { minInvest = it }, label = "Min Invest (₹)", icon = Icons.Default.AttachMoney)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    NavyugaTextField(value = rentReturn, onValueChange = { rentReturn = it }, label = "Rent (e.g. 8%)", icon = Icons.Default.Percent)
                }
                Box(modifier = Modifier.weight(1f)) {
                    NavyugaTextField(value = roi, onValueChange = { roi = it }, label = "Net ROI (%)", icon = Icons.Default.TrendingUp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            NavyugaDropdown(
                label = "Status",
                options = listOf("Available", "Funded", "Coming Soon"),
                selected = selectedStatus,
                onSelectionChange = { selectedStatus = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- 5. PUBLISH BUTTON ---
            NavyugaGradientButton(
                text = if (uploadState is UiState.Loading) "Uploading..." else "Publish Live",
                isLoading = uploadState is UiState.Loading,
                onClick = {
                    if (title.isNotEmpty() && minInvest.isNotEmpty() && city.isNotEmpty()) {
                        viewModel.listNewProperty(
                            title = title,
                            description = description,
                            address = address,
                            city = city,
                            state = state,
                            totalValuation = totalValuation,
                            minInvest = minInvest,
                            rentReturn = rentReturn,
                            roi = roi.toDoubleOrNull() ?: 0.0,
                            status = selectedStatus,
                            imageUri = selectedImageUri
                        )
                    } else {
                        Toast.makeText(context, "Please fill all required details", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            // Bottom padding for scrolling
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}