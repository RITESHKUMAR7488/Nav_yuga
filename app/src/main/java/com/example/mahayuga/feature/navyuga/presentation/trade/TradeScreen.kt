package com.example.mahayuga.feature.navyuga.presentation.trade

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mahayuga.feature.navyuga.domain.model.MarketQuote
import com.example.mahayuga.feature.navyuga.domain.repository.MarketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

// --- VIEWMODEL ---
@HiltViewModel
class TradeViewModel @Inject constructor(
    private val marketRepository: MarketRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<List<MarketQuote>?>(null)
    val uiState: StateFlow<List<MarketQuote>?> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Fetch real data for Trade Screen
            val result =
                marketRepository.getLiveQuotes(listOf("EMBASSY.NS", "MINDSPACE.NS", "BIRET.NS"))
            result.onSuccess { _uiState.value = it }
        }
    }
}

// --- UI ---
private val NavyBlue = Color(0xFF0F172A)
private val CardDark = Color(0xFF1E293B)
private val BrandBlue = Color(0xFF2979FF)
private val GreenPositive = Color(0xFF00E676)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeScreen(
    navController: NavController,
    viewModel: TradeViewModel = hiltViewModel()
) {
    var selectedFilter by remember { mutableStateOf("Buy") }
    val liveQuotes by viewModel.uiState.collectAsState()

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
                            Text(
                                "Buy",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandBlue,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    FilterChip(
                        selected = selectedFilter == "Sell",
                        onClick = { selectedFilter = "Sell" },
                        label = {
                            Text(
                                "Sell",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(
                                0xFFFF5252
                            ), selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Publicly Listed Assets (REITs)",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (liveQuotes == null) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandBlue)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(liveQuotes!!) { quote ->
                        // Clean up Yahoo Finance Name
                        val cleanName = when (quote.symbol) {
                            "MINDSPACE.NS" -> "Mindspace Business Parks"
                            "EMBASSY.NS" -> "Embassy Office Parks"
                            "BIRET.NS" -> "Brookfield India Trust"
                            else -> quote.name.split(",")[0]
                        }

                        ReitListItem(
                            name = cleanName,
                            price = "₹${String.format(Locale.US, "%.2f", quote.currentPrice)}",
                            change = "${if (quote.isPositive) "+" else ""}${
                                String.format(
                                    Locale.US,
                                    "%.2f",
                                    quote.percentageChange
                                )
                            }%",
                            isPositive = quote.isPositive,
                            // ⚡ Pass the real symbol to the Detail screen to prevent crashes
                            onClick = { navController.navigate("trade_asset_detail/${quote.symbol}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReitListItem(
    name: String,
    price: String,
    change: String,
    isPositive: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                // ⭐ Marked as hardcoded context
                Text("Real Estate Investment Trust ⭐", color = Color.Gray, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(price, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    change,
                    color = if (isPositive) GreenPositive else Color(0xFFFF5252),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}