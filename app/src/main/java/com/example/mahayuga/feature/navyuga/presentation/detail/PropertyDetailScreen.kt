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
import com.example.mahayuga.feature.auth.presentation.components.formatIndian // ⚡ IMPORT

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
        Box(Modifier.fillMaxSize().background(DeepDarkBlue), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BrandBlue)
        }
    } else if (property != null) {
        Scaffold(
            containerColor = DeepDarkBlue,
            bottomBar = {
                InvestBottomBar(
                    property = property,
                    onInvestClicked = {
                        try {
                            val message = "Hello, I am interested in investing in *${property.title}*. Please provide more details."
                            val url = "https://api.whatsapp.com/send?phone=$supportNumber&text=${Uri.encode(message)}"
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(url)
                                setPackage("com.whatsapp")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            try {
                                val message = "Hello, I am interested in investing in *${property.title}*."
                                val url = "https://wa.me/$supportNumber?text=${Uri.encode(message)}"
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(browserIntent)
                            } catch (e2: Exception) {
                                Toast.makeText(context, "WhatsApp not found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // ================== CAROUSEL ==================
                Box(modifier = Modifier.height(300.dp).fillMaxWidth()) {
                    val images = if (property.imageUrls.isNotEmpty()) property.imageUrls else listOf("")
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
                            .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)))
                    )

                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(top = 16.dp, start = 8.dp).align(Alignment.TopStart)
                    ) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }

                    if (images.size > 1) {
                        Row(
                            Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            repeat(images.size) { iteration ->
                                val color = if (pagerState.currentPage == iteration) BrandBlue else Color.White.copy(alpha = 0.5f)
                                Box(modifier = Modifier.padding(4.dp).clip(CircleShape).background(color).size(if (pagerState.currentPage == iteration) 10.dp else 8.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                // ================== CONTENT ==================
                Column(modifier = Modifier.padding(24.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(property.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(property.fullLocation, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
                        }
                        SuggestionChip(
                            onClick = {},
                            label = { Text(property.type, color = BrandBlue) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = BrandBlue.copy(alpha = 0.1f))
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Funded", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.7f))
                        Text("${property.fundedPercent}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = BrandBlue)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { property.fundedPercent / 100f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = BrandBlue,
                        trackColor = Color.White.copy(alpha = 0.1f),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // ⚡ FORMATTED
                        StatItem(label = "Price", value = "₹${formatIndian(property.totalValuation)}")
                        VerticalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.White.copy(alpha = 0.1f))

                        val displayRent = if (property.rentReturn.isEmpty()) "₹15k" else "₹${formatIndian(property.rentReturn)}"
                        StatItem(label = "Rent", value = displayRent)

                        VerticalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.White.copy(alpha = 0.1f))
                        StatItem(label = "ROI", value = "${property.roi}%", isHighlight = true)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    SectionTitle("Property Overview")
                    GridItem(label1 = "Area", value1 = property.area, label2 = "Floor", value2 = property.floor)
                    GridItem(label1 = "Age", value1 = property.age, label2 = "Parking", value2 = property.carPark)

                    Spacer(modifier = Modifier.height(24.dp))

                    SectionTitle("Lease Details")
                    InfoRow("Tenant", property.tenantName)
                    InfoRow("Occupancy", property.occupationPeriod)
                    InfoRow("Escalation", property.escalation)

                    Spacer(modifier = Modifier.height(24.dp))

                    SectionTitle("Financial Breakdown")
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // ⚡ FORMATTED
                            InfoRow("Monthly Rent", "₹${formatIndian(property.monthlyRent)}")
                            InfoRow("Gross Annual", "₹${formatIndian(property.grossAnnualRent)}")
                            InfoRow("Property Tax", "₹${formatIndian(property.annualPropertyTax)}")
                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.1f))
                            InfoRow("Net ROI", "${property.roi}%", isBold = true, valueColor = BrandBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    SectionTitle("Description")
                    Text(
                        property.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                    )

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    } else {
        Box(Modifier.fillMaxSize().background(DeepDarkBlue), contentAlignment = Alignment.Center) {
            Text("Property not found.", color = Color.White)
        }
    }
}

// --- HELPER COMPONENTS ---

@Composable
fun StatItem(label: String, value: String, isHighlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 12.dp))
}

@Composable
fun GridItem(label1: String, value1: String, label2: String, value2: String) {
    Row(Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(Modifier.weight(1f)) {
            Text(label1, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
            Text(value1, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = Color.White)
        }
        Column(Modifier.weight(1f)) {
            Text(label2, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
            Text(value2, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = Color.White)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, isBold: Boolean = false, valueColor: Color = Color.White) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, color = valueColor)
    }
}

@Composable
fun InvestBottomBar(
    property: PropertyModel,
    onInvestClicked: () -> Unit
) {
    Surface(
        color = DeepDarkBlue,
        tonalElevation = 8.dp,
        shadowElevation = 16.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        Column {
            Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Price", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.6f))
                    // ⚡ FORMATTED
                    Text("₹${formatIndian(property.totalValuation)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Button(
                    onClick = onInvestClicked,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text("Invest Now", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }
    }
}