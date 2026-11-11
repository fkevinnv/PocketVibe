package com.pocketvibe.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PocketVibeDarkColors = darkColorScheme(
    primary = Color(0xFFA78BFA),
    secondary = Color(0xFFF472B6),
    tertiary = Color(0xFF38BDF8),
    background = Color(0xFF0F0F1A),
    surface = Color(0xFF16162A),
    surfaceVariant = Color(0xFF1E1E33),
    onPrimary = Color(0xFF0F0F1A),
    onBackground = Color(0xFFF1F1F8),
    onSurface = Color(0xFFF1F1F8),
    onSurfaceVariant = Color(0xFFB9B9CF)
)

@Composable
fun PocketVibeTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PocketVibeDarkColors, content = content)
}
