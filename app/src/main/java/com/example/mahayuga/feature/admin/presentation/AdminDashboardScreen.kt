package com.example.mahayuga.feature.admin.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    navController: NavController,
    onLogout: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val usersState by viewModel.usersState.collectAsState()
    val propertiesState by viewModel.propertiesState.collectAsState()

    // ⚡ REAL STATS CALCULATION
    val activePropertiesCount = if (propertiesState is UiState.Success) {
        (propertiesState as UiState.Success).data.count { it.status != "Exited" }.toString()
    } else "..."

    val totalUsersCount = if (usersState is UiState.Success) {
        (usersState as UiState.Success).data.size.toString()
    } else "..."

    // Calculate Total Volume (Sum of totalValuation of all properties)
    val totalVolume = if (propertiesState is UiState.Success) {
        val total = (propertiesState as UiState.Success).data.sumOf {
            it.totalValuation.replace(",", "").toDoubleOrNull() ?: 0.0
        }
        "₹${formatLargeNumber(total)}"
    } else "..."

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. HEADER
            Text(
                text = "Navyuga Admin",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Master Control Panel",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. STATS ROW (REAL DATA)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatCard("Active Props", activePropertiesCount, SuccessGreen, Modifier.weight(1f))
                AdminStatCard("Total Users", totalUsersCount, BrandBlue, Modifier.weight(1f))
                AdminStatCard("Asset Vol", totalVolume, CyanAccent, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. ACTION BUTTONS

            // List New Property
            AdminActionCard(
                title = "List New Property",
                subtitle = "Publish investment opportunities",
                icon = Icons.Default.AddHome,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                onClick = { navController.navigate("admin_add_property") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Manage Properties
            AdminActionCard(
                title = "Manage Properties",
                subtitle = "Edit prices, status, or delete",
                icon = Icons.Default.Edit,
                onClick = { navController.navigate("admin_manage_properties") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Manage Users
            AdminActionCard(
                title = "Manage Users",
                subtitle = "View investors, block accounts",
                icon = Icons.Default.Group,
                onClick = { navController.navigate("admin_manage_users") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Register Investment
            AdminActionCard(
                title = "Register Investment",
                subtitle = "Record offline payments",
                icon = Icons.Default.AttachMoney,
                onClick = { navController.navigate("investment_flow") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Create User
            AdminActionCard(
                title = "Create User / Admin",
                subtitle = "Add new staff or investors",
                icon = Icons.Default.PersonAdd,
                onClick = { navController.navigate("admin_create_user") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Logout
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
            ) {
                Icon(Icons.Default.Logout, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout System", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Helper to format Cr/L
fun formatLargeNumber(value: Double): String {
    return when {
        value >= 10000000 -> String.format("%.1fCr", value / 10000000)
        value >= 100000 -> String.format("%.1fL", value / 100000)
        else -> String.format("%.0f", value)
    }
}

@Composable
fun AdminStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(40.dp)) {
                CircularProgressIndicator(
                    progress = { 1f }, modifier = Modifier.fillMaxSize(), color = color.copy(alpha = 0.2f), strokeWidth = 3.dp
                )
                CircularProgressIndicator(
                    progress = { 0.7f }, modifier = Modifier.fillMaxSize(), color = color, strokeWidth = 3.dp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}

@Composable
fun AdminActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(contentColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = contentColor)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = contentColor)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = contentColor.copy(alpha = 0.7f))
            }

            Icon(Icons.Default.ChevronRight, null, tint = contentColor.copy(alpha = 0.5f))
        }
    }
}