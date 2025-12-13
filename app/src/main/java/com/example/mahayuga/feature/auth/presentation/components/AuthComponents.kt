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
// ⚡ UNIVERSAL HELPER FUNCTIONS
// ==========================================

fun formatIndian(amount: Double): String {
    return try {
        val formatter = NumberFormat.getInstance(Locale("en", "IN"))
        if (amount % 1.0 == 0.0) {
            formatter.maximumFractionDigits = 0
        } else {
            formatter.maximumFractionDigits = 2
        }
        formatter.format(amount)
    } catch (e: Exception) {
        String.format("%.0f", amount)
    }
}

// Overload for String input (Safe parsing)
fun formatIndian(amount: String?): String {
    if (amount.isNullOrBlank()) return "-"
    // Remove existing commas if any before parsing
    val cleanString = amount.replace(",", "")
    val d = cleanString.toDoubleOrNull() ?: return amount // Return original if not a number (e.g. "Contact Us")
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
// ⚡ UI COMPONENTS
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
                    // Allow digits and at most one dot
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
            // ⚡ Apply transformation if it's a number
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