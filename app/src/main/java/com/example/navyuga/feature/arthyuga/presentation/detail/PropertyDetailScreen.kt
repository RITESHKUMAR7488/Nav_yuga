package com.example.navyuga.feature.arthyuga.presentation.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.navyuga.feature.arthyuga.domain.model.PropertyModel
import java.net.URLEncoder

@Composable
fun PropertyDetailScreen(
    propertyId: String,
    onNavigateBack: () -> Unit,
    viewModel: PropertyDetailViewModel = hiltViewModel()
) {
    // 1. Collect Property Data (Coroutine Flow Collection)
    val state by viewModel.state.collectAsStateWithLifecycle()

    // 2. Collect Dynamic WhatsApp Number
    val whatsappNumber by viewModel.supportNumber.collectAsStateWithLifecycle()

    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            state.property?.let { property ->
                PropertyBottomBar(
                    onInvestClick = {
                        launchWhatsApp(context, property, whatsappNumber)
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                state.error != null -> {
                    Text(
                        text = state.error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.property != null -> {
                    PropertyDetailContent(
                        property = state.property!!,
                        onBackClick = onNavigateBack
                    )
                }
            }
        }
    }
}

@Composable
fun PropertyDetailContent(
    property: PropertyModel,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    var visible by remember { mutableStateOf(false) }

    // Coroutine: Trigger animation when composition enters
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // --- 1. Header Image (Restored Height & Overlay) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp) // Restored original height
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(property.mainImage)
                    .crossfade(true)
                    .build(),
                contentDescription = property.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            // Top Bar: Back Button & Verified Tag
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                }

                // Verified Tag (Restored Position)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Verified Property",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Bottom Overlay: Title & Location
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
                    .padding(bottom = 20.dp) // Push up slightly to avoid overlap clipping
            ) {
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = property.fullLocation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // --- 2. Content Body ---
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { 100 }) + fadeIn()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-30).dp) // Overlap effect
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp)
            ) {
                // Funded Progress Bar (Restored Look)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Funded",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${property.fundedPercent}%",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { property.fundedPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        strokeCap = StrokeCap.Round,
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- 3. Financial Stats Row (Price / Return / ROI) ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Price
                    BigStatItem(
                        value = property.totalValuation.ifEmpty { "-" },
                        label = "Price"
                    )

                    VerticalDivider(height = 40.dp)

                    // Return
                    BigStatItem(
                        value = property.rentReturn.ifEmpty { "-" },
                        label = "Return"
                    )

                    VerticalDivider(height = 40.dp)

                    // ROI
                    BigStatItem(
                        value = "${property.roi}%",
                        label = "ROI",
                        isHighlight = true
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(32.dp))

                // --- 4. Detailed Information Sections ---

                // Header 1: Property Information
                InfoSectionHeader(title = "Property Information")
                InfoRow(label = "Property Name", value = property.title)
                InfoRow(label = "Address", value = property.fullLocation)
                InfoRow(label = "Type", value = "-")
                InfoRow(label = "Age", value = "-")
                InfoRow(label = "Area", value = "-")
                InfoRow(label = "Floor", value = "-")
                InfoRow(label = "Car Park", value = "-")

                Spacer(modifier = Modifier.height(24.dp))

                // Header 2: Lease Information
                InfoSectionHeader(title = "Lease Information")
                InfoRow(label = "Tenant Name", value = "-")
                InfoRow(label = "Period of Occupation", value = "-")
                InfoRow(label = "Escalation", value = "-")

                Spacer(modifier = Modifier.height(24.dp))

                // Header 3: Financial Analysis
                InfoSectionHeader(title = "Financial Analysis")
                InfoRow(label = "Monthly RENT", value = property.rentReturn.ifEmpty { "-" })
                InfoRow(label = "Gross Annual Rent", value = "-")
                InfoRow(label = "Annual Property Tax", value = "-")

                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(24.dp))

                // --- 5. Description ---
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (property.description.isNotEmpty()) property.description else "-",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// --- Helper Components ---

@Composable
fun BigStatItem(
    value: String,
    label: String,
    isHighlight: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = if (isHighlight) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun InfoSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

@Composable
fun VerticalDivider(height: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .height(height)
            .width(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    )
}

@Composable
fun PropertyBottomBar(
    onInvestClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .safeDrawingPadding()
        ) {
            Button(
                onClick = onInvestClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Invest Now via WhatsApp",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }
    }
}

private fun launchWhatsApp(context: Context, property: PropertyModel, contactNumber: String) {
    val message = """
        Hi, I am interested in *${property.title}*.
        
        *Details:*
        Location: ${property.fullLocation}
        Price: ${property.totalValuation}
        Return: ${property.rentReturn}
        ROI: ${property.roi}%
        
        Please provide more details.
    """.trimIndent()

    try {
        val url = "https://api.whatsapp.com/send?phone=$contactNumber&text=${URLEncoder.encode(message, "UTF-8")}"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
    }
}