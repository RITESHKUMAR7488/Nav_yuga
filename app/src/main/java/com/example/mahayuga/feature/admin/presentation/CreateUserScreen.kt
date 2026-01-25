package com.example.mahayuga.feature.admin.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.auth.data.model.AssetManagerModel
import com.example.mahayuga.feature.auth.data.model.UserModel
import com.example.mahayuga.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val investorRequests by viewModel.requestsState.collectAsState()
    val partnerRequests by viewModel.amRequestsState.collectAsState()

    // Tab State: 0 = Investors, 1 = Partners
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Approve Requests") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {

            // --- TABS ---
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = BrandBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Investors") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Partners (AM)") }
                )
            }

            // --- CONTENT ---
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {
                if (selectedTab == 0) {
                    InvestorList(investorRequests, viewModel)
                } else {
                    PartnerList(partnerRequests, viewModel)
                }
            }
        }
    }
}

@Composable
fun InvestorList(state: UiState<List<UserModel>>, viewModel: AdminViewModel) {
    when (state) {
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Failure -> Text("Error: ${state.message}", color = ErrorRed)
        is UiState.Success -> {
            val requests = state.data
            if (requests.isEmpty()) {
                Text("No Pending Investor Requests", color = Color.Gray)
            } else {
                LazyColumn {
                    items(requests) { user ->
                        RequestItem(
                            title = user.name,
                            subtitle = "${user.email}\nDOB: ${user.dob}",
                            onApprove = { role -> viewModel.approveUser(user.uid, role) },
                            onReject = { viewModel.rejectUser(user.uid) },
                            isInvestor = true
                        )
                    }
                }
            }
        }

        else -> {}
    }
}

@Composable
fun PartnerList(state: UiState<List<AssetManagerModel>>, viewModel: AdminViewModel) {
    when (state) {
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Failure -> Text("Error: ${state.message}", color = ErrorRed)
        is UiState.Success -> {
            val requests = state.data
            if (requests.isEmpty()) {
                Text("No Pending Partner Applications", color = Color.Gray)
            } else {
                LazyColumn {
                    items(requests) { am ->
                        RequestItem(
                            title = am.entityLegalName,
                            subtitle = "Brand: ${am.brandName}\nContact: ${am.contactName} (${am.mobile})\nAUM: ${am.aumRange}",
                            onApprove = { viewModel.approveAssetManager(am.uid) },
                            onReject = { viewModel.rejectAssetManager(am.uid) },
                            isInvestor = false
                        )
                    }
                }
            }
        }

        else -> {}
    }
}

@Composable
fun RequestItem(
    title: String,
    subtitle: String,
    onApprove: (String) -> Unit,
    onReject: () -> Unit,
    isInvestor: Boolean
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isInvestor) Icons.Default.Person else Icons.Default.Business,
                    contentDescription = null,
                    tint = BrandBlue
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (isInvestor) showDialog = true else onApprove("asset_manager")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Approve")
                }

                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                ) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reject")
                }
            }
        }
    }

    if (showDialog && isInvestor) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select Role") },
            text = { Text("Approve as:") },
            confirmButton = {
                Button(onClick = { onApprove("user"); showDialog = false }) {
                    Text("User")
                }
            },
            dismissButton = {
                Button(
                    onClick = { onApprove("admin"); showDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("Admin")
                }
            }
        )
    }
}