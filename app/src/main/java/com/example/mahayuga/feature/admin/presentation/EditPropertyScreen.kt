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

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Commercial") }
    var status by remember { mutableStateOf("Open") }
    var isTrendingSelection by remember { mutableStateOf("No") }

    // ⚡ NEW: Asset Manager State
    var assetManager by remember { mutableStateOf("") }

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

    var exitPrice by remember { mutableStateOf("") }
    var totalProfit by remember { mutableStateOf("") }

    var escalationPercent by remember { mutableStateOf("") }
    var escalationYears by remember { mutableStateOf("") }

    var keptImages by remember { mutableStateOf<List<String>>(emptyList()) }
    var newImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var originalProperty by remember { mutableStateOf<PropertyModel?>(null) }

    LaunchedEffect(propertyId, propertiesState) {
        if (propertiesState is UiState.Success) {
            val property = (propertiesState as UiState.Success).data.find { it.id == propertyId }
            property?.let {
                originalProperty = it
                title = it.title
                description = it.description
                type = it.type
                status = it.status
                isTrendingSelection = if (it.isTrending) "Yes" else "No"
                // ⚡ Load Asset Manager
                assetManager = it.assetManager

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
                exitPrice = it.exitPrice
                totalProfit = it.totalProfit

                val regex = """(\d+)% \(Every (\d+) Years\)""".toRegex()
                val match = regex.find(it.escalation)
                if (match != null) {
                    escalationPercent = match.groupValues[1]
                    escalationYears = match.groupValues[2]
                }

                keptImages = it.imageUrls
            }
        }
    }

    // Auto-calculate logic (kept same as before)
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

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) { uris ->
            newImageUris = newImageUris + uris
        }

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
            // IMAGES SECTION (Unchanged)
            Text("Property Images", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

            SectionHeader("Basic Info")
            NavyugaTextField(
                value = title,
                onValueChange = { title = it },
                label = "Title",
                icon = Icons.Default.Title
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ⚡ NEW: Editable Asset Manager Field
            NavyugaTextField(
                value = assetManager,
                onValueChange = { assetManager = it },
                label = "Asset Manager Name",
                icon = Icons.Default.PersonOutline
            )

            Spacer(modifier = Modifier.height(8.dp))
            NavyugaTextField(
                value = address,
                onValueChange = { address = it },
                label = "Address",
                icon = Icons.Default.Place
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) {
                    NavyugaTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = "City",
                        icon = Icons.Default.LocationCity
                    )
                }
                Box(Modifier.weight(1f)) {
                    NavyugaTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = "State",
                        icon = Icons.Default.Map
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            NavyugaDropdown(
                label = "Asset Type",
                options = listOf("Office", "Retail", "Warehouse", "Industrial"),
                selected = type,
                onSelectionChange = { type = it })
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) {
                    NavyugaDropdown(
                        label = "Status",
                        options = listOf("Funding", "Funded", "Exited"),
                        selected = status,
                        onSelectionChange = { status = it })
                }
                Box(Modifier.weight(1f)) {
                    NavyugaDropdown(
                        label = "Make Trending?",
                        options = listOf("Yes", "No"),
                        selected = isTrendingSelection,
                        onSelectionChange = { isTrendingSelection = it })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (status == "Exited") {
                SectionHeader("Exit Details")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) {
                        NavyugaTextField(
                            value = exitPrice,
                            onValueChange = { exitPrice = it },
                            label = "Exit Price",
                            icon = Icons.Default.MonetizationOn,
                            isNumber = true
                        )
                    }
                    Box(Modifier.weight(1f)) {
                        NavyugaTextField(
                            value = totalProfit,
                            onValueChange = { totalProfit = it },
                            label = "Total Profit",
                            icon = Icons.Default.TrendingUp,
                            isNumber = true
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            SectionHeader("Specifications")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) {
                    NavyugaTextField(
                        value = area,
                        onValueChange = { area = it },
                        label = "Area (sqft)",
                        icon = Icons.Default.SquareFoot,
                        isNumber = true
                    )
                }
                Box(Modifier.weight(1f)) {
                    NavyugaTextField(
                        value = floor,
                        onValueChange = { floor = it },
                        label = "Floor",
                        icon = Icons.Default.Layers
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) {
                    NavyugaTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = "Age",
                        icon = Icons.Default.DateRange,
                        isNumber = true
                    )
                }
                Box(Modifier.weight(1f)) {
                    NavyugaTextField(
                        value = carPark,
                        onValueChange = { carPark = it },
                        label = "Car Park",
                        icon = Icons.Default.DirectionsCar
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader("Lease Information")
            NavyugaTextField(
                value = tenantName,
                onValueChange = { tenantName = it },
                label = "Tenant Name",
                icon = Icons.Default.Person
            )
            Spacer(modifier = Modifier.height(8.dp))
            NavyugaTextField(
                value = occupationPeriod,
                onValueChange = { occupationPeriod = it },
                label = "Occupation Period (Years)",
                icon = Icons.Default.Timer,
                isNumber = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) {
                    NavyugaTextField(
                        value = escalationPercent,
                        onValueChange = { escalationPercent = it },
                        label = "Escalation %",
                        icon = Icons.Default.TrendingUp,
                        isNumber = true
                    )
                }
                Box(Modifier.weight(1f)) {
                    NavyugaTextField(
                        value = escalationYears,
                        onValueChange = { escalationYears = it },
                        label = "Every X Years",
                        icon = Icons.Default.Update,
                        isNumber = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader("Financials")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) {
                    NavyugaTextField(
                        value = totalValuation,
                        onValueChange = { totalValuation = it },
                        label = "Valuation (₹)",
                        icon = Icons.Default.MonetizationOn,
                        isNumber = true
                    )
                }
                Box(Modifier.weight(1f)) {
                    NavyugaTextField(
                        value = minInvest,
                        onValueChange = { minInvest = it },
                        label = "Min Invest",
                        icon = Icons.Default.AttachMoney,
                        isNumber = true
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) {
                    NavyugaTextField(
                        value = monthlyRent,
                        onValueChange = { monthlyRent = it },
                        label = "Monthly Rent",
                        icon = Icons.Default.Payments,
                        isNumber = true
                    )
                }
                Box(Modifier.weight(1f)) {
                    NavyugaTextField(
                        value = grossAnnualRent,
                        onValueChange = { grossAnnualRent = it },
                        label = "Gross Annual",
                        icon = Icons.Default.AccountBalanceWallet,
                        isNumber = true
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) {
                    NavyugaTextField(
                        value = annualPropertyTax,
                        onValueChange = { annualPropertyTax = it },
                        label = "Annual Tax",
                        icon = Icons.Default.ReceiptLong,
                        isNumber = true
                    )
                }
                Box(Modifier.weight(1f)) {
                    NavyugaTextField(
                        value = roi,
                        onValueChange = { roi = it },
                        label = "ROI %",
                        icon = Icons.Default.TrendingUp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            NavyugaTextField(
                value = fundedPercent,
                onValueChange = { fundedPercent = it },
                label = "Funded %",
                icon = Icons.Default.PieChart,
                isNumber = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            NavyugaGradientButton(
                text = "Update Property",
                isLoading = uploadState is UiState.Loading,
                onClick = {
                    originalProperty?.let { orig ->
                        val finalEscalation =
                            if (escalationPercent.isNotEmpty()) "$escalationPercent (Every $escalationYears Years)" else ""

                        val updatedFields = orig.copy(
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
                            roi = roi.toDoubleOrNull() ?: 0.0,
                            fundedPercent = fundedPercent.toIntOrNull() ?: 0,
                            monthlyRent = monthlyRent,
                            grossAnnualRent = grossAnnualRent,
                            annualPropertyTax = annualPropertyTax,
                            tenantName = tenantName,
                            occupationPeriod = occupationPeriod,
                            escalation = finalEscalation,
                            exitPrice = exitPrice,
                            totalProfit = totalProfit,
                            isTrending = isTrendingSelection == "Yes",
                            // ⚡ UPDATE ASSET MANAGER
                            assetManager = assetManager
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