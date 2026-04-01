package com.example.mahayuga.feature.admin.presentation

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.ui.theme.* // ⚡ UPDATED IMPORT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSelectUserScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val usersState by viewModel.usersState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Step 1: Select User") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by name or email...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = usersState) {
                is UiState.Success -> {
                    val filteredUsers = state.data.filter {
                        it.name.contains(searchQuery, true) || it.email.contains(searchQuery, true)
                    }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filteredUsers) { user ->
                            AdminUserItem(
                                user = user,
                                onClick = {
                                    viewModel.selectUserForInvestment(user)
                                    navController.navigate("admin_inv_select_property")
                                }
                            )
                        }
                    }
                }

                is UiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                is UiState.Failure -> Text(
                    "Error loading users",
                    color = BricxDangerRed
                ) // ⚡ UPDATED
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSelectPropertyScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val propertiesState by viewModel.propertiesState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Step 2: Select Property") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)) {
            Text(
                "Investing for: ${viewModel.selectedUser?.name}",
                style = MaterialTheme.typography.labelLarge,
                color = BricxBrandBlue // ⚡ UPDATED
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search property...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = propertiesState) {
                is UiState.Success -> {
                    val filteredProps = state.data.filter { it.title.contains(searchQuery, true) }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(filteredProps) { property ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectPropertyForInvestment(property)
                                        navController.navigate("admin_inv_form")
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            property.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (property.assetId.isNotEmpty()) {
                                            Text(
                                                "ID: ${property.assetId}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = BricxBrandBlue
                                            ) // ⚡ UPDATED
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Valuation: ₹${property.totalValuation}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = BricxBrandBlue
                                        ) // ⚡ UPDATED
                                        Text(
                                            "Funded: ₹${property.totalFunding}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = BricxSuccessGreen
                                        ) // ⚡ UPDATED
                                    }
                                }
                            }
                        }
                    }
                }

                is UiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminInvestmentFormScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var amount by remember { mutableStateOf("") }
    val uploadState by viewModel.propertyUploadState.collectAsState()
    val paymentModes = listOf("CASH", "CHEQUE", "ONLINE")
    var selectedMode by remember { mutableStateOf("CASH") }
    var referenceNumber by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.investmentStatus.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            if (msg.contains("Success")) {
                navController.navigate("admin_dashboard") {
                    popUpTo("admin_dashboard") {
                        inclusive = true
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Step 3: Register Investment") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BricxBrandBlue.copy(alpha = 0.1f)), // ⚡ UPDATED
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = BricxBrandBlue) // ⚡ UPDATED
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Investor", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    viewModel.selectedUser?.name ?: "Unknown User",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = BricxBrandBlue.copy(alpha = 0.2f)
                        ) // ⚡ UPDATED
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HomeWork, null, tint = BricxBrandBlue) // ⚡ UPDATED
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Property", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    viewModel.selectedProperty?.title ?: "Unknown Property",
                                    fontWeight = FontWeight.Bold
                                )
                                if (!viewModel.selectedProperty?.assetId.isNullOrEmpty()) {
                                    Text(
                                        "ID: ${viewModel.selectedProperty?.assetId}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BricxBrandBlue
                                    ) // ⚡ UPDATED
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.all { char -> char.isDigit() }) amount = it },
                    label = { Text("Investment Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BricxBrandBlue,
                        focusedLabelColor = BricxBrandBlue
                    ) // ⚡ UPDATED
                )

                Text("Payment Method", fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    paymentModes.forEach { mode ->
                        FilterChip(
                            selected = selectedMode == mode,
                            onClick = { selectedMode = mode },
                            label = { Text(mode) },
                            leadingIcon = {
                                if (selectedMode == mode) Icon(
                                    Icons.Default.Check,
                                    null
                                )
                            }
                        )
                    }
                }

                if (selectedMode != "CASH") {
                    OutlinedTextField(
                        value = referenceNumber, onValueChange = { referenceNumber = it },
                        label = { Text(if (selectedMode == "CHEQUE") "Cheque Number" else "Transaction ID") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BricxSuccessGreen,
                            focusedLabelColor = BricxSuccessGreen
                        ) // ⚡ UPDATED
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (amount.isNotEmpty()) {
                            viewModel.submitInvestment(
                                amount.toLong(),
                                selectedMode,
                                if (selectedMode == "CASH") "Cash Deposit" else referenceNumber
                            )
                        } else {
                            Toast.makeText(context, "Enter valid amount", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BricxBrandBlue), // ⚡ UPDATED
                    shape = RoundedCornerShape(12.dp), enabled = uploadState !is UiState.Loading
                ) {
                    if (uploadState is UiState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("CONFIRM INVESTMENT", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}