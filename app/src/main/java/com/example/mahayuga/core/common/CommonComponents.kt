// main/java/com/example/mahayuga/core/common/CommonComponents.kt
package com.example.mahayuga.core.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mahayuga.ui.theme.*
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector

// ==========================================
// 1. NAVIGATION & LAYOUTS
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BricxTopAppBar(
    title: String,
    onNavigateBack: () -> Unit,
    showTrailingIcons: Boolean = false,
    isWatchlisted: Boolean = false,
    onShareClick: () -> Unit = {},
    onWatchlistClick: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title, color = BricxTextPrimary, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BricxTextPrimary
                )
            }
        },
        actions = {
            if (showTrailingIcons) {
                Row(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .background(BricxSurfaceCard, RoundedCornerShape(50))
                        .border(1.dp, BricxDivider, RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = BricxTextPrimary,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onShareClick() }
                    )
                    Icon(
                        imageVector = if (isWatchlisted) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Watchlist",
                        tint = if (isWatchlisted) BricxBrandTeal else BricxTextPrimary,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onWatchlistClick() }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BricxBackground)
    )
}

@Composable
fun StickyTradeBottomBar(
    onSipClick: () -> Unit,
    onSellClick: () -> Unit,
    onBuyClick: () -> Unit
) {
    Surface(color = BricxSurfaceCard, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BricxSecondaryButton(
                text = "SIP",
                onClick = onSipClick,
                modifier = Modifier
                    .weight(0.6f)
                    .height(48.dp)
            )
            BricxPrimaryButton(
                text = "SELL",
                onClick = onSellClick,
                backgroundColor = BricxDangerRed,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            )
            BricxPrimaryButton(
                text = "BUY",
                onClick = onBuyClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            )
        }
    }
}

// ==========================================
// 2. BUTTONS & INPUTS
// ==========================================

@Composable
fun BricxPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BricxBrandTeal,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.5f)
        ),
        enabled = enabled
    ) {
        Text(text, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun BricxSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = BricxTextPrimary),
        border = BorderStroke(1.dp, BricxBorder)
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BricxTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier.fillMaxWidth(),
    leadingIcon: @Composable (() -> Unit)? = null,
    onImeAction: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(onAny = { onImeAction() }),
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        leadingIcon = leadingIcon,
        trailingIcon = {
            if (isPassword) {
                val image =
                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = image,
                        contentDescription = "Toggle password visibility",
                        tint = BricxTextSecondary
                    )
                }
            }
        },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BricxBrandTeal,
            unfocusedBorderColor = BricxBorderLight,
            focusedLabelColor = BricxBrandTeal,
            unfocusedLabelColor = BricxTextSecondary,
            focusedContainerColor = BricxSurfaceCardLight,
            unfocusedContainerColor = BricxSurfaceCardLight,
            focusedTextColor = BricxTextPrimary,
            unfocusedTextColor = BricxTextPrimary,
            cursorColor = BricxBrandTeal
        )
    )
}

// ==========================================
// 3. DATA DISPLAY COMPONENTS
// ==========================================

@Composable
fun DetailGrid(items: List<Pair<String, String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BricxSurfaceCard, RoundedCornerShape(12.dp))
            .border(1.dp, BricxBorder, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items.forEach { (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = label,
                    color = BricxTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = value,
                    color = BricxTextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun ExpandableFinanceSection(title: String, data: List<Pair<String, String>>) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = BricxTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand",
                tint = BricxTextSecondary
            )
        }
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp, start = 4.dp, end = 4.dp)
            ) {
                data.chunked(2).forEach { rowItems ->
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)) {
                        rowItems.forEach { item ->
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.first, color = BricxTextSecondary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    item.second,
                                    color = BricxTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider(color = BricxBorder, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun PropertyMiniCard(
    propertyName: String,
    title: String,
    openPrice: String,
    lastPrice: String,
    imageUrl: String? = null,
    sparklineData: List<Float> = listOf(10f, 15f, 12f, 20f, 18f, 25f, 30f)
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(240.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BricxSurfaceCard),
        border = BorderStroke(1.dp, BricxBorder)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(BricxSurfaceCardLight)
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF0F4F8).copy(alpha = 0.1f))
                    ) // Placeholder
                }
            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f)
                .padding(12.dp)) {
                Text(
                    propertyName,
                    color = BricxTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    title,
                    color = BricxTextSecondary,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Dynamic Sparkline
                val isPositive =
                    if (sparklineData.size >= 2) sparklineData.last() >= sparklineData.first() else true
                val sparkColor = if (isPositive) BricxSuccessGreen else BricxDangerRed
                SparklineGraph(
                    data = sparklineData,
                    color = sparkColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Open Price:", color = BricxTextSecondary, fontSize = 10.sp)
                    Text("₹$openPrice", color = BricxTextMuted, fontSize = 10.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Last Price:", color = BricxTextSecondary, fontSize = 10.sp)
                    Text(
                        "₹$lastPrice",
                        color = BricxTextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DataMetricRow(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = BricxTextSecondary, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = BricxTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

// ==========================================
// 4. VISUALS & GRAPHS
// ==========================================

@Composable
fun PortfolioDonutChart(
    values: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    strokeWidthDp: Float = 30f
) {
    require(values.size == colors.size) { "Values and Colors lists must be the same size." }

    Canvas(modifier = modifier) {
        val total = values.sum()
        var currentStartAngle = -90f // Start from the top
        val strokeWidthPx = strokeWidthDp.dp.toPx()

        if (total == 0f) {
            // Draw empty state circle
            drawArc(
                color = BricxBorder,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidthPx)
            )
            return@Canvas
        }

        values.forEachIndexed { index, value ->
            val sweepAngle = (value / total) * 360f
            drawArc(
                color = colors[index],
                startAngle = currentStartAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidthPx)
            )
            currentStartAngle += sweepAngle
        }
    }
}

@Composable
fun SparklineGraph(
    data: List<Float>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (data.isEmpty() || data.size == 1) return@Canvas

        val maxVal = data.maxOrNull() ?: 1f
        val minVal = data.minOrNull() ?: 0f
        val range = if (maxVal == minVal) 1f else maxVal - minVal

        val stepX = size.width / (data.size - 1)
        val path = Path()

        data.forEachIndexed { index, value ->
            val x = index * stepX
            // Normalize Y: (value - min) / range. Multiply by height. Invert since Y=0 is top.
            val normalizedY = (value - minVal) / range
            val y = size.height - (normalizedY * size.height)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
// ⚡ THE NEW UNIVERSAL HUB HEADER
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BricxHubTopAppBar(
    title: String,
    icon: ImageVector,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onMessageClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                Icon(icon, contentDescription = title, tint = BricxBrandTeal, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, color = BricxTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        },
        actions = {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(end = 16.dp)) {
                GroupedHeaderIcons(listOf(Icons.Outlined.Search to onSearchClick))
                GroupedHeaderIcons(
                    listOf(
                        Icons.Outlined.Notifications to onNotificationClick,
                        Icons.AutoMirrored.Outlined.Send to onMessageClick
                    )
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BricxBackground,
            scrolledContainerColor = BricxBackground
        )
    )
}

// ⚡ MOVED FROM HOMESCREEN TO MAKE IT UNIVERSALLY REUSABLE
@Composable
fun GroupedHeaderIcons(icons: List<Pair<ImageVector, () -> Unit>>) {
    Row(
        modifier = Modifier
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(50))
            .background(BricxSurfaceCard.copy(alpha = 0.85f), RoundedCornerShape(50))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icons.forEach { (icon, onClick) ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BricxTextPrimary,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
            )
        }
    }
}