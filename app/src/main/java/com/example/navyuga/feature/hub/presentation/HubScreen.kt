package com.example.navyuga.feature.hub.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.hub.data.model.SuperAppModule
import com.example.navyuga.ui.theme.*

@Composable
fun HubScreen(
    navController: NavController,
    viewModel: HubViewModel = hiltViewModel()
) {
    val modulesState by viewModel.modules.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBg)
            .padding(16.dp)
    ) {
        // 1. Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Navyuga Hub",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextWhiteHigh,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Select your universe",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhiteMedium
                )
            }

            // Logout Icon
            IconButton(onClick = {
                navController.navigate("login") {
                    popUpTo(0) // Clear stack
                }
            }) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = ErrorRed
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Grid Content
        when (val state = modulesState) {
            is UiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.data) { module ->
                        ModuleCard(module = module) {
                            if (module.isEnabled) {
                                if (module.id == "arthyuga") {
                                    // ⚡ Navigate to Phase 3 (ArthYuga)
                                    navController.navigate("arthyuga_dashboard")
                                } else {
                                    Toast.makeText(context, "Navigating to ${module.title}", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyanAccent)
                }
            }
            else -> {}
        }
    }
}

@Composable
fun ModuleCard(
    module: SuperAppModule,
    onClick: () -> Unit
) {
    val cardAlpha = if (module.isEnabled) 1f else 0.5f

    // Gradient Border for active cards
    val borderModifier = if (module.isEnabled) {
        Modifier.border(1.dp, Brush.linearGradient(listOf(CyanAccent, BrandBlue)), RoundedCornerShape(16.dp))
    } else {
        Modifier.border(1.dp, BorderStroke, RoundedCornerShape(16.dp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .then(borderModifier)
            .clickable(enabled = module.isEnabled, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MidnightCard.copy(alpha = cardAlpha)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Circle
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(MidnightSurface, CircleShape)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = module.icon,
                    contentDescription = null,
                    tint = if (module.isEnabled) CyanAccent else TextWhiteLow,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = module.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextWhiteHigh,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = module.description,
                style = MaterialTheme.typography.labelSmall,
                color = TextWhiteMedium,
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 4.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ⚡ PREVIEW CODE
@Preview(showBackground = true)
@Composable
fun HubScreenPreview() {
    NavyugaTheme {
        // Fake Navigation & Data for Preview
        HubScreen(navController = rememberNavController())
    }
}

@Preview
@Composable
fun ModuleCardPreview() {
    NavyugaTheme {
        ModuleCard(
            module = SuperAppModule(
                id = "test",
                title = "ArthYuga",
                description = "Invest in Real Estate",
                icon = Icons.Default.Apartment,
                isEnabled = true
            ),
            onClick = {}
        )
    }
}