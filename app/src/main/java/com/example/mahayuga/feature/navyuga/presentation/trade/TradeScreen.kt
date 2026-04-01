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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mahayuga.feature.navyuga.domain.model.MarketQuote
import com.example.mahayuga.feature.navyuga.domain.repository.MarketRepository
import com.example.mahayuga.ui.theme.* // ⚡ IMPORTED BRICX THEME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TradeViewModel @Inject constructor(
    private val marketRepository: MarketRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<List<MarketQuote>?>(null)
    val uiState: StateFlow<List<MarketQuote>?> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            marketRepository.getLiveQuotesFlow(listOf("EMBASSY", "MINDSPACE", "BIRET"))
                .collect { quotes ->
                    _uiState.value = quotes
                }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeScreen(
    navController: NavController,
    viewModel: TradeViewModel = hiltViewModel()
) {
    var selectedFilter by remember { mutableStateOf("Buy") }
    val liveQuotes by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = BricxBackground, // ⚡ UPDATED
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BricxBackground) // ⚡ UPDATED
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trade",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = BricxTextPrimary // ⚡ UPDATED
                )
                Icon(Icons.Default.Notifications, "Notif", tint = BricxTextPrimary) // ⚡ UPDATED
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    FilterChip(
                        selected = selectedFilter == "Buy",
                        onClick = { selectedFilter = "Buy" },
                        label = {
                            Text(
                                "Buy",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BricxBrandBlue, // ⚡ UPDATED
                            selectedLabelColor = BricxTextPrimary // ⚡ UPDATED
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
                                textAlign = TextAlign.Center
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BricxDangerRed, // ⚡ UPDATED
                            selectedLabelColor = BricxTextPrimary // ⚡ UPDATED
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
                color = BricxTextPrimary, // ⚡ UPDATED
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (liveQuotes == null || liveQuotes!!.isEmpty()) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BricxBrandBlue) // ⚡ UPDATED
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(liveQuotes!!) { quote ->
                        val cleanName = when (quote.symbol) {
                            "MINDSPACE" -> "Mindspace Business Parks"
                            "EMBASSY" -> "Embassy Office Parks"
                            "BIRET" -> "Brookfield India Trust"
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
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard), // ⚡ UPDATED
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            BricxBorder
        ) // ⚡ ADDED BORDER FOR CONSISTENCY
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    name,
                    color = BricxTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ) // ⚡ UPDATED
                Text(
                    "Real Estate Investment Trust ⭐",
                    color = BricxTextSecondary,
                    fontSize = 12.sp
                ) // ⚡ UPDATED
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    price,
                    color = BricxTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ) // ⚡ UPDATED
                Text(
                    change,
                    color = if (isPositive) BricxSuccessGreen else BricxDangerRed, // ⚡ UPDATED
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}