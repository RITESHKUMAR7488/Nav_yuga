package com.example.navyuga.feature.profile.presentation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.navyuga.R
import com.example.navyuga.core.common.UiState
import com.example.navyuga.feature.profile.data.model.DocumentModel
import com.example.navyuga.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onLogout: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()
    val documents by viewModel.documents.collectAsState()
    val uploadState by viewModel.uploadState.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedDocId by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadDocument(it, selectedDocId) }
    }

    LaunchedEffect(uploadState) {
        if (uploadState is UiState.Success) {
            Toast.makeText(context, "Uploaded Successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerContentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(300.dp)
            ) {
                // DRAWER HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1F2937))
                                .padding(12.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text("eStake Investor", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("investor@estake.com", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // DRAWER ITEMS
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    DrawerSection("Main")
                    DrawerItem(Icons.Default.School, "Education") {}
                    DrawerItem(Icons.Default.Apartment, "My Properties") {}

                    DrawerSection("My Wallet")
                    DrawerItem(Icons.Default.AccountBalanceWallet, "Balance: ₹0.00") {}
                    DrawerItem(Icons.Default.ReceiptLong, "Transactions") {}
                    DrawerItem(Icons.Default.AddCard, "Add Funds") {}
                    DrawerItem(Icons.Default.Payments, "Withdraw") {}

                    DrawerSection("Account")
                    DrawerItem(Icons.Default.Person, "Profile") {}
                    DrawerItem(Icons.Default.Settings, "Settings") {}

                    // Logout
                    DrawerItem(Icons.Default.Logout, "Logout", isDestructive = true) {
                        viewModel.logout()
                        scope.launch { drawerState.close() }
                        onLogout()
                    }

                    DrawerSection("Support")
                    DrawerItem(Icons.Default.Help, "Help Center") {}
                    DrawerItem(Icons.Default.SupportAgent, "Contact Us") {}
                    DrawerItem(Icons.Default.PrivacyTip, "Privacy Policy") {}

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. HEADER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ritesh Kumar",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )

                    // Theme Toggle
                    IconButton(onClick = onThemeToggle) {
                        Crossfade(targetState = isDarkTheme, label = "theme") { isDark ->
                            val iconRes = if (isDark) R.drawable.ic_sun else R.drawable.ic_moon
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = "Theme",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    // Menu Toggle
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, "Menu", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }

                // 2. AVATAR
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1F2937))
                        .padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // 3. MY PORTFOLIO (Stats)
                Text(
                    text = "My Portfolio",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 24.dp, bottom = 16.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(stats) { stat ->
                        PortfolioStatCard(
                            label = stat.title,
                            value = stat.value,
                            progress = stat.progress,
                            color = Color(stat.colorHex)
                        )
                    }
                }

                // 4. MY PROFILE (Uploads)
                Text(
                    text = "My Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 32.dp, bottom = 16.dp)
                )

                if (uploadState is UiState.Loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp))
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(documents.size) { index ->
                        UploadPropertyCard(
                            index = index + 1,
                            onClick = {
                                selectedDocId = documents[index].id
                                launcher.launch("image/*")
                            }
                        )
                    }
                }

                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(start = 24.dp, top = 24.dp)
                        .height(48.dp)
                ) {
                    Text("Add Property", color = Color.White)
                }

                Text(
                    text = "Click any slot to upload an image.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, end = 24.dp, bottom = 100.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PortfolioStatCard(label: String, value: String, progress: Float, color: Color) {
    Card(
        modifier = Modifier.width(160.dp).wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3441))
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp).padding(bottom = 12.dp)) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF2A3441),
                    strokeWidth = 8.dp,
                )
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = color,
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round
                )
            }
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun UploadPropertyCard(index: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(140.dp).height(180.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3441)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Icon(Icons.Default.MoreVert, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp).align(Alignment.TopEnd))
            Column(
                modifier = Modifier.align(Alignment.CenterStart).padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Icon(painterResource(id = R.drawable.ic_check_circle), null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    Text("Upload", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Property $index", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                }
            }
            Text("#$index", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.BottomStart))
        }
    }
}

@Composable
fun DrawerSection(title: String) {
    Text(text = title, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp))
}

@Composable
fun DrawerItem(icon: ImageVector, label: String, isDestructive: Boolean = false, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label) },
        selected = false,
        onClick = onClick,
        icon = { Icon(icon, null, tint = if (isDestructive) ErrorRed else MaterialTheme.colorScheme.onSurfaceVariant) },
        colors = NavigationDrawerItemDefaults.colors(unselectedTextColor = if (isDestructive) ErrorRed else MaterialTheme.colorScheme.onSurface),
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}