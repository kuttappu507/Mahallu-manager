package com.mahallu.manager.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import android.app.Activity

private val LightColors = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    secondary = AccentCoral,
    onSecondary = TextOnPrimary,
    secondaryContainer = AccentCoralLight,
    onSecondaryContainer = AccentCoralDark,
    tertiary = Info,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = Border,
    outlineVariant = BorderStrong,
    error = Error,
    onError = TextOnPrimary,
    errorContainer = ErrorLight,
    onErrorContainer = Error,
    scrim = TextPrimary.copy(alpha = 0.5f)
)

private val DarkColors = darkColorScheme(
    primary = PrimaryIndigoDark,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = PrimaryLight,
    secondary = AccentCoral,
    onSecondary = TextOnPrimary,
    secondaryContainer = AccentCoralDark,
    onSecondaryContainer = AccentCoralLight,
    tertiary = Info,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    outlineVariant = BorderDark,
    error = Error,
    onError = TextOnPrimary,
    errorContainer = ErrorLight,
    onErrorContainer = Error
)

@Composable
fun MahalluTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalMahalluColors provides if (darkTheme) DarkMahalluColors else LightMahalluColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}