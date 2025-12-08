package com.example.navyuga.feature.arthyuga.presentation.detail

import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.navyuga.feature.auth.presentation.components.NavyugaGradientButton
import com.example.navyuga.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    viewModel: PropertyDetailViewModel = hiltViewModel(),
    navController: androidx.navigation.NavController
) {
    val property by viewModel.property.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState() // ⚡ Observe Loading
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var investmentAmount by remember { mutableFloatStateOf(5000f) }

    // ⚡ Better Loading/Error State Handling
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    if (property == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Property Not Found (Check ID match)", color = ErrorRed)
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.padding(top = 16.dp)) {
                Text("Go Back")
            }
        }
        return
    }

    val prop = property!!
    val roi = prop.roi
    val monthlyReturn = (investmentAmount * (roi / 100)) / 12

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(prop.title, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    NavyugaGradientButton(
                        text = "Invest Now via WhatsApp",
                        onClick = {
                            val message = "Hi, I am interested in *${prop.title}* at *${prop.location}*.\n\nI want to invest *₹${investmentAmount.toInt()}*."
                            val url = "https://api.whatsapp.com/send?phone=919876543210&text=${java.net.URLEncoder.encode(message, "UTF-8")}"
                            val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        ) {
            // CAROUSEL
            Box {
                val pagerState = rememberPagerState(pageCount = { prop.imageUrls.size })

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth().height(280.dp)
                ) { page ->
                    AsyncImage(
                        model = prop.imageUrls.getOrNull(page),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                if (pagerState.pageCount > 1) {
                    Row(
                        Modifier.height(50.dp).fillMaxWidth().align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(pagerState.pageCount) { iteration ->
                            val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.5f)
                            Box(modifier = Modifier.padding(4.dp).clip(CircleShape).background(color).size(8.dp))
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = prop.location, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)).padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBadge("Target ROI", "${prop.roi}%", SuccessGreen)
                    StatBadge("Min Invest", "₹${prop.minInvest}", BrandBlue)
                    StatBadge("Funded", "${prop.fundedPercent}%", CyanAccent)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // CALCULATOR
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Investment Calculator", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("I want to invest: ₹${investmentAmount.toInt()}", color = MaterialTheme.colorScheme.primary)
                        Slider(
                            value = investmentAmount,
                            onValueChange = { investmentAmount = it },
                            valueRange = 5000f..500000f,
                            steps = 100,
                            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                        )

                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))

                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Est. Monthly Return", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("₹${String.format("%.0f", monthlyReturn)}", color = SuccessGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("About the Asset", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(
                    "This premium commercial property located in a prime area offers high rental yields. Leased to a AAA tenant with a lock-in period of 5 years.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )
            }
        }
    }
}

@Composable
fun StatBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
    }
}