package com.example.navyuga.feature.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.navyuga.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavyugaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    errorMessage: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val borderColor = if (errorMessage != null) ErrorRed else BorderStroke

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = TextWhiteMedium) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = BrandBlue) },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = TextWhiteMedium
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            // ⚡ Fixed Material 3 Colors
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MidnightSurface,
                unfocusedContainerColor = MidnightSurface,
                focusedBorderColor = CyanAccent,
                unfocusedBorderColor = borderColor,
                errorBorderColor = ErrorRed,
                focusedLabelColor = CyanAccent,
                unfocusedLabelColor = TextWhiteMedium,
                cursorColor = CyanAccent,
                focusedTextColor = TextWhiteHigh,
                unfocusedTextColor = TextWhiteHigh
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = ErrorRed,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun NavyugaGradientButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        contentPadding = PaddingValues(0.dp), // Important for gradient to fill the button
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryGradient), // Uses the brush defined in Color.kt
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

}
@Preview(showBackground = true, backgroundColor = 0xFF0B0E14) // Midnight Blue Hex
@Composable
fun ComponentsPreview() {
    NavyugaTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            NavyugaTextField(
                value = "Test Input",
                onValueChange = {},
                label = "Sample Field",
                icon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(16.dp))

            NavyugaGradientButton(
                text = "Test Button",
                onClick = {}
            )
        }
    }
}