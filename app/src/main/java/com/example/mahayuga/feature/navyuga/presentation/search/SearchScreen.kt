package com.example.mahayuga.feature.navyuga.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mahayuga.ui.theme.BrandBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    onRoiClick: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    var selectedCountry by remember { mutableStateOf("India") }
    var selectedCity by remember { mutableStateOf("All Cities") }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = "Find Properties",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onRoiClick,
                containerColor = Color(0xFF4361EE).copy(alpha = 0.8f),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(60.dp)
                    // ⚡ FIX: Use offset to lift button safely
                    .offset(y = (-10).dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(Icons.Default.Calculate, "Calculate ROI", modifier = Modifier.size(20.dp))
                    Text(
                        "ROI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            NavyugaExposedDropdown(
                "Country",
                viewModel.countries,
                selectedCountry
            ) { selectedCountry = it }
            Spacer(modifier = Modifier.height(16.dp))
            NavyugaExposedDropdown("City", viewModel.cities, selectedCity) { selectedCity = it }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { navController.navigate("search_results/$selectedCountry/$selectedCity") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandBlue,
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
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            readOnly = true,
            value = selected,
            onValueChange = {},
            label = { Text(label, color = Color.White.copy(0.7f)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF1E293B),
                unfocusedContainerColor = Color(0xFF1E293B),
                focusedBorderColor = Color(0xFF60A5FA),
                unfocusedBorderColor = Color.White.copy(0.2f)
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF1E293B)).fillMaxWidth()
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(selectionOption, style = MaterialTheme.typography.bodyLarge, color = Color.White, modifier = Modifier.padding(vertical = 4.dp))
                    },
                    onClick = { onSelectionChange(selectionOption); expanded = false },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}