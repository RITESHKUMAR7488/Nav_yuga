package com.example.mahayuga.feature.auth.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mahayuga.R
import com.example.mahayuga.navigation.AssetManagerDestinations
import kotlinx.coroutines.delay

// Theme Colors
private val NavyBackground = Color(0xFF0F172A)
private val NavyLightSurface = Color(0xFF1E293B)
private val TextWhite = Color(0xFFFFFFFF)
private val BrandBlue = Color(0xFF2979FF)

@Composable
fun WelcomeScreen(navController: NavController) {
    var showContent by remember { mutableStateOf(false) }

    // 0 = Investor, 1 = Asset Manager (Partner)
    var selectedRole by remember { mutableIntStateOf(0) }

    // Typing effect
    val textToType = "Welcome To"
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        showContent = true
        textToType.forEachIndexed { index, _ ->
            displayedText = textToType.substring(0, index + 1)
            delay(100)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // 1. Text: "Welcome To" (Animated)
        Text(
            text = displayedText,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextWhite
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Logo: Navyuga
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(1000)) + slideInVertically()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.navyuga),
                    contentDescription = "Navyuga Logo",
                    modifier = Modifier.size(180.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 3. ROLE TOGGLE SWITCH (New Feature)
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(500))
        ) {
            RoleToggleSwitch(
                selectedRole = selectedRole,
                onRoleSelected = { selectedRole = it }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 4. Buttons
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(1000, delayMillis = 500)) + slideInVertically(initialOffsetY = { 50 })
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val isPartner = selectedRole == 1

                // Login Button
                Button(
                    onClick = {
                        // Both roles use the same login screen initially
                        // The ViewModel will route them based on their data later
                        navController.navigate("login")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TextWhite,
                        contentColor = NavyBackground
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                // Sign Up / Join Button (Diverges here)
                Button(
                    onClick = {
                        if (isPartner) {
                            // Go to New Asset Manager Onboarding
                            navController.navigate(AssetManagerDestinations.ONBOARDING_INTRO)
                        } else {
                            // Go to Existing Investor Register
                            navController.navigate("register")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPartner) BrandBlue else NavyLightSurface,
                        contentColor = TextWhite
                    ),
                    shape = MaterialTheme.shapes.medium,
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text(
                        text = if (isPartner) "Join as Partner" else "Sign up",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun RoleToggleSwitch(
    selectedRole: Int,
    onRoleSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(NavyLightSurface)
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Investor Tab
            RoleTab(
                title = "Investor",
                isSelected = selectedRole == 0,
                modifier = Modifier.weight(1f),
                onClick = { onRoleSelected(0) }
            )

            // Partner Tab
            RoleTab(
                title = "Asset Manager",
                isSelected = selectedRole == 1,
                modifier = Modifier.weight(1f),
                onClick = { onRoleSelected(1) }
            )
        }
    }
}

@Composable
fun RoleTab(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        if (isSelected) BrandBlue else Color.Transparent,
        label = "bg"
    )
    val textColor by animateColorAsState(
        if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
        label = "text"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(25.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Preview
@Composable
fun WelcomePreview() {
    MaterialTheme {
        WelcomeScreen(rememberNavController())
    }
}