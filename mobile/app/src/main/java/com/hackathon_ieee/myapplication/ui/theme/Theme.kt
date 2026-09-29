package com.hackathon_ieee.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RiverGuardDarkColorScheme = darkColorScheme(
    primary = RiverPrimary,
    onPrimary = RiverBackground,
    primaryContainer = RiverCyanContainer,
    onPrimaryContainer = RiverText,
    secondary = RiverCyan,
    onSecondary = RiverBackground,
    secondaryContainer = RiverInput,
    onSecondaryContainer = RiverText,
    tertiary = RiverWarning,
    onTertiary = RiverBackground,
    background = RiverBackground,
    onBackground = RiverText,
    surface = RiverSurface,
    onSurface = RiverText,
    surfaceVariant = RiverInput,
    onSurfaceVariant = RiverTextSecondary,
    error = RiverDanger,
    onError = RiverBackground,
    outline = RiverBorder
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
