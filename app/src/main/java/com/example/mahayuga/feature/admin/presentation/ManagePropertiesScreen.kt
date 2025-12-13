package com.example.mahayuga.feature.admin.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePropertiesScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    // ⚡ Observe the new UiState flow
    val propertiesState by viewModel.propertiesState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Properties") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        // ⚡ Handle Loading/Success/Error
        when (val state = propertiesState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.data) { property ->
                        AdminPropertyItem(property)
                    }
                }
            }
            is UiState.Failure -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.message}", color = ErrorRed)
                }
            }
            // Handle initial/idle state if necessary, typically same as loading or empty
            else -> {}
        }
    }
}

@Composable
fun AdminPropertyItem(property: PropertyModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = property.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Status: ${property.status} • Min: ₹${property.minInvest}",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandBlue
                )
            }

            // Edit Action
            IconButton(onClick = { /* Navigate to Edit Screen */ }) {
                Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
            }

            // Delete Action
            IconButton(onClick = { /* Delete Logic */ }) {
                Icon(Icons.Default.Delete, "Delete", tint = ErrorRed)
            }
        }
    }
}
@Preview
@Composable
fun ManagePropertiesScreenPreview() {
    NavyugaTheme {
        ManagePropertiesScreen(
            navController = rememberNavController()

        )
    }
}

