package com.mahallu.manager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimary,
    secondary = CoralAccent,
    tertiary = CoralAccent,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = White,
    onSurface = White,
    surfaceVariant = DarkSurface,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    secondary = CoralAccent,
    tertiary = CoralAccent,
    background = Background,
    surface = Surface,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = Surface,
    outline = Border,
    
    // Additional color mappings for better UI consistency
    primaryContainer = IndigoLight,
    onPrimaryContainer = IndigoPrimaryDark,
    secondaryContainer = CoralLight,
    onSecondaryContainer = CoralAccent,
    error = Error,
    onError = White,
    errorContainer = ErrorLight,
    onErrorContainer = Error,
    outlineVariant = Border
)

@Composable
fun MahalluManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

// Helper function to get current colors
object AppColors {
    val primary = IndigoPrimary
    val primaryDark = IndigoPrimaryDark
    val accent = CoralAccent
    val accentLight = CoralLight
    val background = Background
    val surface = Surface
    val textPrimary = TextPrimary
    val textSecondary = TextSecondary
    val success = Success
    val warning = Warning
    val error = Error
    val border = Border
}
