package com.example.mahayuga.feature.profile.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mahayuga.core.common.BricxTopAppBar // ⚡ IMPORTED COMMON COMPONENT
import com.example.mahayuga.feature.navyuga.presentation.home.InstagramStylePropertyCard
import com.example.mahayuga.ui.theme.* // ⚡ IMPORTED BRICX THEME

@Composable
fun LikedPropertiesScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val likedProperties by viewModel.likedProperties.collectAsState()

    Scaffold(
        containerColor = BricxBackground, // ⚡ UPDATED
        topBar = {
            // ⚡ REPLACED RAW TOPAPPBAR WITH BRICXTOPAPPBAR
            BricxTopAppBar(
                title = "Liked Properties",
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        if (likedProperties.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No liked properties found.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BricxTextSecondary // ⚡ UPDATED
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(likedProperties) { property ->
                    InstagramStylePropertyCard(
                        property = property,
                        onItemClick = { onNavigateToDetail(property.id) },
                        onLikeClick = { viewModel.toggleLike(property.id, property.isLiked) },
                        onShareClick = { /* Handle share */ },
                        onInvestClick = { onNavigateToDetail(property.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}