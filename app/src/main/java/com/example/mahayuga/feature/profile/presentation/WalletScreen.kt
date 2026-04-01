// main/java/com/example/mahayuga/feature/profile/presentation/WalletScreen.kt
package com.example.mahayuga.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mahayuga.core.common.* // ⚡ IMPORTED COMMON COMPONENTS
import com.example.mahayuga.ui.theme.* // ⚡ IMPORTED BRICX THEME

private val WalletCardStart = Color(0xFF2979FF)
private val WalletCardEnd = Color(0xFF1565C0)

@Composable
fun WalletScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = BricxBackground, // ⚡ UPDATED
        topBar = {
            // ⚡ REPLACED RAW TOPAPPBAR WITH BRICXTOPAPPBAR
            BricxTopAppBar(
                title = "Wallet",
                onNavigateBack = onBackClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                WalletCardStart,
                                WalletCardEnd
                            )
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Total balance",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("₹0", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WalletActionButton(icon = Icons.Default.Sync, label = "Invest")
                WalletActionButton(icon = Icons.Default.Add, label = "Deposit")
                WalletActionButton(icon = Icons.Default.CallMade, label = "Withdraw")
                WalletActionButton(icon = Icons.Outlined.CreditCard, label = "Settings")
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard), // ⚡ UPDATED
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(Icons.Default.AccountBalance, null, tint = BricxTextPrimary)
                        Icon(Icons.Default.CreditCard, null, tint = Color(0xFF1A237E))
                        Icon(Icons.Default.CreditCard, null, tint = Color(0xFFFF9800))
                        Icon(Icons.Default.Smartphone, null, tint = BricxTextSecondary)
                    }

                    Text(
                        "Pay with Debit Card or Bank Transfer",
                        color = BricxTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Add your Bank Account or pay instantly at checkout using ApplePay or Debit Card",
                        color = BricxTextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // ⚡ REPLACED RAW BUTTON WITH BRICXPRIMARYBUTTON
                    BricxPrimaryButton(
                        text = "Add payment method",
                        onClick = { /* Add Method */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        backgroundColor = BricxBrandBlue // Using Brand Blue to fit the wallet theme nicely
                    )
                }
            }
        }
    }
}

@Composable
fun WalletActionButton(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(BricxSurfaceCardLight)
                .clickable { }, // ⚡ UPDATED
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = BricxTextPrimary) } // ⚡ UPDATED
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            label,
            color = BricxTextPrimary,
            style = MaterialTheme.typography.bodySmall
        ) // ⚡ UPDATED
    }
}