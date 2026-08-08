package com.adroid.cryptosignal.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * A fixed brand palette — no Material You dynamic color, no light theme. A trader-facing
 * signal app benefits from a single, predictable dark surface rather than adapting to the
 * device wallpaper or system light/dark switch.
 */
private val AdroidColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = TextPrimary,
    secondary = AccentBlue,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = OutlineDark,
    error = SellRed,
    onError = TextPrimary
)

@Composable
fun AdroidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AdroidColorScheme,
        content = content
    )
}
