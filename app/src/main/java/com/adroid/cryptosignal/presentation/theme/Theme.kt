package com.adroid.cryptosignal.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = PrimaryBlue,
    background = BackgroundDark,
    surface = SurfaceDark
)

private val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    background = BackgroundLight,
    surface = SurfaceLight
)

@Composable
fun AdroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
