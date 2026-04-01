// main/java/com/example/mahayuga/feature/navyuga/presentation/messages/MessagesScreen.kt
package com.example.mahayuga.feature.navyuga.presentation.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.mahayuga.ui.theme.* // ⚡ IMPORTED BRICX THEME
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: String
)

class MessagesViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    init {
        loadChatHistory()
    }

    private fun loadChatHistory() {
        _messages.value = listOf(
            ChatMessage(
                "1",
                "Hello! I am the automated assistant. How can I help you regarding our properties today?",
                false,
                "10:00 AM"
            )
        )
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(System.currentTimeMillis().toString(), text, true, "Now")
        _messages.value = _messages.value + userMsg

        viewModelScope.launch {
            delay(1500)
            val botReply = ChatMessage(
                (System.currentTimeMillis() + 1).toString(),
                "Your inquiry regarding '$text' has been securely forwarded to our Asset Managers. Sharing contact numbers is restricted to protect your privacy. We will reply here shortly.",
                false,
                "Now"
            )
            _messages.value = _messages.value + botReply
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    assetManagerName: String = "Embassy Group Support",
    onNavigateBack: () -> Unit,
    viewModel: MessagesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    var currentInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = BricxBackground, // ⚡ UPDATED
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Message",
                            color = BricxTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ) // ⚡ UPDATED
                        Text(
                            assetManagerName,
                            color = BricxBrandTeal,
                            fontSize = 12.sp
                        ) // ⚡ UPDATED
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BricxTextPrimary
                        ) // ⚡ UPDATED
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BricxBackground) // ⚡ UPDATED
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BricxSurfaceCard) // ⚡ UPDATED
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = currentInput,
                    onValueChange = { currentInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Type a message...",
                            color = BricxTextSecondary
                        )
                    }, // ⚡ UPDATED
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = BricxTextPrimary, // ⚡ UPDATED
                        unfocusedTextColor = BricxTextPrimary, // ⚡ UPDATED
                        focusedContainerColor = BricxBackground, // ⚡ UPDATED
                        unfocusedContainerColor = BricxBackground // ⚡ UPDATED
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        viewModel.sendMessage(currentInput)
                        currentInput = ""
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(BricxBrandTeal) // ⚡ UPDATED
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BricxWarningOrange.copy(alpha = 0.1f))
                    .padding(12.dp), // ⚡ UPDATED
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "For your safety, communication is restricted to this app. Exchanging phone numbers is not permitted.",
                    color = BricxWarningOrange, // ⚡ UPDATED
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(message = msg)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (message.isFromUser) BricxBrandTeal else BricxSurfaceCardLight // ⚡ UPDATED
    val shape = if (message.isFromUser) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(shape)
                .background(bubbleColor)
                .padding(12.dp)
        ) {
            Text(text = message.text, color = BricxTextPrimary, fontSize = 15.sp) // ⚡ UPDATED
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message.timestamp,
                color = BricxTextPrimary.copy(alpha = 0.5f), // ⚡ UPDATED
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}