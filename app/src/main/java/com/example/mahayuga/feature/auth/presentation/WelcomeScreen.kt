package com.example.mahayuga.feature.auth.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mahayuga.R
import kotlinx.coroutines.delay

// ⚡ Colors derived from Property Detail Screen & Request
private val NavyBackground = Color(0xFF0F172A) // Deep Navy from Property Description
private val NavyLightSurface = Color(0xFF1E293B) // Lighter Navy for contrast (Sign Up)
private val TextWhite = Color(0xFFFFFFFF)

@Composable
fun WelcomeScreen(navController: NavController) {
    var showContent by remember { mutableStateOf(false) }

    // ⚡ Changed text to "Welcome To"
    val textToType = "Welcome To"
    var displayedText by remember { mutableStateOf("") }

    // ⚡ Using Coroutines here for the typing effect
    LaunchedEffect(Unit) {
        showContent = true
        // delay() is a suspend function that pauses the coroutine without blocking the thread
        textToType.forEachIndexed { index, _ ->
            displayedText = textToType.substring(0, index + 1)
            delay(100)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground) // ⚡ Updated Background
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
                    painter = painterResource(id = R.drawable.navyuga), // ⚡ Switched to Navyuga Logo
                    contentDescription = "Navyuga Logo",
                    modifier = Modifier.size(180.dp) // Adjusted size
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 3. Buttons
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(1000, delayMillis = 500)) + slideInVertically(initialOffsetY = { 50 })
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Log In: White Background, Navy Text (High Contrast)
                Button(
                    onClick = { navController.navigate("login") },
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

                // Sign Up: Lighter Navy Background, White Text (Complimenting Contrast)
                Button(
                    onClick = { navController.navigate("register") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyLightSurface, // ⚡ Lighter Navy
                        contentColor = TextWhite
                    ),
                    shape = MaterialTheme.shapes.medium,
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("Sign up", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}