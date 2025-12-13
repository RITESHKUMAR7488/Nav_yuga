package com.example.mahayuga.feature.navyuga.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    var selectedCountry by remember { mutableStateOf("India") }
    var selectedCity by remember { mutableStateOf("All Cities") }

    Scaffold(
        containerColor = Color.Black, // Consistent Black Background
        topBar = {
            // Simple title or TopBar can be added here if needed
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Find Properties",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp, top = 16.dp)
            )

            // --- UPDATED DROPDOWNS (RoiScreen Style) ---
            NavyugaExposedDropdown("Country", viewModel.countries, selectedCountry) { selectedCountry = it }
            Spacer(modifier = Modifier.height(16.dp))
            NavyugaExposedDropdown("City", viewModel.cities, selectedCity) { selectedCity = it }

            Spacer(modifier = Modifier.height(32.dp))

            // --- SEARCH BUTTON (Lighter Blue) ---
            Button(
                onClick = {
                    // Navigate to the new Results Page
                    navController.navigate("search_results/$selectedCountry/$selectedCity")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF60A5FA), // Lighter Blue
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Search",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavyugaExposedDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelectionChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(), // Makes the whole field clickable
            readOnly = true,
            value = selected,
            onValueChange = {},
            label = { Text(label, color = Color.White.copy(0.7f)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF1E293B), // Dark surface
                unfocusedContainerColor = Color(0xFF1E293B),
                focusedBorderColor = Color(0xFF60A5FA),
                unfocusedBorderColor = Color.White.copy(0.2f)
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium) // Big word
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color(0xFF1E293B)) // Match dropdown background
                .fillMaxWidth()
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(
                            selectionOption,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    },
                    onClick = {
                        onSelectionChange(selectionOption)
                        expanded = false
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}