package com.example.mahayuga.feature.admin.presentation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.auth.presentation.components.NavyugaGradientButton
import com.example.mahayuga.feature.auth.presentation.components.NavyugaTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Office") }
    var status by remember { mutableStateOf("Funding") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }

    var age by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("") }
    var carPark by remember { mutableStateOf("") }

    var tenantName by remember { mutableStateOf("") }
    var occupationPeriod by remember { mutableStateOf("") }

    var escalationPercent by remember { mutableStateOf("") }
    var escalationYears by remember { mutableStateOf("") }

    var totalValuation by remember { mutableStateOf("") }
    var minInvest by remember { mutableStateOf("") }
    var monthlyRent by remember { mutableStateOf("") }
    var annualPropertyTax by remember { mutableStateOf("") }
    var fundedPercent by remember { mutableStateOf("") }

    // ⚡ NEW: Exited Property Fields
    var exitPrice by remember { mutableStateOf("") }
    var totalProfit by remember { mutableStateOf("") }

    var roi by remember { mutableStateOf("") }
    var grossAnnualRent by remember { mutableStateOf("") }

    // Auto-Calculation Coroutine
    LaunchedEffect(monthlyRent, totalValuation, annualPropertyTax) {
        val rent = monthlyRent.replace(",", "").toDoubleOrNull() ?: 0.0
        val price = totalValuation.replace(",", "").toDoubleOrNull() ?: 0.0
        val tax = annualPropertyTax.replace(",", "").toDoubleOrNull() ?: 0.0

        if (rent > 0) {
            val calculatedAnnualRent = rent * 12
            grossAnnualRent = String.format("%.0f", calculatedAnnualRent)

            if (price > 0) {
                val netIncome = calculatedAnnualRent - tax
                val calculatedRoi = (netIncome / price) * 100
                roi = String.format("%.2f", calculatedRoi)
            }
        }
    }

    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val uploadState by viewModel.propertyUploadState.collectAsState()
    val context = LocalContext.current

    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris -> if (uris.isNotEmpty()) selectedImageUris = uris }

    LaunchedEffect(uploadState) {
        if (uploadState is UiState.Success) {
            Toast.makeText(context, "Property Published!", Toast.LENGTH_LONG).show()
            viewModel.resetUploadState()
            navController.popBackStack()
        } else if (uploadState is UiState.Failure) {
            Toast.makeText(context, (uploadState as UiState.Failure).message, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("List New Asset") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // IMAGES
            if (selectedImageUris.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                        .clickable { multiplePhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, null, tint = MaterialTheme.colorScheme.primary)
                            Text("Add Photos (Max 10)")
                        }
                    }
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(120.dp).padding(vertical = 8.dp)) {
                    items(selectedImageUris) { uri ->
                        AsyncImage(model = uri, contentDescription = null, modifier = Modifier.width(160.dp).fillMaxHeight(), contentScale = ContentScale.Crop)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader("Property Details")
            NavyugaTextField(value = title, onValueChange = { title = it }, label = "Property Name", icon = Icons.Default.Apartment)
            Spacer(modifier = Modifier.height(8.dp))
            NavyugaTextField(value = address, onValueChange = { address = it }, label = "Address", icon = Icons.Default.Place)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { NavyugaTextField(value = city, onValueChange = { city = it }, label = "City", icon = Icons.Default.LocationCity) }
                Box(Modifier.weight(1f)) { NavyugaTextField(value = state, onValueChange = { state = it }, label = "State", icon = Icons.Default.Map) }
            }
            Spacer(modifier = Modifier.height(8.dp))

            NavyugaDropdown(label = "Asset Type", options = listOf("Office", "Retail", "Warehouse", "Industrial"), selected = type, onSelectionChange = { type = it })
            Spacer(modifier = Modifier.height(8.dp))

            // ⚡ STATUS SELECTION - TRIGGERS EXITED LOGIC
            NavyugaDropdown(label = "Status", options = listOf("Funding", "Funded", "Exited"), selected = status, onSelectionChange = { status = it })

            Spacer(modifier = Modifier.height(16.dp))

            // ⚡ EXITED PROPERTY FIELDS
            if (status == "Exited") {
                SectionHeader("Exit Details")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) {
                        NavyugaTextField(value = exitPrice, onValueChange = { exitPrice = it }, label = "Exit Price", icon = Icons.Default.MonetizationOn, isNumber = true)
                    }
                    Box(Modifier.weight(1f)) {
                        NavyugaTextField(value = totalProfit, onValueChange = { totalProfit = it }, label = "Total Profit", icon = Icons.Default.TrendingUp, isNumber = true)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            SectionHeader("Specifications")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { NavyugaTextField(value = area, onValueChange = { area = it }, label = "Area (Sq Ft)", icon = Icons.Default.SquareFoot, isNumber = true) }
                Box(Modifier.weight(1f)) { NavyugaTextField(value = floor, onValueChange = { floor = it }, label = "Floor", icon = Icons.Default.Layers) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { NavyugaTextField(value = age, onValueChange = { age = it }, label = "Age of Building", icon = Icons.Default.DateRange, isNumber = true) }
                Box(Modifier.weight(1f)) { NavyugaTextField(value = carPark, onValueChange = { carPark = it }, label = "Car Park", icon = Icons.Default.DirectionsCar) }
            }
            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader("Lease Information")
            NavyugaTextField(value = tenantName, onValueChange = { tenantName = it }, label = "Tenant Name", icon = Icons.Default.Person)
            Spacer(modifier = Modifier.height(8.dp))
            // ⚡ CHANGED TO YEARS
            NavyugaTextField(value = occupationPeriod, onValueChange = { occupationPeriod = it }, label = "Occupation Period (Years)", icon = Icons.Default.Timer, isNumber = true)

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { NavyugaTextField(value = escalationPercent, onValueChange = { escalationPercent = it }, label = "Escalation %", icon = Icons.Default.TrendingUp, isNumber = true) }
                Box(Modifier.weight(1f)) { NavyugaTextField(value = escalationYears, onValueChange = { escalationYears = it }, label = "Every X Years", icon = Icons.Default.Update, isNumber = true) }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader("Financial Analysis")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { NavyugaTextField(value = totalValuation, onValueChange = { totalValuation = it }, label = "Price (Total)", icon = Icons.Default.MonetizationOn, isNumber = true) }
                Box(Modifier.weight(1f)) { NavyugaTextField(value = minInvest, onValueChange = { minInvest = it }, label = "Min Invest", icon = Icons.Default.AttachMoney, isNumber = true) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { NavyugaTextField(value = monthlyRent, onValueChange = { monthlyRent = it }, label = "Monthly Rent", icon = Icons.Default.Payments, isNumber = true) }
                Box(Modifier.weight(1f)) { NavyugaTextField(value = grossAnnualRent, onValueChange = { grossAnnualRent = it }, label = "Gross Annual (Auto)", icon = Icons.Default.AccountBalanceWallet, isNumber = true) }
            }//////hiiii i am here
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { NavyugaTextField(value = annualPropertyTax, onValueChange = { annualPropertyTax = it }, label = "Annual Tax", icon = Icons.Default.ReceiptLong, isNumber = true) }
                Box(Modifier.weight(1f)) { NavyugaTextField(value = roi, onValueChange = { roi = it }, label = "ROI % (Auto)", icon = Icons.Default.Percent, isNumber = true) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            NavyugaTextField(value = fundedPercent, onValueChange = { fundedPercent = it }, label = "Funded % (0-100)", icon = Icons.Default.PieChart, isNumber = true)

            Spacer(modifier = Modifier.height(32.dp))

            NavyugaGradientButton(
                text = if (uploadState is UiState.Loading) "Uploading..." else "Publish Live",
                isLoading = uploadState is UiState.Loading,
                onClick = {
                    if (title.isNotEmpty() && totalValuation.isNotEmpty()) {
                        val finalEscalation = if (escalationPercent.isNotEmpty()) "$escalationPercent (Every $escalationYears Years)" else ""

                        viewModel.listNewProperty(
                            title = title, description = description, type = type, status = status,
                            address = address, city = city, state = state,
                            age = age, area = area, floor = floor, carPark = carPark,
                            totalValuation = totalValuation, minInvest = minInvest, roi = roi.toDoubleOrNull() ?: 0.0, fundedPercent = fundedPercent.toIntOrNull() ?: 0,
                            monthlyRent = monthlyRent, grossAnnualRent = grossAnnualRent, annualPropertyTax = annualPropertyTax,
                            tenantName = tenantName, occupationPeriod = occupationPeriod,
                            escalation = finalEscalation,
                            // ⚡ PASS NEW EXITED FIELDS
                            exitPrice = exitPrice, totalProfit = totalProfit,
                            imageUris = selectedImageUris
                        )
                    } else {
                        Toast.makeText(context, "Fill required fields", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}