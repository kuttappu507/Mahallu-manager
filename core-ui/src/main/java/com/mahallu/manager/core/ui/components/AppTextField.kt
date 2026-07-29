package com.mahallu.manager.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.theme.RadiusMd

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: ImageVector? = null,
    onTrailingClick: (() -> Unit)? = null,
    helperText: String? = null,
    isRequired: Boolean = false
) {
    val colors = LocalMahalluColors.current
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = colors.textSecondary,
                fontWeight = FontWeight.Medium
            )
            if (isRequired) {
                Spacer(Modifier.width(2.dp))
                Text(text = "*", color = colors.error, style = MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(Modifier.height(6.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(RadiusMd.value.dp))
                .border(
                    width = 1.dp,
                    color = when {
                        isError -> colors.error
                        else -> colors.border
                    },
                    shape = RoundedCornerShape(RadiusMd.value.dp)
                )
                .background(colors.surfaceVariant),
            placeholder = if (placeholder != null) {
                { Text(text = placeholder, color = colors.textTertiary, style = MaterialTheme.typography.bodyMedium) }
            } else null,
            leadingIcon = if (leadingIcon != null) {
                { Icon(leadingIcon, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(18.dp)) }
            } else null,
            trailingIcon = if (trailingIcon != null && onTrailingClick != null) {
                {
                    IconButton(onClick = onTrailingClick) {
                        Icon(trailingIcon, contentDescription = null, tint = colors.textSecondary)
                    }
                }
            } else null,
            singleLine = singleLine,
            maxLines = maxLines,
            enabled = enabled,
            readOnly = readOnly,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.textPrimary),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.surfaceVariant,
                unfocusedContainerColor = colors.surfaceVariant,
                disabledContainerColor = colors.surfaceVariant.copy(alpha = 0.6f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                disabledTextColor = colors.textTertiary,
                cursorColor = colors.primaryIndigo
            )
        )
        if (isError && !errorMessage.isNullOrBlank()) {
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Error, contentDescription = null, tint = colors.error, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(text = errorMessage, color = colors.error, style = MaterialTheme.typography.labelSmall)
            }
        } else if (!helperText.isNullOrBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(text = helperText, color = colors.textSecondary, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Password",
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    isRequired: Boolean = true
) {
    var visible by remember { mutableStateOf(false) }
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        isError = isError,
        errorMessage = errorMessage,
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Done,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = if (visible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
        onTrailingClick = { visible = !visible },
        isRequired = isRequired
    )
}