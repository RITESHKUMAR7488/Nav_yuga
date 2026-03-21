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
import com.example.mahayuga.feature.profile.presentation.ProfileViewModel
import kotlinx.coroutines.delay

@Composable
fun NavyugaSplashScreen(
    onSplashFinished: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val alpha = remember { Animatable(0f) }
    val context = LocalContext.current
    val biometricAuth = remember { BiometricAuthenticator(context) }

    LaunchedEffect(Unit) {
        // 1. Animate Logo
        alpha.animateTo(1f, animationSpec = tween(800))
        delay(1500) // Keep logo visible for 1.5s

        // 2. Trigger Biometrics (if Activity available)
        val activity = context as? FragmentActivity
        if (activity != null) {
            biometricAuth.authenticate(
                activity = activity,
                title = "Unlock BRICX",
                onSuccess = { onSplashFinished() },
                onError = { errorMsg ->
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            onSplashFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black), // Sleek black background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground), // Bricx Logo
                contentDescription = "BRICX Logo",
                modifier = Modifier
                    .size(140.dp)
                    .alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "BRICX",
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 2.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Investing For Tomorrow",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}