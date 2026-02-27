// main/java/com/example/mahayuga/feature/navyuga/presentation/trade/TradeScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.trade

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val NavyBlue = Color(0xFF0F172A)
private val CardDark = Color(0xFF1E293B)
private val BrandBlue = Color(0xFF2979FF)
private val GreenPositive = Color(0xFF00E676)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeScreen(navController: NavController) {
    var selectedFilter by remember { mutableStateOf("Buy") }

    Scaffold(
        containerColor = NavyBlue,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyBlue)
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trade",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Icon(Icons.Default.Notifications, "Notif", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Buy / Sell Toggle
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    FilterChip(
                        selected = selectedFilter == "Buy",
                        onClick = { selectedFilter = "Buy" },
                        label = {
                            Text("Buy", modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandBlue, selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    FilterChip(
                        selected = selectedFilter == "Sell",
                        onClick = { selectedFilter = "Sell" },
                        label = {
                            Text("Sell", modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFF5252), selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Publicly Listed Assets (REITs)", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // List of REITs
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    ReitListItem(
                        name = "Embassy REIT",
                        price = "₹410.50",
                        change = "+0.62%",
                        onClick = { navController.navigate("trade_asset_detail/embassy_reit") }
                    )
                }
                item {
                    ReitListItem(
                        name = "Mindspace REIT",
                        price = "₹320.10",
                        change = "+1.20%",
                        onClick = { navController.navigate("trade_asset_detail/mindspace_reit") }
                    )
                }
                item {
                    ReitListItem(
                        name = "Brookfield India REIT",
                        price = "₹255.75",
                        change = "-0.40%",
                        isPositive = false,
                        onClick = { navController.navigate("trade_asset_detail/brookfield_reit") }
                    )
                }
            }
        }
    }
}

@Composable
fun ReitListItem(name: String, price: String, change: String, isPositive: Boolean = true, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Real Estate Investment Trust", color = Color.Gray, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(price, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(change, color = if (isPositive) GreenPositive else Color(0xFFFF5252), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}