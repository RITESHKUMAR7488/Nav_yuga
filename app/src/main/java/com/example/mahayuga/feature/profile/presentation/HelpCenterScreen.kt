package com.example.mahayuga.feature.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mahayuga.ui.theme.BrandBlue

private val ScreenBg = Color(0xFF050505)
private val CardBg = Color(0xFF101920)
private val BorderColor = Color(0xFF1F2B36)
private val TextGrey = Color(0xFF888888)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Get help",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. HELP RESOURCES
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Help resources",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column {
                        HelpOptionItem(Icons.Outlined.HelpOutline, "FAQs", onClick = {})
                        Divider(color = BorderColor, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        HelpOptionItem(Icons.Outlined.Book, "Glossary", onClick = {})
                        Divider(color = BorderColor, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        HelpOptionItem(Icons.Outlined.PlayCircle, "How it Works", onClick = {})
                        Divider(color = BorderColor, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        HelpOptionItem(Icons.Outlined.TouchApp, "Welcome tour", onClick = {})
                        Divider(color = BorderColor, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        HelpOptionItem(Icons.Outlined.Widgets, "Navyuga widget", onClick = {})
                    }
                }
            }

            // 2. CONTACT US
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Contact us",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column {
                        HelpOptionItem(Icons.Outlined.ChatBubbleOutline, "Live chat", onClick = {})
                        Divider(color = BorderColor, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        HelpOptionItem(Icons.Outlined.Call, "Whatsapp us", onClick = {})
                        Divider(color = BorderColor, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        HelpOptionItem(Icons.Outlined.Email, "Email", onClick = {})
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 3. FOOTER
            Text(
                text = "Version 2.225",
                style = MaterialTheme.typography.bodySmall,
                color = TextGrey,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun HelpOptionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = BrandBlue, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        // ⚡ REMOVED: Forward Arrow Icon
    }
}