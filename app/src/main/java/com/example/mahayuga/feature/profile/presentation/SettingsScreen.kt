package com.example.mahayuga.feature.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mahayuga.ui.theme.BrandBlue

private val SettingsBg = Color(0xFF050505)
private val CardBg = Color(0xFF101920)
private val BorderColor = Color(0xFF1F2B36)
private val TextGrey = Color(0xFF888888)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    var hapticEnabled by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = SettingsBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
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
                actions = {
                    // Empty placeholder to center title
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SettingsBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Language
            SettingsOptionCard(
                icon = Icons.Outlined.Language,
                title = "Language",
                value = "EN",
                onClick = {}
            )

            // 2. Currency
            SettingsOptionCard(
                icon = Icons.Outlined.AttachMoney,
                title = "Currency",
                value = "AED", // Matching screenshot
                onClick = {}
            )

            // 3. Investment Preferences
            SettingsOptionCard(
                icon = Icons.Outlined.Tune, // Sliders icon works well here
                title = "Investment preferences",
                value = "Unselected",
                onClick = {}
            )

            // 4. Notifications
            SettingsOptionCard(
                icon = Icons.Outlined.Notifications,
                title = "Notifications settings",
                onClick = {}
            )

            // 5. Haptic Feedback (Switch)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Vibration,
                        null,
                        tint = BrandBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Haptic feedback",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Enable haptic feedback as you navigate through the app.",
                            color = TextGrey,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 16.sp
                        )
                    }

                    Switch(
                        checked = hapticEnabled,
                        onCheckedChange = { hapticEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BrandBlue, // Greenish in screenshot, but keeping Brand consistency
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }
            }

            // 6. Appearance
            SettingsOptionCard(
                icon = Icons.Outlined.Contrast, // Or DarkMode icon
                title = "Appearance",
                value = "System",
                onClick = {}
            )
        }
    }
}

@Composable
fun SettingsOptionCard(
    icon: ImageVector,
    title: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
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

            if (value != null) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGrey,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                null,
                tint = TextGrey
            )
        }
    }
}