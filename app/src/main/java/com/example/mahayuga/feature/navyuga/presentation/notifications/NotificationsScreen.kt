// main/java/com/example/mahayuga/feature/navyuga/presentation/notifications/NotificationsScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.core.common.BricxTopAppBar // ⚡ IMPORTED COMMON COMPONENT
import com.example.mahayuga.ui.theme.* // ⚡ IMPORTED BRICX THEME
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val source: String,
    val time: String,
    val icon: ImageVector
)

class NotificationsViewModel : ViewModel() {
    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    init {
        fetchNotifications()
    }

    private fun fetchNotifications() {
        viewModelScope.launch {
            delay(500)
            _notifications.value = listOf(
                AppNotification(
                    "1",
                    "New Property Listed",
                    "Embassy REIT has listed a new commercial property in Pune.",
                    "BricX",
                    "2 mins ago",
                    Icons.Default.Update
                ),
                AppNotification(
                    "2",
                    "Dividend Received",
                    "Your Q2 dividend for Mindspace REIT has been credited.",
                    "Asset Manager",
                    "1 hour ago",
                    Icons.Default.Receipt
                ),
                AppNotification(
                    "3",
                    "SEBI Guidelines Updated",
                    "Please review the updated SM REIT taxation guidelines.",
                    "News/Updates",
                    "1 day ago",
                    Icons.Default.Info
                )
            )
        }
    }
}

@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = BricxBackground, // ⚡ UPDATED
        topBar = {
            // ⚡ REPLACED RAW TOPAPPBAR WITH BRICXTOPAPPBAR
            BricxTopAppBar(
                title = "Notifications",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (notifications.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BricxBrandTeal) // ⚡ UPDATED
                    }
                }
            } else {
                items(notifications, key = { it.id }) { notification ->
                    NotificationCard(notification)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: AppNotification) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard), // ⚡ UPDATED
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BricxBorder) // ⚡ UPDATED
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BricxBrandTeal.copy(alpha = 0.2f)), // ⚡ UPDATED
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.icon,
                    contentDescription = null,
                    tint = BricxBrandTeal, // ⚡ UPDATED
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = notification.source,
                        color = BricxBrandTeal, // ⚡ UPDATED
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = notification.time,
                        color = BricxTextSecondary,
                        fontSize = 10.sp
                    ) // ⚡ UPDATED
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.title,
                    color = BricxTextPrimary, // ⚡ UPDATED
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    color = BricxTextSecondary,
                    fontSize = 14.sp
                ) // ⚡ UPDATED
            }
        }
    }
}