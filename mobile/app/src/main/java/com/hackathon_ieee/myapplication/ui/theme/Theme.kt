package com.hackathon_ieee.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RiverGuardDarkColorScheme = darkColorScheme(
    primary = RiverPrimary,
    onPrimary = RiverText,
    primaryContainer = RiverGlassHigh,
    onPrimaryContainer = RiverText,
    secondary = RiverPrimary,
    onSecondary = RiverText,
    secondaryContainer = RiverGlassHigh,
    onSecondaryContainer = RiverText,
    tertiary = RiverWarning,
    onTertiary = RiverBackground,
    background = RiverBackground,
    onBackground = RiverText,
    surface = RiverBackground,
    onSurface = RiverText,
    surfaceVariant = RiverGlassHigh,
    onSurfaceVariant = RiverText,
    error = RiverDanger,
    onError = RiverText,
    outline = RiverText.copy(alpha = 0.35f)
)

@Composable
fun MobileTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RiverGuardDarkColorScheme,
        typography = Typography,
        content = content
    )
}
