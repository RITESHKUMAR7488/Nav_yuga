package com.example.mahayuga.feature.auth.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.auth.presentation.components.NavyugaGradientButton
import com.example.mahayuga.feature.auth.presentation.components.NavyugaTextField
import com.example.mahayuga.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val registerState by viewModel.registerState.collectAsState()
    val context = LocalContext.current
    val datePickerState = rememberDatePickerState()

    // Handle Date Picker
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dob = formatter.format(Date(millis))
            showDatePicker = false
        }
    }

    LaunchedEffect(registerState) {
        if (registerState is UiState.Success) {
            Toast.makeText(context, "Request Sent! Waiting for Admin Approval.", Toast.LENGTH_LONG).show()
            // Navigate back to Login, not Hub
            navController.navigate("login") {
                popUpTo("login") { inclusive = true }
            }
        } else if (registerState is UiState.Failure) {
            Toast.makeText(context, (registerState as UiState.Failure).message, Toast.LENGTH_LONG).show()
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Join Navyuga",
            style = MaterialTheme.typography.headlineMedium,
            color = TextWhiteHigh,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        NavyugaTextField(value = name, onValueChange = { name = it }, label = "Full Name", icon = Icons.Default.Person)
        Spacer(modifier = Modifier.height(16.dp))

        // DOB Field
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = dob,
                onValueChange = {},
                label = { Text("Date of Birth") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, null, tint = BrandBlue) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = TextWhiteHigh,
                    disabledBorderColor = BrandBlue.copy(alpha = 0.5f),
                    disabledLabelColor = TextWhiteMedium,
                    disabledLeadingIconColor = BrandBlue
                )
            )
            Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
        }

        Spacer(modifier = Modifier.height(16.dp))
        NavyugaTextField(value = email, onValueChange = { email = it }, label = "Email Address", icon = Icons.Default.Email)
        Spacer(modifier = Modifier.height(16.dp))
        NavyugaTextField(value = password, onValueChange = { password = it }, label = "Password", icon = Icons.Default.Lock, isPassword = true)

        Spacer(modifier = Modifier.height(32.dp))

        NavyugaGradientButton(
            text = "Request Account",
            isLoading = registerState is UiState.Loading,
            onClick = {
                if (name.isBlank() || email.isBlank() || password.isBlank() || dob.isBlank()) {
                    Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.register(name, email, password, dob)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Already have an account? Login", color = BrandBlue)
        }
    }
}