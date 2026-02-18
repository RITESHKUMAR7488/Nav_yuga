// main/java/com/example/mahayuga/feature/assetmanager/presentation/listings/AmListingsScreen.kt
package com.example.mahayuga.feature.assetmanager.presentation.listings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel

private val AmBackground = Color(0xFF061123)
private val AmSurface = Color(0xFF111c30)
private val AmAccent = Color(0xFF38a882)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmListingsScreen(
    onBackClick: () -> Unit,
    viewModel: AmListingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Pending", "Live", "Rejected")

    Scaffold(
        containerColor = AmBackground,
        topBar = {
            TopAppBar(
                title = { Text("My Listings", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AmBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = AmBackground,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = AmAccent
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    val count = when (index) {
                        0 -> state.pendingProperties.size
                        1 -> state.liveProperties.size
                        else -> state.rejectedProperties.size
                    }
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                "$title ($count)",
                                color = if (selectedTabIndex == index) AmAccent else Color.Gray
                            )
                        }
                    )
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AmAccent)
                }
            } else {
                val currentList = when (selectedTabIndex) {
                    0 -> state.pendingProperties
                    1 -> state.liveProperties
                    else -> state.rejectedProperties
                }

                if (currentList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No properties found in this category.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentList) { property ->
                            ListingCard(property = property, tabIndex = selectedTabIndex)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListingCard(property: PropertyModel, tabIndex: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AmSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = property.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )

                val statusColor = when (tabIndex) {
                    0 -> Color(0xFFFFA500) // Orange for Pending
                    1 -> AmAccent // Green for Live
                    else -> Color.Red // Red for Rejected
                }

                Text(
                    text = property.approvalStatus,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Location: ${property.fullLocation}", color = Color.Gray, fontSize = 14.sp)
            Text(text = "Asset ID: ${property.assetId}", color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Valuation", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        property.totalValuation,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Current Status", color = Color.Gray, fontSize = 12.sp)
                    Text(property.status, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            if (tabIndex == 2) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Rejected by Admin. Please check requirements or contact support.",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}