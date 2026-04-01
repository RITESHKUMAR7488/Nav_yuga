// main/java/com/example/mahayuga/feature/profile/presentation/ProfileScreen.kt
package com.example.mahayuga.feature.profile.presentation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mahayuga.core.common.BricxHubTopAppBar
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
    onNavigateToLiked: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToAbout: () -> Unit = {},
    onLogout: () -> Unit,
    onNavigateToMenu: (() -> Unit)? = null
) {
    val currentUserState by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    val user = (currentUserState as? UiState.Success)?.data
    val userName = user?.name?.takeIf { it.isNotBlank() } ?: "Investor"
    val userInitials = if (userName.length >= 2) userName.take(2).uppercase() else "IN"
    val userId = user?.uid?.take(6)?.uppercase() ?: "NEW"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BricxBackground)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .background(BricxBackground)
                .statusBarsPadding()
        ) {
            BricxHubTopAppBar(
                title = "Profile",
                icon = Icons.Outlined.Person,
                onSearchClick = {
                    Toast.makeText(context, "Search coming soon", Toast.LENGTH_SHORT).show()
                },
                onNotificationClick = onNavigateToNotifications,
                onMessageClick = onNavigateToMessages
            )
            HorizontalDivider(color = BricxBorder.copy(alpha = 0.5f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(BricxSurfaceCard)
                    .border(2.dp, BricxBrandTeal, CircleShape)
                    .clickable { onNavigateToAccount() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInitials,
                    color = BricxBrandTeal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = userName,
                color = BricxTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "BRICX-ID: #$userId", color = BricxTextSecondary, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileOptionCard(
                icon = Icons.Outlined.AccountCircle,
                title = "Account Details",
                onClick = onNavigateToAccount
            )
            ProfileOptionCard(
                icon = Icons.Outlined.FavoriteBorder,
                title = "Liked Properties",
                onClick = onNavigateToLiked
            )
            ProfileOptionCard(
                icon = Icons.Outlined.AccountBalanceWallet,
                title = "Wallet",
                onClick = onNavigateToWallet
            )
            ProfileOptionCard(
                icon = Icons.Outlined.Settings,
                title = "Settings",
                onClick = onNavigateToSettings
            )
            ProfileOptionCard(
                icon = Icons.Outlined.Lock,
                title = "Security & privacy",
                onClick = onNavigateToSecurity
            )
            ProfileOptionCard(
                icon = Icons.Outlined.Info,
                title = "About BRICX",
                onClick = onNavigateToAbout
            )
            ProfileOptionCard(
                icon = Icons.Outlined.HelpOutline,
                title = "Help center",
                onClick = onNavigateToHelp
            )
            ProfileOptionCard(
                icon = Icons.Outlined.Description,
                title = "Documents",
                badge = "NEW",
                onClick = {
                    Toast.makeText(context, "Documents coming soon", Toast.LENGTH_SHORT).show()
                })

            Spacer(modifier = Modifier.height(12.dp))

            ProfileOptionCard(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Logout",
                textColor = BricxDangerRed,
                iconColor = BricxDangerRed,
                showChevron = false,
                onClick = { viewModel.logout(); onLogout() }
            )
        }

        Spacer(modifier = Modifier.height(180.dp))
    }
}

@Composable
fun ProfileOptionCard(
    icon: ImageVector, title: String, badge: String? = null,
    textColor: Color = BricxTextPrimary, iconColor: Color = BricxTextPrimary,
    showChevron: Boolean = true, onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BricxBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor.copy(alpha = 0.8f),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            if (badge != null) {
                Surface(
                    color = BricxBrandTeal.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Text(
                        text = badge,
                        color = BricxBrandTeal,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (showChevron) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = BricxTextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}