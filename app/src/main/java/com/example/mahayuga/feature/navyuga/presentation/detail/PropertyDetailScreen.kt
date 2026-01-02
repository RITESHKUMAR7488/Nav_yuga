package com.example.mahayuga.feature.navyuga.presentation.detail

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.auth.presentation.components.formatIndian

private val DeepDarkBlue = Color(0xFF0F172A)
private val BrandBlue = Color(0xFF4361EE)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PropertyDetailScreen(
    propertyId: String,
    onNavigateBack: () -> Unit,
    viewModel: PropertyDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val supportNumber by viewModel.supportNumber.collectAsState()
    val property = uiState.property
    val context = LocalContext.current

    if (uiState.isLoading) {
        Box(Modifier
            .fillMaxSize()
            .background(DeepDarkBlue), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BrandBlue)
        }
    } else if (property != null) {
        val isExited = property.status == "Exited"
        val isFunded = property.status == "Funded"
        val showBottomBar = !isExited && !isFunded

        Scaffold(
            containerColor = DeepDarkBlue,
            bottomBar = {
                if (showBottomBar) {
                    InvestBottomBar(
                        property = property,
                        onInvestClicked = {
                            try {
                                val message =
                                    "Hello, I am interested in investing in *${property.title}*."
                                val url =
                                    "https://api.whatsapp.com/send?phone=$supportNumber&text=${
                                        Uri.encode(message)
                                    }"
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse(url); setPackage("com.whatsapp")
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "WhatsApp not found", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()) {
                    val images =
                        if (property.imageUrls.isNotEmpty()) property.imageUrls else listOf("")
                    val pagerState = rememberPagerState(pageCount = { images.size })
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                        AsyncImage(
                            model = images[page],
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.7f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(top = 16.dp, start = 8.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                }
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                property.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                property.fullLocation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        SuggestionChip(
                            onClick = {},
                            label = {
                                Text(
                                    property.status,
                                    color = if (isExited) Color.Red else BrandBlue
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (isExited) Color.Red.copy(
                                    0.1f
                                ) else BrandBlue.copy(alpha = 0.1f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (!isExited) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Funded",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(0.7f)
                            )
                            Text(
                                "${property.fundedPercent}%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = BrandBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { property.fundedPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = BrandBlue,
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Stats Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (isExited) {
                            StatItem(
                                label = "Entry",
                                value = "₹${formatIndian(property.totalValuation)}"
                            )
                            VerticalDivider(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(1.dp),
                                color = Color.White.copy(alpha = 0.1f)
                            )
                            StatItem(label = "Exit", value = "₹${formatIndian(property.exitPrice)}")
                            VerticalDivider(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(1.dp),
                                color = Color.White.copy(alpha = 0.1f)
                            )
                            StatItem(
                                label = "Profit",
                                value = "₹${formatIndian(property.totalProfit)}",
                                isHighlight = false
                            )
                        } else {
                            StatItem(
                                label = "Price",
                                value = "₹${formatIndian(property.totalValuation)}"
                            )
                            VerticalDivider(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(1.dp),
                                color = Color.White.copy(alpha = 0.1f)
                            )

                            // ⚡ UPDATED: Show Rent/Year
                            val rentToShow = if (property.grossAnnualRent.isNotEmpty()) {
                                property.grossAnnualRent
                            } else {
                                val monthly =
                                    property.monthlyRent.replace(",", "").toDoubleOrNull() ?: 0.0
                                (monthly * 12).toString()
                            }
                            StatItem(label = "Rent/Year", value = "₹${formatIndian(rentToShow)}")

                            VerticalDivider(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(1.dp),
                                color = Color.White.copy(alpha = 0.1f)
                            )
                            StatItem(label = "ROI", value = "${property.roi}%", isHighlight = false)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    SectionTitle("Property Overview")
                    GridItem(
                        label1 = "Area",
                        value1 = property.area,
                        label2 = "Floor",
                        value2 = property.floor
                    )
                    GridItem(
                        label1 = "Age",
                        value1 = property.age,
                        label2 = "Parking",
                        value2 = property.carPark
                    )

                    if (property.assetManager.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow("Asset Manager", property.assetManager)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isExited) {
                        SectionTitle("Exit Performance")
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(
                                    alpha = 0.05f
                                )
                            ), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                InfoRow("Entry Price", "₹${formatIndian(property.totalValuation)}")
                                InfoRow("Exit Price", "₹${formatIndian(property.exitPrice)}")
                                Divider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = Color.White.copy(alpha = 0.1f)
                                )
                                InfoRow(
                                    "Total Profit",
                                    "₹${formatIndian(property.totalProfit)}",
                                    isBold = true,
                                    valueColor = Color.White
                                )
                            }
                        }
                    } else {
                        SectionTitle("Lease Details")
                        InfoRow("Tenant", property.tenantName)
                        InfoRow("Occupancy", "${property.occupationPeriod} Years")
                        InfoRow("Escalation", property.escalation)

                        Spacer(modifier = Modifier.height(24.dp))

                        SectionTitle("Financial Breakdown")
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(
                                    alpha = 0.05f
                                )
                            ), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                InfoRow("Monthly Rent", "₹${formatIndian(property.monthlyRent)}")
                                InfoRow(
                                    "Gross Annual",
                                    "₹${formatIndian(property.grossAnnualRent)}"
                                )
                                InfoRow(
                                    "Property Tax",
                                    "₹${formatIndian(property.annualPropertyTax)}"
                                )
                                Divider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = Color.White.copy(alpha = 0.1f)
                                )
                                InfoRow(
                                    "Net ROI",
                                    "${property.roi}%",
                                    isBold = true,
                                    valueColor = Color.White
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionTitle("Description")
                    Text(
                        property.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    } else {
        Box(
            Modifier
                .fillMaxSize()
                .background(DeepDarkBlue),
            contentAlignment = Alignment.Center
        ) { Text("Property not found.", color = Color.White) }
    }
}

// Helpers
@Composable
fun StatItem(label: String, value: String, isHighlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun GridItem(label1: String, value1: String, label2: String, value2: String) {
    Row(Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp)) {
        Column(Modifier.weight(1f)) {
            Text(label1, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.5f))
            Text(value1, style = MaterialTheme.typography.bodyLarge, color = Color.White)
        }
        Column(Modifier.weight(1f)) {
            Text(label2, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.5f))
            Text(value2, style = MaterialTheme.typography.bodyLarge, color = Color.White)
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = Color.White
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(0.7f))
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = valueColor
        )
    }
}

@Composable
fun InvestBottomBar(property: PropertyModel, onInvestClicked: () -> Unit) {
    Surface(
        color = DeepDarkBlue,
        tonalElevation = 8.dp,
        shadowElevation = 16.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        Column {
            Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Min Investment",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        "₹${formatIndian(property.minInvest)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Button(
                    onClick = onInvestClicked,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text(
                        "Invest Now",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}