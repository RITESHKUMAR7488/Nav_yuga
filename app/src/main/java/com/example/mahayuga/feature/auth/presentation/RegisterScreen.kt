// main/java/com/example/mahayuga/feature/auth/presentation/RegisterScreen.kt
package com.example.mahayuga.feature.auth.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mahayuga.core.common.* // ⚡ IMPORTED COMMON COMPONENTS
import com.example.mahayuga.ui.theme.* // ⚡ IMPORTED BRICX THEME
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val registerState by viewModel.registerState.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    var selectedPlanet by remember { mutableStateOf("Earth") }
    var isPlanetExpanded by remember { mutableStateOf(false) }
    val planets = listOf("Mars", "Earth", "Venus")
    var planetError by remember { mutableStateOf<String?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val context = LocalContext.current

    LaunchedEffect(registerState) {
        if (registerState is UiState.Success) {
            val successMessage = (registerState as UiState.Success<String>).data
            Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show()
            navController.navigate("login") { popUpTo("welcome") { inclusive = true } }
        }
    }

    val isLoading = registerState is UiState.Loading
    val errorMessage = (registerState as? UiState.Failure)?.message

    Scaffold(
        containerColor = BricxBackground,
        topBar = {
            BricxTopAppBar(
                title = "BRICX",
                onNavigateBack = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Create your account",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BricxTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(32.dp))

            BricxTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(16.dp))

            BricxTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email address",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(16.dp))

            BricxTextField(
                value = phone,
                onValueChange = { if (it.all { char -> char.isDigit() }) phone = it },
                label = "Phone Number",
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it },
                label = { Text("Date of Birth (DD/MM/YYYY)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BricxBrandTeal,
                    unfocusedBorderColor = BricxBorderLight,
                    focusedLabelColor = BricxBrandTeal,
                    unfocusedLabelColor = BricxTextSecondary,
                    cursorColor = BricxBrandTeal,
                    focusedContainerColor = BricxSurfaceCardLight,
                    unfocusedContainerColor = BricxSurfaceCardLight,
                    focusedTextColor = BricxTextPrimary,
                    unfocusedTextColor = BricxTextPrimary
                ),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Select Date",
                            tint = BricxTextSecondary
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
                        TextButton(onClick = {
                            showDatePicker = false
                        }) { Text("Cancel", color = BricxBrandTeal) }
                    },
                    colors = DatePickerDefaults.colors(
                        containerColor = BricxSurfaceCardLight,
                        titleContentColor = BricxTextPrimary,
                        headlineContentColor = BricxTextPrimary,
                        weekdayContentColor = BricxTextSecondary,
                        dayContentColor = BricxTextPrimary,
                        selectedDayContainerColor = BricxBrandTeal,
                        selectedDayContentColor = BricxTextPrimary,
                        todayContentColor = BricxBrandTeal,
                        todayDateBorderColor = BricxBrandTeal,
                        yearContentColor = BricxTextPrimary,
                        currentYearContentColor = BricxBrandTeal,
                        selectedYearContainerColor = BricxBrandTeal,
                        selectedYearContentColor = BricxTextPrimary
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BricxBrandTeal,
                        unfocusedBorderColor = BricxBorderLight,
                        focusedLabelColor = BricxBrandTeal,
                        unfocusedLabelColor = BricxTextSecondary,
                        focusedContainerColor = BricxSurfaceCardLight,
                        unfocusedContainerColor = BricxSurfaceCardLight,
                        focusedTextColor = BricxTextPrimary,
                        unfocusedTextColor = BricxTextPrimary
                    )
                )
                ExposedDropdownMenu(
                    expanded = isPlanetExpanded,
                    onDismissRequest = { isPlanetExpanded = false },
                    modifier = Modifier.background(BricxSurfaceCardLight)
                ) {
                    planets.forEach { planet ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = planet,
                                    color = if (planet == "Earth") BricxTextPrimary else Color.Gray
                                )
                            },
                            onClick = {
                                isPlanetExpanded = false
                                if (planet == "Earth") {
                                    selectedPlanet = planet; planetError = null
                                } else {
                                    planetError =
                                        "Sorry, services are not yet available on $planet."; selectedPlanet =
                                        "Earth"
                                }
                            }
                        )
                    }
                }
            }

            if (planetError != null) Text(
                text = planetError!!,
                color = BricxDangerRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            BricxTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                isPassword = true,
                onImeAction = { viewModel.register(name, email, password, dob, phone) })
            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage != null) Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(color = BricxBrandTeal)
            } else {
                BricxPrimaryButton(
                    text = "Sign up",
                    onClick = { viewModel.register(name, email, password, dob, phone) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account?", color = BricxTextSecondary)
                TextButton(onClick = { navController.navigate("login") }) {
                    Text("Log in", color = BricxBrandTeal, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}