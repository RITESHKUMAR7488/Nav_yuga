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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mahayuga.R
import com.example.mahayuga.core.common.BricxTopAppBar // ⚡ IMPORTED COMMON COMPONENT
import com.example.mahayuga.ui.theme.* // ⚡ IMPORTED BRICX THEME
import com.google.firebase.firestore.FirebaseFirestore

data class StatItem(val value: String, val label: String)

data class Founder(
    val name: String,
    val title: String,
    val description: String,
    val imageRes: Int,
    val linkedinUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutNavyugaScreen(onBackClick: () -> Unit) {
    var stats by remember {
        mutableStateOf(
            listOf(
                StatItem("0", "Registered Users"), StatItem("0", "Property Transactions"),
                StatItem("0", "User Nationalities"), StatItem("0", "Rental Income Paid"),
                StatItem("0%", "Avg Returns")
            )
        )
    }

    LaunchedEffect(Unit) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("app_settings").document("about_stats")
            .addSnapshotListener { snapshot, e ->
                if (e == null && snapshot != null && snapshot.exists()) {
                    stats = listOf(
                        StatItem(snapshot.getString("registeredUsers") ?: "0", "Registered Users"),
                        StatItem(
                            snapshot.getString("propertyTransactions") ?: "0",
                            "Property Transactions"
                        ),
                        StatItem(
                            snapshot.getString("userNationalities") ?: "0",
                            "User Nationalities"
                        ),
                        StatItem(
                            snapshot.getString("rentalIncomePaid") ?: "0",
                            "Rental Income Paid"
                        ),
                        StatItem(snapshot.getString("avgReturns") ?: "0%", "Avg Returns")
                    )
                }
            }
    }

    val founders = listOf(
        Founder(
            "Yaman Ondhia",
            "Founder and CEO",
            "“Navyuga is a vision to make Real Assets accessible. Building an infrastructure that enables Asset Managers to digitalise real estate and help investors reach the level of transparency it needs to own a part of the world. The vision is clearly stated in the tagline- Ownership For All”",
            R.drawable.ic_founder,
            "https://www.linkedin.com/in/yaman-ondhia"
        ),
        Founder(
            "Eashani Sengupta",
            "Co-Founder and COO",
            "“Navyuga is a technology driven real estate finance infrastructure. It enables asset managers to digitally manage properties, track returns and manage investors. The long term vision is freedom.”",
            R.drawable.ic_cofounder,
            "https://www.linkedin.com/in/eashani-sengupta"
        )
    )

    Scaffold(
        containerColor = BricxBackground, // ⚡ UPDATED
        topBar = {
            // ⚡ REPLACED CUSTOM HEADER WITH BRICX COMMON COMPONENT
            BricxTopAppBar(
                title = "About Navyuga",
                onNavigateBack = onBackClick
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            item {
                Text(
                    text = "Navyuga in numbers",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BricxTextPrimary, // ⚡ UPDATED
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 12.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(stats) { stat -> StatCard(stat) }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
                Text(
                    text = "Meet the founders",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BricxTextPrimary, // ⚡ UPDATED
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
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard), // ⚡ UPDATED
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
                color = BricxBrandBlue // ⚡ UPDATED
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stat.label,
                style = MaterialTheme.typography.labelSmall,
                color = BricxTextSecondary, // ⚡ UPDATED
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
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard), // ⚡ UPDATED
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
    ) {
        Column {
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

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        .clickable {
                            try {
                                val intent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(founder.linkedinUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_linkedin),
                        contentDescription = "LinkedIn",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = founder.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = BricxTextPrimary // ⚡ UPDATED
                )
                Text(
                    text = founder.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = BricxBrandBlue, // ⚡ UPDATED
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BricxSurfaceCardLight, RoundedCornerShape(12.dp)) // ⚡ UPDATED
                        .padding(16.dp)
                ) {
                    Text(
                        text = founder.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic,
                            lineHeight = 22.sp,
                            color = BricxTextSecondary.copy(alpha = 0.9f) // ⚡ UPDATED
                        )
                    )
                }
            }
        }
    }
}