package com.example.mahayuga.feature.admin.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.admin.data.model.InvestmentModel
import com.example.mahayuga.feature.auth.data.model.UserModel
import com.example.mahayuga.ui.theme.BrandBlue
import com.example.mahayuga.ui.theme.ErrorRed
import com.example.mahayuga.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserDetailScreen(
    navController: NavController,
    userId: String,
    viewModel: AdminViewModel = hiltViewModel()
) {
    // Load data on entry
    LaunchedEffect(userId) {
        viewModel.fetchInvestmentsForUser(userId)
    }

    val context = LocalContext.current
    val usersState by viewModel.usersState.collectAsState()
    val investmentsState by viewModel.selectedUserInvestments.collectAsState()

    // Find the specific user from the list
    val user = (usersState as? UiState.Success)?.data?.find { it.uid == userId }

    // State for Delete Warning Dialog
    var showDeleteUserDialog by remember { mutableStateOf(false) }

    // Listen for delete operations
    LaunchedEffect(Unit) {
        viewModel.deleteOperationState.collect { state ->
            when(state) {
                is UiState.Success -> {
                    Toast.makeText(context, state.data, Toast.LENGTH_SHORT).show()
                    if (state.data.contains("User")) {
                        navController.popBackStack() // Go back if user deleted
                    }
                }
                is UiState.Failure -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    if (user == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(user.name.ifEmpty { "User Details" }) },
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
        LazyColumn(
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. User Profile Card
            item {
                UserProfileCard(user)
            }

            // 2. Danger Zone (Delete User)
            item {
                Button(
                    onClick = { showDeleteUserDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed.copy(alpha = 0.1f), contentColor = ErrorRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Default.DeleteForever, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete User Permanently")
                }
            }

            item { Divider(color = MaterialTheme.colorScheme.outlineVariant) }

            item {
                Text(
                    "Portfolio & Investments",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // 3. Investment List
            when (investmentsState) {
                is UiState.Loading -> item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                is UiState.Success -> {
                    val investments = (investmentsState as UiState.Success<List<InvestmentModel>>).data
                    if (investments.isEmpty()) {
                        item {
                            Text("No investments found for this user.", color = Color.Gray, modifier = Modifier.padding(8.dp))
                        }
                    } else {
                        items(investments) { inv ->
                            AdminInvestmentItem(inv, onDelete = { viewModel.deleteSingleInvestment(inv) })
                        }
                    }
                }
                is UiState.Failure -> item { Text("Failed to load investments", color = ErrorRed) }
                else -> {}
            }
        }
    }

    // DELETE CONFIRMATION DIALOG
    if (showDeleteUserDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteUserDialog = false },
            icon = { Icon(Icons.Default.Warning, null, tint = ErrorRed) },
            title = { Text("Delete User?") },
            text = {
                Text("This action CANNOT be undone.\n\nIt will:\n1. Revert all funding from properties.\n2. Delete all investment records.\n3. Delete the user profile forever.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUserPermanently(user.uid)
                        showDeleteUserDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Yes, Delete Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteUserDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun UserProfileCard(user: UserModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = BrandBlue, modifier = Modifier.size(50.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(user.name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(user.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(Modifier.width(4.dp))
                        Text(user.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("Total Invested", "₹${user.totalInvestment}", BrandBlue)
                StatItem("Current Value", "₹${user.currentValue}", SuccessGreen)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("Total Area", "${String.format("%.1f", user.totalArea)} Sqft", Color.Gray)
                StatItem("Monthly Rent", "₹${user.totalRent}", Color.Gray)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun AdminInvestmentItem(inv: InvestmentModel, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(inv.propertyTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)

                    // ⚡ NEW: Display Asset ID if available
                    if (inv.assetId.isNotEmpty()) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "[${inv.assetId}]",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text("Invested: ₹${inv.amount}", color = SuccessGreen, style = MaterialTheme.typography.bodySmall)
                Text("Ref: ${inv.paymentReference}", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.DeleteForever, "Delete Investment", tint = ErrorRed)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Reverse Investment?") },
            text = { Text("This will subtract ₹${inv.amount} from the property funding and the user's portfolio.") },
            confirmButton = {
                Button(onClick = { onDelete(); showDeleteDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)) {
                    Text("Reverse Transaction")
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }
}