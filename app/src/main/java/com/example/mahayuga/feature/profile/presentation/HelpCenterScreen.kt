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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mahayuga.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(onBackClick: () -> Unit) {
    Scaffold(
        containerColor = BricxBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Get help",
                        fontWeight = FontWeight.Bold,
                        color = BricxTextPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Back",
                            tint = BricxTextPrimary
                        )
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BricxBackground)
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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Help resources",
                    style = MaterialTheme.typography.titleMedium,
                    color = BricxTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
                    border = BorderStroke(1.dp, BricxBorder)
                ) {
                    Column {
                        HelpOptionItem(Icons.Outlined.HelpOutline, "FAQs", onClick = {})
                        HorizontalDivider(
                            color = BricxBorder,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        HelpOptionItem(Icons.Outlined.Book, "Glossary", onClick = {})
                        HorizontalDivider(
                            color = BricxBorder,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        HelpOptionItem(Icons.Outlined.PlayCircle, "How it Works", onClick = {})
                        HorizontalDivider(
                            color = BricxBorder,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        HelpOptionItem(Icons.Outlined.TouchApp, "Welcome tour", onClick = {})
                        HorizontalDivider(
                            color = BricxBorder,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        HelpOptionItem(Icons.Outlined.Widgets, "Navyuga widget", onClick = {})
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Contact us",
                    style = MaterialTheme.typography.titleMedium,
                    color = BricxTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
                    border = BorderStroke(1.dp, BricxBorder)
                ) {
                    Column {
                        HelpOptionItem(Icons.Outlined.ChatBubbleOutline, "Live chat", onClick = {})
                        HorizontalDivider(
                            color = BricxBorder,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        HelpOptionItem(Icons.Outlined.Call, "Whatsapp us", onClick = {})
                        HorizontalDivider(
                            color = BricxBorder,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        HelpOptionItem(Icons.Outlined.Email, "Email", onClick = {})
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Version 2.225",
                style = MaterialTheme.typography.bodySmall,
                color = BricxTextSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun HelpOptionItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = BricxBrandBlue, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = BricxTextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}