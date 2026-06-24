package com.mahallu.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Indigo + Coral Color Scheme (Option B)
val Primary = Color(0xFF4F46E5)
val PrimaryDark = Color(0xFF4338CA)
val AccentCoral = Color(0xFFFF6B6B)
val LightCoral = Color(0xFFFFE5E5)
val Background = Color(0xFFFFFFFF)
val Surface = Color(0xFFF8FAFC)
val TextPrimary = Color(0xFF1F2937)
val TextSecondary = Color(0xFF6B7280)
val Border = Color(0xFFE5E7EB)
val Success = Color(0xFF10B981)
val Warning = Color(0xFFF59E0B)
val Error = Color(0xFFEF4444)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Primary.copy(alpha = 0.1f),
    onPrimaryContainer = Primary,
    secondary = AccentCoral,
    onSecondary = Color.White,
    secondaryContainer = LightCoral,
    onSecondaryContainer = AccentCoral,
    tertiary = PrimaryDark,
    onTertiary = Color.White,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = Surface,
    onSurfaceVariant = TextSecondary,
    error = Error,
    onError = Color.White,
    outline = Border
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Primary.copy(alpha = 0.2f),
    onPrimaryContainer = Primary,
    secondary = AccentCoral,
    onSecondary = Color.White,
    secondaryContainer = AccentCoral.copy(alpha = 0.2f),
    onSecondaryContainer = LightCoral,
    tertiary = Primary.copy(alpha = 0.8f),
    onTertiary = Color.White,
    background = Color(0xFF111827),
    onBackground = Color(0xFFF9FAFB),
    surface = Color(0xFF1F2937),
    onSurface = Color(0xFFF9FAFB),
    surfaceVariant = Color(0xFF374151),
    onSurfaceVariant = Color(0xFF9CA3AF),
    error = Error,
    onError = Color.White,
    outline = Color(0xFF4B5563)
)

@Composable
fun MahalluTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
