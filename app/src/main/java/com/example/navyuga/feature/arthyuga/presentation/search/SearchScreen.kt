package com.example.navyuga.feature.arthyuga.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.arthyuga.presentation.home.PropertyCard // Uses the fixed PropertyCard from above
import com.example.navyuga.feature.auth.presentation.components.NavyugaGradientButton
import com.example.navyuga.ui.theme.*

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    var selectedCountry by remember { mutableStateOf("India") }
    var selectedCity by remember { mutableStateOf("All Cities") }
    var selectedCurrency by remember { mutableStateOf("INR") }

    val searchState by viewModel.searchResults.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Find Properties",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // --- 3 SMART DROPDOWNS ---
        NavyugaDropdown("Country", viewModel.countries, selectedCountry) { selectedCountry = it }
        Spacer(modifier = Modifier.height(12.dp))
        NavyugaDropdown("City", viewModel.cities, selectedCity) { selectedCity = it }
        Spacer(modifier = Modifier.height(12.dp))
        NavyugaDropdown("Currency", viewModel.currencies, selectedCurrency) { selectedCurrency = it }

        Spacer(modifier = Modifier.height(24.dp))

        NavyugaGradientButton(
            text = "Search",
            onClick = { viewModel.performSearch(selectedCountry, selectedCity, selectedCurrency) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- RESULTS ---
        when (val state = searchState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is UiState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(state.data) { property ->
                        PropertyCard(property, onClick = {
                            navController.navigate("property_detail/${property.id}")
                        })
                    }
                }
            }
            is UiState.Failure -> {
                Text(
                    text = state.message,
                    color = ErrorRed,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            else -> {}
        }
    }
}

@Composable
fun NavyugaDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelectionChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, null, Modifier.clickable { expanded = true })
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier.fillMaxWidth().clickable { expanded = true },
            shape = RoundedCornerShape(12.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        onSelectionChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}