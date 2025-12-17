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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mahayuga.R
import kotlinx.coroutines.delay

// Dark Theme Colors
private val GptBlack = Color(0xFF000000)
private val GptDarkSurface = Color(0xFF1E1E1E)
private val GptTextWhite = Color(0xFFFFFFFF)
private val GptTextGrey = Color(0xFFC5C5D2)

@Composable
fun WelcomeScreen(
    navController: NavController
) {
    var showContent by remember { mutableStateOf(false) }
    val textToType = "Welcome to Mahayuga"
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        showContent = true
        textToType.forEachIndexed { index, _ ->
            displayedText = textToType.substring(0, index + 1)
            delay(50)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GptBlack) // Black Background
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Logo Area
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(1000)) + slideInVertically()
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(GptDarkSurface, shape = MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.white),
                    contentDescription = "Logo",
                    modifier = Modifier.size(60.dp),
                    colorFilter = ColorFilter.tint(GptTextWhite) // White Logo
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Typing Text (White)
        Text(
            text = displayedText,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GptTextWhite
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        // Buttons
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(1000, delayMillis = 1000)) + slideInVertically(initialOffsetY = { 50 })
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Log In: White Button, Black Text
                Button(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GptTextWhite,
                        contentColor = GptBlack
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                // Sign Up: Dark Surface, White Text
                Button(
                    onClick = { navController.navigate("register") },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GptDarkSurface,
                        contentColor = GptTextWhite
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