package com.example.mahayuga.feature.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mahayuga.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityPrivacyScreen(onBackClick: () -> Unit) {
    Scaffold(
        containerColor = BricxBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Security & privacy",
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
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0A1E3F),
                                BricxBackground
                            ), startY = 0f, endY = 300f
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Your security is our priority",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BricxTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "We employ stringent measures, ensuring your money remains secure at all times.",
                            style = MaterialTheme.typography.bodySmall,
                            color = BricxTextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BricxBrandBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = BricxBrandBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "Security",
                        style = MaterialTheme.typography.titleSmall,
                        color = BricxTextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
                        border = BorderStroke(1.dp, BricxBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Fingerprint,
                                null,
                                tint = BricxBrandBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Biometric authentication",
                                    color = BricxTextPrimary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Enable for added security and streamline your login process",
                                    color = BricxTextSecondary,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Switch(
                                checked = true,
                                onCheckedChange = { },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = BricxTextPrimary,
                                    checkedTrackColor = BricxBrandBlue,
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color.DarkGray
                                )
                            )
                        }
                    }

                    SecurityOptionItem(
                        icon = Icons.Outlined.Security,
                        title = "Multi-factor authentication",
                        subtitle = "Coming Soon",
                        showArrow = false,
                        onClick = {})
                    SecurityOptionItem(
                        icon = Icons.Outlined.Link,
                        title = "Social logins",
                        subtitle = "Coming Soon",
                        showArrow = false,
                        onClick = {})
                }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "Privacy",
                        style = MaterialTheme.typography.titleSmall,
                        color = BricxTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    SecurityOptionItem(
                        icon = Icons.Outlined.Description,
                        title = "Privacy policy",
                        subtitle = "Updating",
                        showArrow = false,
                        onClick = {})
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BricxSurfaceCardLight)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            null,
                            tint = BricxBrandBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Navyuga will comply with SEBI if necessary in near future",
                            style = MaterialTheme.typography.labelSmall,
                            color = BricxTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun SecurityOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    showArrow: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
        border = BorderStroke(1.dp, BricxBorder)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = BricxBrandBlue, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    color = BricxTextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        subtitle,
                        color = BricxTextSecondary,
                        style = MaterialTheme.typography.labelSmall,
                        lineHeight = 16.sp
                    )
                }
            }
            if (showArrow) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = BricxTextSecondary)
            }
        }
    }
}