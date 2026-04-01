package com.example.mahayuga.feature.auth.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import kotlinx.coroutines.delay

private val NavyBackground = Color(0xFF0F172A)
private val NavyLightSurface = Color(0xFF1E293B)
private val TextWhite = Color(0xFFFFFFFF)

@Composable
fun WelcomeScreen(navController: NavController) {
    var showContent by remember { mutableStateOf(false) }

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

        Text(
            text = displayedText,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextWhite
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(1000)) + slideInVertically()
        ) {
            // ⚡ BRICX BRANDING REPLACING IMAGE LOGO
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "BRICX",
                    color = Color.White,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 0.15.em,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "SMART REAL ESTATE INVESTING",
                    color = Color(0xFF14B8A6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.1.em,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(1000, delayMillis = 500)) + slideInVertically(initialOffsetY = { 50 })
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TextWhite, contentColor = NavyBackground),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { navController.navigate("register") },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyLightSurface, contentColor = TextWhite),
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