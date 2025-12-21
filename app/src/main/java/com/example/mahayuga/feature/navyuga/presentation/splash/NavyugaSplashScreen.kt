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
        (currentUserState as UiState.Success).data.name.ifEmpty { "Yaman" }
    } else "Yaman"

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(800))
        delay(1000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.navyuga),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .alpha(alpha.value)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Welcome to Navyuga,\n$userName",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alpha.value)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "A New Era Of Investing",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}