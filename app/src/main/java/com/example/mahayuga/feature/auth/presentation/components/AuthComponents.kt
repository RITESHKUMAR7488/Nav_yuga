package com.example.mahayuga.feature.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.example.mahayuga.ui.theme.ErrorRed
import com.example.mahayuga.ui.theme.PrimaryGradient
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.min

// ==========================================
// ⚡ UNIVERSAL HELPER FUNCTIONS (UPDATED FOR Cr/L)
// ==========================================

fun formatIndian(amount: Double): String {
    return try {
        when {
            amount >= 10000000 -> {
                val cr = amount / 10000000
                // If it's a whole number like 1.00 Cr, show 1 Cr, else 1.52 Cr
                if (cr % 1.0 == 0.0) String.format("%.0f Cr", cr) else String.format("%.2f Cr", cr)
            }
            amount >= 100000 -> {
                val l = amount / 100000
                if (l % 1.0 == 0.0) String.format("%.0f L", l) else String.format("%.2f L", l)
            }
            else -> {
                val formatter = NumberFormat.getInstance(Locale("en", "IN"))
                formatter.maximumFractionDigits = 0
                formatter.format(amount)
            }
        }
    } catch (e: Exception) {
        String.format("%.0f", amount)
    }
}

// Overload for String input (Safe parsing)
fun formatIndian(amount: String?): String {
    if (amount.isNullOrBlank()) return "-"
    // Remove existing commas if any before parsing
    val cleanString = amount.replace(",", "").replace("₹", "").trim()
    val d = cleanString.toDoubleOrNull() ?: return amount
    return formatIndian(d)
}

// ==========================================
// ⚡ VISUAL TRANSFORMATION (Auto-Commas)
// ==========================================

class IndianNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        if (original.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val parts = original.split(".")
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1) "." + parts[1] else ""

        val formattedInteger = formatIndianInteger(integerPart)
        val formatted = formattedInteger + decimalPart

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val separatorIndex = original.indexOf('.')
                val offsetInInteger = if (separatorIndex == -1 || offset <= separatorIndex) offset else separatorIndex
                val commasAdded = countCommasAdded(integerPart.take(offsetInInteger))

                return if (separatorIndex != -1 && offset > separatorIndex) {
                    offsetInInteger + commasAdded + (offset - separatorIndex)
                } else {
                    offset + commasAdded
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                val cleanTextUpToOffset = formatted.take(offset).replace(",", "")
                return min(cleanTextUpToOffset.length, original.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }

    private fun formatIndianInteger(number: String): String {
        if (number.isEmpty()) return ""
        val sb = StringBuilder(number)
        val len = sb.length
        if (len <= 3) return sb.toString()

        var i = len - 3
        while (i > 0) {
            sb.insert(i, ",")
            i -= 2
        }
        return sb.toString()
    }

    private fun countCommasAdded(subString: String): Int {
        if (subString.length <= 3) return 0
        val chunks = (subString.length - 3)
        return 1 + (chunks - 1) / 2
    }
}

// ==========================================
// ⚡ ORIGINAL UI COMPONENTS
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavyugaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    isNumber: Boolean = false,
    errorMessage: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val containerColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurface
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val iconColor = MaterialTheme.colorScheme.primary
    val borderColor = if (errorMessage != null) ErrorRed else MaterialTheme.colorScheme.outline

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = { input ->
                if (isNumber) {
                    val filtered = input.filter { it.isDigit() || it == '.' }
                    if (filtered.count { it == '.' } <= 1) {
                        onValueChange(filtered)
                    }
                } else {
                    onValueChange(input)
                }
            },
            label = { Text(label) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = iconColor) },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = labelColor
                        )
                    }
                }
            } else null,
            visualTransformation = when {
                isPassword && !passwordVisible -> PasswordVisualTransformation()
                isNumber -> IndianNumberVisualTransformation()
                else -> VisualTransformation.None
            },
            keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next) else KeyboardOptions.Default,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = borderColor,
                errorBorderColor = ErrorRed,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = labelColor,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
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
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth().height(50.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(PrimaryGradient),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text(text = text, color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

// ==========================================
// ⚡ NEW COMPONENT FOR CHATGPT STYLE UI
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GptTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    // Dark Mode Colors
    val InputBackground = Color(0xFF1E1E1E) // Dark Grey Surface
    val InputBorder = Color(0xFF3E3E3E)     // Slightly lighter border
    val TextColor = Color(0xFFFFFFFF)       // White Text
    val LabelColor = Color(0xFF8E8EA0)      // Grey Label
    val FocusColor = Color(0xFF10A37F)      // Green Focus

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FocusColor,
            unfocusedBorderColor = InputBorder,
            focusedLabelColor = FocusColor,
            unfocusedLabelColor = LabelColor,
            cursorColor = FocusColor,
            // Container Colors
            focusedContainerColor = InputBackground,
            unfocusedContainerColor = InputBackground,
            // Text Colors
            focusedTextColor = TextColor,
            unfocusedTextColor = TextColor
        ),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = LabelColor
                    )
                }
            }
        } else null,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        singleLine = true
    )
}