package com.example.mahayuga.feature.admin.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.ui.theme.BrandBlue
import com.example.mahayuga.ui.theme.ErrorRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePropertiesScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val propertiesState by viewModel.propertiesState.collectAsState()
    var propertyToDelete by remember { mutableStateOf<PropertyModel?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Properties") },
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

        if (propertyToDelete != null) {
            AlertDialog(
                onDismissRequest = { propertyToDelete = null },
                icon = { Icon(Icons.Default.Warning, null, tint = ErrorRed) },
                title = { Text("Delete Property?") },
                text = { Text("Are you sure you want to delete '${propertyToDelete?.title}'? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            propertyToDelete?.let { viewModel.deleteProperty(it.id) }
                            propertyToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                    ) {
                        Text("Yes, Delete")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { propertyToDelete = null }) {
                        Text("Cancel")
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = propertiesState) {
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is UiState.Failure -> Text("Error: ${state.message}", modifier = Modifier.align(Alignment.Center))
                is UiState.Success -> {
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        items(state.data) { property ->
                            ManagePropertyItem(
                                property = property,
                                onEdit = { navController.navigate("admin_edit_property/${property.id}") },
                                onDelete = { propertyToDelete = property }
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun ManagePropertyItem(
    property: PropertyModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // ⚡ ASSET ID HEADER
            if (property.assetId.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        color = BrandBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "ID: ${property.assetId}",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandBlue,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(property.imageUrls.firstOrNull()),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.Gray),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(property.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("₹${property.totalValuation}", style = MaterialTheme.typography.bodyMedium, color = BrandBlue)
                    Text("ROI: ${property.roi}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Column {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, "Edit", tint = BrandBlue)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Delete", tint = ErrorRed)
                    }
                }
            }
        }
    }
}