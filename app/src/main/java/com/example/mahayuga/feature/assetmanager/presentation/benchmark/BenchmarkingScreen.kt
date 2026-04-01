package com.example.mahayuga.feature.assetmanager.presentation.benchmark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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

private val BenchBg = Color(0xFF061123)
private val BenchCard = Color(0xFF111c30)
private val MyColor = Color(0xFF38a882) // Teal
private val MarketColor = Color(0xFF64748B) // Slate
private val Gold = Color(0xFFF59E0B)

@Composable
fun BenchmarkingScreen(
    viewModel: BenchmarkingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BenchBg)
            .padding(16.dp)
    ) {
        Text("Performance Benchmarking", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
        Text("Navyuga vs Market Average", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF8B9BB4))

        Spacer(modifier = Modifier.height(24.dp))

        // 1. RANK & TRUST
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Trust Score
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = BenchCard)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Trust Score", color = Color(0xFF8B9BB4), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${state.trustScore}", color = Gold, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text("Excellent", color = Gold, fontSize = 12.sp)
                }
            }
            // City Rank
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = BenchCard)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("City Rank", color = Color(0xFF8B9BB4), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("#${state.rank}", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text("Top 5% Manager", color = MyColor, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. METRICS COMPARISON
        Text("Metric Comparison", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).background(MyColor, CircleShape))
            Text(" You  ", color = Color(0xFF8B9BB4), fontSize = 12.sp)
            Box(Modifier.size(8.dp).background(MarketColor, CircleShape))
            Text(" Market", color = Color(0xFF8B9BB4), fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        state.metrics.forEach { metric ->
            BenchmarkRow(metric)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BenchmarkRow(metric: MarketMetric) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(metric.name, color = Color.White, fontSize = 14.sp)
            val diff = metric.myValue - metric.marketValue
            val sign = if (diff > 0) "+" else ""
            Text(
                "$sign${String.format("%.1f", diff)}${metric.unit}",
                color = if (diff >= 0) MyColor else Color(0xFFFF3B30),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Bars
        Row(verticalAlignment = Alignment.CenterVertically) {
            // My Bar
            Column(modifier = Modifier.weight(1f)) {
                LinearProgressIndicator(
                    progress = { (metric.myValue / 100.0).toFloat() },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = MyColor,
                    trackColor = Color(0xFF1E293B)
                )
                Text("${metric.myValue}${metric.unit}", color = MyColor, fontSize = 10.sp, modifier = Modifier.padding(top=2.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            // Market Bar
            Column(modifier = Modifier.weight(1f)) {
                LinearProgressIndicator(
                    progress = { (metric.marketValue / 100.0).toFloat() },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = MarketColor,
                    trackColor = Color(0xFF1E293B)
                )
                Text("${metric.marketValue}${metric.unit}", color = MarketColor, fontSize = 10.sp, modifier = Modifier.padding(top=2.dp))
            }
        }
    }
}