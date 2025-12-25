package com.example.mahayuga.feature.profile.presentation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mahayuga.R

// Navyuga Theme Colors
private val ScreenBg = Color(0xFF050505)
private val CardBg = Color(0xFF101920) // Dark Slate
private val BrandBlue = Color(0xFF2979FF)
private val TextWhite = Color(0xFFFFFFFF)
private val TextGrey = Color(0xFF94A3B8)

data class StatItem(
    val value: String,
    val label: String
)

data class Founder(
    val name: String,
    val title: String,
    val description: String,
    val imageRes: Int,
    val linkedinUrl: String // ⚡ Added LinkedIn URL
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutNavyugaScreen(
    onBackClick: () -> Unit
) {
    // 1. Stats Data
    val stats = listOf(
        StatItem("0", "Registered Users"),
        StatItem("0", "Property Transactions"),
        StatItem("0", "User Nationalities"),
        StatItem("0", "Rental Income Paid"),
        StatItem("0%", "Avg Returns")
    )

    // 2. Founders Data
    val founders = listOf(
        Founder(
            name = "Yaman Ondhia",
            title = "Founder and CEO",
            description = "“Navyuga is a vision to make Real Assets accessible. Building an infrastructure that enables Asset Managers to digitalise real estate and help investors reach the level of transparency it needs to own a part of the world. The vision is clearly stated in the tagline- Ownership For All”",
            imageRes = R.drawable.ic_founder, // TODO: Replace with R.drawable.yaman
            linkedinUrl = "https://www.linkedin.com/in/yaman-ondhia-873310224?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=ios_app"
        ),
        Founder(
            name = "Eashani Sengupta",
            title = "Co-Founder and COO",
            description = "“Navyuga is a technology driven real estate finance infrastructure. It enables asset managers to digitally manage properties, track returns and manage investors. The long term vision is freedom.”",
            imageRes = R.drawable.ic_cofounder, // TODO: Replace with R.drawable.eashani
            linkedinUrl = "https://www.linkedin.com/in/eashani-sengupta-2a116a208?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=ios_app"
        )
    )

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "About Navyuga",
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ScreenBg)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // SECTION 1: NAVYUGA IN NUMBERS
            item {
                Text(
                    text = "Navyuga in numbers",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 12.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(stats) { stat ->
                        StatCard(stat)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // SECTION 2: MEET THE FOUNDERS
            item {
                Text(
                    text = "Meet the founders",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            items(founders) { founder ->
                FounderCard(founder)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun StatCard(stat: StatItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(160.dp)
            .height(100.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stat.value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = BrandBlue
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stat.label,
                style = MaterialTheme.typography.labelSmall,
                color = TextGrey,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun FounderCard(founder: Founder) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
    ) {
        Column {
            // 1. BIG IMAGE SECTION
            Box {
                Image(
                    painter = painterResource(id = founder.imageRes),
                    contentDescription = founder.name,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                )

                // Floating LinkedIn Icon (Top Right)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        .clickable {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(founder.linkedinUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // ⚡ CHANGED: Using Image instead of Icon to preserve original logo colors
                    // and removed tint to ensure visibility.
                    Image(
                        painter = painterResource(id = R.drawable.ic_linkedin),
                        contentDescription = "LinkedIn",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // 2. INFO SECTION
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = founder.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = founder.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = BrandBlue,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quote Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = founder.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic,
                            lineHeight = 22.sp,
                            color = TextGrey.copy(alpha = 0.9f)
                        )
                    )
                }
            }
        }
    }
}