package com.example.mahayuga.feature.assetmanager.presentation.fundraising

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.NumberFormat
import java.util.Locale

// --- THEME ---
private val FundBg = Color(0xFF061123)
private val FundCard = Color(0xFF111c30)
private val FundBlue = Color(0xFF3B82F6) // Capital
private val FundOrange = Color(0xFFF97316) // Liquidity
private val TextWhite = Color.White
private val TextGrey = Color(0xFF8B9BB4)

@Composable
fun FundraisingScreen(
    viewModel: FundraisingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundBg)
            .padding(16.dp)
    ) {
        // Header
        Text("Capital & Liquidity", style = MaterialTheme.typography.headlineMedium, color = TextWhite, fontWeight = FontWeight.Bold)
        Text("Fundraising Status & Secondary Market", style = MaterialTheme.typography.bodyMedium, color = TextGrey)

        Spacer(modifier = Modifier.height(24.dp))

        // 1. ACTIVE RAISE CARD
        state.activeFund?.let { fund ->
            Card(
                colors = CardDefaults.cardColors(containerColor = FundCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("ACTIVE ROUND", color = FundBlue, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${fund.daysLeft} Days Left", color = TextWhite, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(fund.name, style = MaterialTheme.typography.titleLarge, color = TextWhite, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress Bar
                    val progress = (fund.raisedAmount / fund.targetAmount).toFloat()
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = FundBlue,
                        trackColor = Color(0xFF1E293B),
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(formatFundK(fund.raisedAmount) + " Raised", color = TextWhite, fontWeight = FontWeight.Bold)
                        Text("Target: " + formatFundK(fund.targetAmount), color = TextGrey)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    // Soft vs Hard Money
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatItem("Hard Commit", formatFundK(fund.hardCommitment), TextWhite)
                        StatItem("Soft Commit", formatFundK(fund.softCommitment), TextGrey)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. LIQUIDITY REQUESTS (SECONDARY MARKET)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Liquidity Queue (Exits)", style = MaterialTheme.typography.titleMedium, color = TextWhite, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text("Demand: ${formatFundK(state.totalLiquidityDemand)}", color = FundOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.liquidityQueue) { req ->
                LiquidityCard(req)
            }
        }
    }
}

@Composable
fun LiquidityCard(req: LiquidityRequest) {
    Card(
        colors = CardDefaults.cardColors(containerColor = FundCard),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(req.assetName, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Seller: ${req.investorName} • ${req.requestDate}", color = TextGrey, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatFundK(req.amount), color = FundOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(req.urgency.uppercase() + " PRIORITY", color = if(req.urgency=="High") FundOrange else TextGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column {
        Text(label, color = TextGrey, fontSize = 10.sp)
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

fun formatFundK(amount: Double): String {
    return when {
        amount >= 10000000 -> String.format("₹%.2f Cr", amount / 10000000)
        amount >= 100000 -> String.format("₹%.2f L", amount / 100000)
        else -> "₹${amount.toInt()}"
    }
}