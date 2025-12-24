package com.example.mahayuga.feature.profile.presentation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.ui.theme.BrandBlue
import com.example.mahayuga.ui.theme.ErrorRed

private val AccountBg = Color(0xFF050505)
private val CardBg = Color(0xFF101920)
private val TextGrey = Color(0xFF888888)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    onBackClick: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userState by viewModel.currentUser.collectAsState()
    val actionState by viewModel.accountActionState.collectAsState()
    val context = LocalContext.current

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showEditPhoneDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(actionState) {
        when (val result = actionState) {
            is UiState.Success -> {
                Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
                viewModel.resetActionState()
                if (result.data == "Account Deleted") {
                    onAccountDeleted()
                }
            }

            is UiState.Failure -> {
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                viewModel.resetActionState()
            }

            else -> {}
        }
    }

    if (userState is UiState.Success) {
        val user = (userState as UiState.Success).data
        val initials = if (user.name.isNotEmpty()) user.name.take(2).uppercase() else "YA"

        Scaffold(
            containerColor = AccountBg,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "My Account",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = AccountBg)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Account Type
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, Color(0xFF1F2B36))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF1F2B36), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Individual Account",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = BrandBlue.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        null,
                                        tint = BrandBlue,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Verified",
                                        color = BrandBlue,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Profile
                AccountInfoCard(
                    title = "Profile",
                    content = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2A3441)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(initials, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    user.name,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        null,
                                        tint = BrandBlue,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        user.email,
                                        color = TextGrey,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    },
                    onUpdate = { showEditProfileDialog = true }
                )

                // 3. Phone
                AccountInfoCard(
                    title = "Phone",
                    content = {
                        Text(
                            text = user.phone.ifEmpty { "+91 -" },
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    onUpdate = { showEditPhoneDialog = true }
                )

                // ⚡ REMOVED: Investment Limit Card

                Spacer(modifier = Modifier.weight(1f))

                // 5. Close Account
                TextButton(
                    onClick = { showDeleteConfirmDialog = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "Close account",
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // --- DIALOGS ---

        if (showEditProfileDialog) {
            EditInfoDialog(
                title = "Update Profile",
                initialValue1 = user.name,
                label1 = "Full Name",
                initialValue2 = user.email,
                label2 = "Email Address",
                onDismiss = { showEditProfileDialog = false },
                onSave = { newName, newEmail ->
                    viewModel.updateUserProfile(newName, newEmail, user.phone)
                    showEditProfileDialog = false
                }
            )
        }

        if (showEditPhoneDialog) {
            EditInfoDialog(
                title = "Update Phone",
                initialValue1 = user.phone,
                label1 = "Phone Number",
                isSingleField = true,
                onDismiss = { showEditPhoneDialog = false },
                onSave = { newPhone, _ ->
                    viewModel.updateUserProfile(user.name, user.email, newPhone)
                    showEditPhoneDialog = false
                }
            )
        }

        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("Close Account?", color = ErrorRed) },
                text = {
                    Text(
                        "Are you sure you want to close your account? This action is permanent and cannot be undone.",
                        color = Color.Black
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.deleteAccount(); showDeleteConfirmDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                    ) {
                        Text("Yes, Close")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteConfirmDialog = false
                    }) { Text("Cancel") }
                },
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun AccountInfoCard(title: String, content: @Composable () -> Unit, onUpdate: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, Color(0xFF1F2B36))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, color = TextGrey, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) { content() }
                Button(
                    onClick = onUpdate,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) { Text("Update", fontSize = 12.sp) }
            }
        }
    }
}

@Composable
fun EditInfoDialog(
    title: String,
    initialValue1: String,
    label1: String,
    initialValue2: String = "",
    label2: String = "",
    isSingleField: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var val1 by remember { mutableStateOf(initialValue1) }
    var val2 by remember { mutableStateOf(initialValue2) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg), // ⚡ Dark Background
            border = BorderStroke(1.dp, Color(0xFF1F2B36))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Shared Text Field Colors
                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = BrandBlue,
                    unfocusedBorderColor = Color(0xFF1F2B36),
                    focusedLabelColor = BrandBlue,
                    unfocusedLabelColor = TextGrey,
                    cursorColor = BrandBlue
                )

                OutlinedTextField(
                    value = val1,
                    onValueChange = { val1 = it },
                    label = { Text(label1) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                if (!isSingleField) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = val2,
                        onValueChange = { val2 = it },
                        label = { Text(label2) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = TextGrey) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(val1, val2) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                    ) {
                        Text("Save", color = Color.White)
                    }
                }
            }
        }
    }
}