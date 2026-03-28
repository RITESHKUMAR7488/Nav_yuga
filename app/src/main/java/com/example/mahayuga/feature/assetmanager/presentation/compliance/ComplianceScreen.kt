// main/java/com/example/mahayuga/feature/assetmanager/presentation/compliance/ComplianceScreen.kt
package com.example.mahayuga.feature.assetmanager.presentation.compliance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- THEME COLORS ---
private val AmBackground = Color(0xFF061123)
private val AmSurface = Color(0xFF111c30)
private val AmAccent = Color(0xFF38a882)
private val IconBoxBg = Color(0xFF1A2A40)
private val IconTint = Color(0xFF4FC3F7)
private val TextWhite = Color.White
private val TextGrey = Color(0xFF8B9BB4)

// --- DATA MODEL ---
data class ComplianceGridItem(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector
)

@Composable
fun ComplianceScreen(
    onNavigateBack: () -> Unit,
    onComplianceClick: (String) -> Unit
) {
    // ⚡ FIX: Using only guaranteed CORE Material Icons
    val complianceItems = listOf(
        ComplianceGridItem("kyc", "KYC", "(Know Your\nCustomer)", Icons.Default.Person),
        ComplianceGridItem("aml", "AML", "(Anti-Money\nLaundering)", Icons.Default.Lock),
        ComplianceGridItem("reg_filings", "Regulatory\nFilings", null, Icons.Default.List),
        ComplianceGridItem("sebi", "SEBI\nCompliance", null, Icons.Default.Build),
        ComplianceGridItem("inv_acc", "Investor\nAccreditation", null, Icons.Default.CheckCircle),
        ComplianceGridItem(
            "risk_audit",
            "Risk\nManagement\nand Audits",
            null,
            Icons.Default.Warning
        ),
        ComplianceGridItem("tax_1", "Tax\nCompliance", null, Icons.Default.Edit),
        ComplianceGridItem("tax_2", "Tax\nCompliance", null, Icons.Default.Info),
        ComplianceGridItem("tax_3", "Tax\nCompliance", null, Icons.Default.Done),
        ComplianceGridItem("doc_repo", "Document\nRepository", null, Icons.Default.Email)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmBackground)
    ) {
        // --- CUSTOM HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // ⚡ FIX: Using Core Lock icon for header
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Compliance",
                    tint = AmAccent,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Compliance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = TextWhite
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO: Search */ }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = TextWhite)
                }

                BadgedBox(badge = { Badge(containerColor = Color.Red) { Text("2") } }) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Alerts",
                        tint = TextWhite
                    )
                }

                IconButton(onClick = { /* TODO: Messages */ }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.MailOutline,
                        contentDescription = "Messages",
                        tint = TextWhite
                    )
                }
            }
        }

        // --- GRID CONTENT ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(complianceItems) { item ->
                ComplianceGridCard(
                    item = item,
                    onClick = { onComplianceClick(item.id) }
                )
            }
        }
    }
}

@Composable
fun ComplianceGridCard(
    item: ComplianceGridItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp) // Fixed height to keep cards uniform
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AmSurface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box mimicking the image's squircle design
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(IconBoxBg)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = IconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.title,
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 16.sp
                )
                if (item.subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.subtitle,
                        color = TextGrey,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}