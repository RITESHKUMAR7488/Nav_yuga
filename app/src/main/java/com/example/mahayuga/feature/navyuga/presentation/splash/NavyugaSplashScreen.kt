package com.example.mahayuga.feature.navyuga.presentation.splash

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mahayuga.R
import com.example.mahayuga.core.common.BiometricAuthenticator
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
    val context = LocalContext.current
    val biometricAuth = remember { BiometricAuthenticator(context) }

    val userName = if (currentUserState is UiState.Success) {
        val fullName = (currentUserState as UiState.Success).data.name
        if (fullName.isNotBlank()) fullName.trim().substringBefore(" ") else "User"
    } else "User"

    LaunchedEffect(Unit) {
        // 1. Animate Logo
        alpha.animateTo(1f, animationSpec = tween(800))
        delay(1500) // Keep logo visible for 1.5s

        // 2. Trigger Biometrics (if Activity available)
        val activity = context as? FragmentActivity
        if (activity != null) {
            biometricAuth.authenticate(
                activity = activity,
                title = "Unlock Navyuga",
                onSuccess = { onSplashFinished() },
                onError = { errorMsg ->
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    // Optional: You might want to finish the app or allow retry here
                }
            )
        } else {
            // Fallback if not fragment activity (shouldn't happen)
            onSplashFinished()
        }
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
            Text(
                text = "Hi $userName!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(90.dp))

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

            Spacer(modifier = Modifier.height(90.dp))

            Text(
                text = "A New Era Of Real Estate Investing",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}