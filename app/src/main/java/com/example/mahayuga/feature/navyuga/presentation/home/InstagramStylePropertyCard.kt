package com.example.mahayuga.feature.navyuga.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mahayuga.feature.navyuga.domain.model.PropertyModel
import com.example.mahayuga.feature.auth.presentation.components.formatIndian

private val FabColor = Color(0xFF4361EE)
private val StatusGreen = Color(0xFF00E676)
private val StatusRed = Color(0xFFFF3B30)

@Composable
fun InstagramStylePropertyCard(
    property: PropertyModel,
    onItemClick: () -> Unit,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    onInvestClick: () -> Unit,
    modifier: Modifier = Modifier,
    showInvestButton: Boolean = true
) {
    val isExited = property.status == "Exited"
    val isFunding = property.status == "Funding"

    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.clickable { onItemClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            // Header Row
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = property.mainImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        property.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        property.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 3 Dots Menu
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            "Options",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        containerColor = Color(0xFF1E1E1E)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Share", color = Color.White) },
                            onClick = { showMenu = false; onShareClick() },
                            leadingIcon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    "Share",
                                    tint = Color.White
                                )
                            }
                        )
                        // Moved Like Option Inside Menu
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (property.isLiked) "Unsave Property" else "Save Property",
                                    color = Color.White
                                )
                            },
                            onClick = { showMenu = false; onLikeClick() },
                            leadingIcon = {
                                Icon(
                                    if (property.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    "Save",
                                    tint = if (property.isLiked) Color.Red else Color.White
                                )
                            }
                        )
                    }
                }
            }

            // Image Section
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    // Reduced Height
                    .height(270.dp)
            ) {
                AsyncImage(
                    model = property.mainImage,
                    contentDescription = "Property Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                )

                // Only show Funded Circle if Funding
                if (isFunding) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(60.dp)
                            .background(Color.Black.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { property.fundedPercent / 100f },
                            modifier = Modifier.fillMaxSize(),
                            color = FabColor,
                            trackColor = Color.White.copy(alpha = 0.2f),
                            strokeWidth = 4.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${property.fundedPercent}%",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Funded",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp)
                            )
                        }
                    }
                }
            }

            // Stats Row
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    PropertyStat("Tenant", property.tenantName.ifEmpty { "-" })
                }
                VerticalBar()
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    // Format Sq Ft
                    val areaFormatted = try {
                        formatIndian(property.area.replace(",", "").toDouble())
                    } catch (e: Exception) {
                        property.area
                    }
                    PropertyStat("Sq ft", areaFormatted.ifEmpty { "-" })
                }
                VerticalBar()
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    PropertyStat(
                        "Tenure",
                        if (property.occupationPeriod.isNotEmpty()) "${property.occupationPeriod} Yrs" else "-"
                    )
                }
            }

            // Financials Row
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isExited) {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        PropertyStat("Entry", "₹${formatIndian(property.totalValuation)}")
                    }
                    VerticalBar()
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        PropertyStat("Exit", "₹${formatIndian(property.exitPrice)}")
                    }
                    VerticalBar()
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        PropertyStat("Profit", "₹${formatIndian(property.totalProfit)}", true)
                    }
                } else {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        PropertyStat("Price", "₹${formatIndian(property.totalValuation)}")
                    }
                    VerticalBar()
                    val annualRent =
                        if (property.grossAnnualRent.isNotEmpty()) property.grossAnnualRent else {
                            val monthly =
                                property.monthlyRent.replace(",", "").toDoubleOrNull() ?: 0.0
                            (monthly * 12).toString()
                        }
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        PropertyStat("Rent/Year", "₹${formatIndian(annualRent)}")
                    }
                    VerticalBar()
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        PropertyStat("ROI", "${property.roi}%", true)
                    }
                }
            }

            // Min Invest (Only for Funding)
            if (isFunding) {
                val minInvestText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White)) { append("Min Investment - ") }
                    withStyle(style = SpanStyle(color = FabColor)) {
                        append(
                            "₹${
                                formatIndian(
                                    property.minInvest
                                )
                            }"
                        )
                    }
                }
                Text(
                    text = minInvestText,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            HorizontalDivider(color = Color.White.copy(0.1f))

            // Action Button OR Status Bar
            if (isFunding && showInvestButton) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onShareClick) {
                        Icon(Icons.AutoMirrored.Filled.Send, "Share", tint = Color.White)
                    }
                    Button(
                        onClick = onInvestClick,
                        colors = ButtonDefaults.buttonColors(containerColor = FabColor),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("INVEST", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Green/Red Block for Funded/Sold
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(if (isExited) StatusRed else StatusGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isExited) "SOLD" else "FUNDED",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun VerticalBar() {
    Box(Modifier
        .width(1.dp)
        .height(32.dp)
        .background(Color.Gray.copy(0.2f)))
}

@Composable
fun PropertyStat(label: String, value: String, isHighlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}