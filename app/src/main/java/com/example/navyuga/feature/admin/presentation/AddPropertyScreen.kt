package com.example.navyuga.feature.admin.presentation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.arthyuga.presentation.search.NavyugaDropdown
import com.example.navyuga.feature.auth.presentation.components.NavyugaGradientButton
import com.example.navyuga.feature.auth.presentation.components.NavyugaTextField
import com.example.navyuga.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var minInvest by remember { mutableStateOf("") }
    var roi by remember { mutableStateOf("") }
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
            // IMAGE PICKER
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

            // FORM FIELDS
            NavyugaTextField(value = title, onValueChange = { title = it }, label = "Property Title", icon = Icons.Default.Home)
            Spacer(modifier = Modifier.height(16.dp))
            NavyugaTextField(value = location, onValueChange = { location = it }, label = "Location", icon = Icons.Default.LocationOn)
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    NavyugaTextField(value = minInvest, onValueChange = { minInvest = it }, label = "Min Invest (₹)", icon = Icons.Default.AttachMoney)
                }
                Box(modifier = Modifier.weight(1f)) {
                    NavyugaTextField(value = roi, onValueChange = { roi = it }, label = "ROI (%)", icon = Icons.Default.TrendingUp)
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

            // PUBLISH BUTTON
            NavyugaGradientButton(
                text = if (uploadState is UiState.Loading) "Uploading..." else "Publish Live",
                isLoading = uploadState is UiState.Loading, // ⚡ Shows spinner while uploading
                onClick = {
                    if (title.isNotEmpty() && minInvest.isNotEmpty()) {
                        // ⚡ Trigger real upload + save
                        viewModel.listNewProperty(
                            title = title,
                            location = location,
                            minInvest = minInvest,
                            roi = roi.toDoubleOrNull() ?: 0.0,
                            status = selectedStatus,
                            imageUri = selectedImageUri
                        )
                    } else {
                        Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}