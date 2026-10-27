package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NeuraDarkColorScheme = darkColorScheme(
    primary = NeuraElectricBlue,
    onPrimary = Color.White,
    primaryContainer = NeuraDeepPurple,
    onPrimaryContainer = Color.White,
    secondary = NeuraVibrantViolet,
    onSecondary = Color.White,
    tertiary = NeuraNeonCyan,
    onTertiary = Color.Black,
    background = NeuraBackground,
    onBackground = NeuraTextPrimary,
    surface = NeuraSurface,
    onSurface = NeuraTextPrimary,
    surfaceVariant = NeuraCard,
    onSurfaceVariant = NeuraTextSecondary,
    outline = NeuraCardBorder,
    error = NeuraError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NeuraDarkColorScheme,
        typography = Typography,
        content = content
    )
}
