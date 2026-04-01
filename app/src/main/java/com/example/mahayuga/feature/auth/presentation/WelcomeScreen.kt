// main/java/com/example/mahayuga/feature/auth/presentation/WelcomeScreen.kt
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
import com.example.mahayuga.core.common.* // ⚡ IMPORTED COMMON COMPONENTS
import com.example.mahayuga.ui.theme.* // ⚡ IMPORTED BRICX THEME
import kotlinx.coroutines.delay

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
            .background(BricxBackground) // ⚡ UPDATED
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = displayedText,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BricxTextPrimary // ⚡ UPDATED
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(1000)) + slideInVertically()
        ) {
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
                    color = BricxBrandTeal, // ⚡ UPDATED
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
            enter = fadeIn(
                tween(
                    1000,
                    delayMillis = 500
                )
            ) + slideInVertically(initialOffsetY = { 50 })
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ⚡ REPLACED RAW BUTTONS WITH BRICX COMPONENTS
                BricxPrimaryButton(
                    text = "Log in",
                    onClick = { navController.navigate("login") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    backgroundColor = BricxTextPrimary // White button for primary action here
                )

                BricxPrimaryButton(
                    text = "Sign up",
                    onClick = { navController.navigate("register") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    backgroundColor = BricxSurfaceCardLight // Secondary action look
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}