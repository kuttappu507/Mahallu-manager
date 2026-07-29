package com.mahallu.manager.core.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class MahalluColors(
    val primaryIndigo: Color,
    val primaryDark: Color,
    val accentCoral: Color,
    val accentCoralLight: Color,
    val success: Color,
    val successLight: Color,
    val warning: Color,
    val warningLight: Color,
    val error: Color,
    val errorLight: Color,
    val info: Color,
    val infoLight: Color,
    val chartIncome: Color,
    val chartExpense: Color,
    val chartCollection: Color,
    val chartDonation: Color,
    val chartGrid: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val borderStrong: Color
)

val LightMahalluColors = MahalluColors(
    primaryIndigo = PrimaryIndigo,
    primaryDark = PrimaryDark,
    accentCoral = AccentCoral,
    accentCoralLight = AccentCoralLight,
    success = Success,
    successLight = SuccessLight,
    warning = Warning,
    warningLight = WarningLight,
    error = Error,
    errorLight = ErrorLight,
    info = Info,
    infoLight = InfoLight,
    chartIncome = ChartIncome,
    chartExpense = ChartExpense,
    chartCollection = ChartCollection,
    chartDonation = ChartDonation,
    chartGrid = ChartGrid,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    textTertiary = TextTertiary,
    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    border = Border,
    borderStrong = BorderStrong
)

val DarkMahalluColors = MahalluColors(
    primaryIndigo = PrimaryIndigoDark,
    primaryDark = PrimaryDark,
    accentCoral = AccentCoral,
    accentCoralLight = AccentCoralLight,
    success = Success,
    successLight = SuccessLight,
    warning = Warning,
    warningLight = WarningLight,
    error = Error,
    errorLight = ErrorLight,
    info = Info,
    infoLight = InfoLight,
    chartIncome = ChartIncome,
    chartExpense = ChartExpense,
    chartCollection = ChartCollection,
    chartDonation = ChartDonation,
    chartGrid = BorderDark,
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    textTertiary = TextTertiaryDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    border = BorderDark,
    borderStrong = BorderDark
)

val LocalMahalluColors = staticCompositionLocalOf { LightMahalluColors }