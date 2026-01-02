package com.example.mahayuga.feature.navyuga.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mahayuga.R
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.profile.presentation.ProfileViewModel
import kotlinx.coroutines.delay

@Composable
fun NavyugaSplashScreen(
    onSplashFinished: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val currentUserState by profileViewModel.currentUser.collectAsState()
    val alpha = remember { Animatable(0f) }

    val userName = if (currentUserState is UiState.Success) {
        val fullName = (currentUserState as UiState.Success).data.name
        if (fullName.isNotBlank()) fullName.trim().substringBefore(" ") else "User"
    } else "User"

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(800))
        delay(1500)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Top Text: Greeting
            Text(
                text = "Hi $userName!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.alpha(alpha.value)
            )

            // ⚡ INCREASED SPACING (48.dp -> 90.dp)
            Spacer(modifier = Modifier.height(90.dp))

            // 2. Center Content: Logo & Title
            Image(
                painter = painterResource(id = R.drawable.navyuga),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .alpha(alpha.value)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "NAVYUGA",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alpha.value)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ownership For All",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.alpha(alpha.value)
            )

            // ⚡ INCREASED SPACING (48.dp -> 90.dp)
            Spacer(modifier = Modifier.height(90.dp))

            // 3. Bottom Text: Tagline
            Text(
                text = "A New Era Of Real Estate Investing",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}