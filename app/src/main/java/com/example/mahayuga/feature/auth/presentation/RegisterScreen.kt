package com.example.mahayuga.feature.auth.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.auth.presentation.components.GptTextField
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Dark Theme Colors
private val GptBlack = Color(0xFF000000)
private val GptTextWhite = Color(0xFFFFFFFF)
private val GptTextGrey = Color(0xFFC5C5D2)
private val GptBrandGreen = Color(0xFF10A37F)
private val GptInputBackground = Color(0xFF1E1E1E)
private val GptInputBorder = Color(0xFF3E3E3E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val registerState by viewModel.registerState.collectAsState()

    // Form States
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") } // ⚡ New Phone State
    var password by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    // Planet State
    var selectedPlanet by remember { mutableStateOf("Earth") }
    var isPlanetExpanded by remember { mutableStateOf(false) }
    val planets = listOf("Mars", "Earth", "Venus")
    var planetError by remember { mutableStateOf<String?>(null) }

    // Date Picker State
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val context = LocalContext.current

    LaunchedEffect(registerState) {
        if (registerState is UiState.Success) {
            val successMessage = (registerState as UiState.Success<String>).data
            Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show()
            navController.navigate("login") {
                popUpTo("welcome") { inclusive = true }
            }
        }
    }

    val isLoading = registerState is UiState.Loading
    val errorMessage = (registerState as? UiState.Failure)?.message

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GptBlack)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()), // Added scroll for small screens
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Create your account",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GptTextWhite
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Name
        GptTextField(
            value = name,
            onValueChange = { name = it },
            label = "Full Name",
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email
        GptTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email address",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ⚡ Phone Number Field
        GptTextField(
            value = phone,
            onValueChange = { if (it.all { char -> char.isDigit() }) phone = it },
            label = "Phone Number",
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🗓️ DOB Field with M3 DatePicker
        OutlinedTextField(
            value = dob,
            onValueChange = { dob = it }, // Allow manual typing
            label = { Text("Date of Birth (DD/MM/YYYY)") },
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GptBrandGreen,
                unfocusedBorderColor = GptInputBorder,
                focusedLabelColor = GptBrandGreen,
                unfocusedLabelColor = GptTextGrey,
                cursorColor = GptBrandGreen,
                focusedContainerColor = GptInputBackground,
                unfocusedContainerColor = GptInputBackground,
                focusedTextColor = GptTextWhite,
                unfocusedTextColor = GptTextWhite
            ),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Select Date",
                        tint = GptTextGrey
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel", color = GptBrandGreen)
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = GptInputBackground,
                    titleContentColor = GptTextWhite,
                    headlineContentColor = GptTextWhite,
                    weekdayContentColor = GptTextGrey,
                    dayContentColor = GptTextWhite,
                    selectedDayContainerColor = GptBrandGreen,
                    selectedDayContentColor = GptTextWhite,
                    todayContentColor = GptBrandGreen,
                    todayDateBorderColor = GptBrandGreen,
                    yearContentColor = GptTextWhite,
                    currentYearContentColor = GptBrandGreen,
                    selectedYearContainerColor = GptBrandGreen,
                    selectedYearContentColor = GptTextWhite
                )
            ) {
                DatePicker(state = datePickerState)
                LaunchedEffect(datePickerState.selectedDateMillis) {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        formatter.timeZone = TimeZone.getTimeZone("UTC")
                        dob = formatter.format(Date(millis))
                        showDatePicker = false
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🌍 Planet Dropdown
        ExposedDropdownMenuBox(
            expanded = isPlanetExpanded,
            onExpandedChange = { isPlanetExpanded = !isPlanetExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedPlanet,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Planet") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPlanetExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GptBrandGreen,
                    unfocusedBorderColor = GptInputBorder,
                    focusedLabelColor = GptBrandGreen,
                    unfocusedLabelColor = GptTextGrey,
                    focusedContainerColor = GptInputBackground,
                    unfocusedContainerColor = GptInputBackground,
                    focusedTextColor = GptTextWhite,
                    unfocusedTextColor = GptTextWhite
                )
            )

            ExposedDropdownMenu(
                expanded = isPlanetExpanded,
                onDismissRequest = { isPlanetExpanded = false },
                modifier = Modifier.background(GptInputBackground)
            ) {
                planets.forEach { planet ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = planet,
                                color = if (planet == "Earth") GptTextWhite else Color.Gray
                            )
                        },
                        onClick = {
                            isPlanetExpanded = false
                            if (planet == "Earth") {
                                selectedPlanet = planet
                                planetError = null
                            } else {
                                planetError = "Sorry, services are not yet available on $planet."
                                selectedPlanet = "Earth"
                            }
                        }
                    )
                }
            }
        }

        if (planetError != null) {
            Text(
                text = planetError!!,
                color = Color(0xFFFF4444),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        GptTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = { viewModel.register(name, email, password, dob, phone) }, // ⚡ Pass phone
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GptTextWhite,
                contentColor = GptBlack
            ),
            shape = MaterialTheme.shapes.medium,
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = GptBlack, modifier = Modifier.size(24.dp))
            } else {
                Text("Sign up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Already have an account?", color = GptTextGrey)
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Log in", color = GptBrandGreen, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}