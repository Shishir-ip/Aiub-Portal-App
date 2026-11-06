package com.shishir.aiubportal.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF64b5f6),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF0b4a9e),
    onPrimaryContainer = Color(0xFFdceaff),
    secondary = Color(0xFF38c1f2),
    onSecondary = Color(0xFF0e131b),
    secondaryContainer = Color(0xFF22334a),
    onSecondaryContainer = Color(0xFFcfe3ff),
    background = Color(0xFF0e131b),
    onBackground = Color(0xFFe7ecf3),
    surface = Color(0xFF101724),
    onSurface = Color(0xFFe7ecf3),
    surfaceVariant = Color(0xFF1a2231),
    onSurfaceVariant = Color(0xFFb7c2d2),
    error = Color(0xFFf28b8b),
    onError = Color(0xFF3a1a1a),
    outline = Color(0xFF4a5872)
)

private val LightScheme = lightColorScheme(
    primary = Color(0xFF0b4a9e),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFdceaff),
    onPrimaryContainer = Color(0xFF082c5e),
    secondary = Color(0xFF0288d1),
    background = Color(0xFFf5f7fb),
    onBackground = Color(0xFF1a2230),
    surface = Color(0xFFffffff),
    onSurface = Color(0xFF1a2230),
    surfaceVariant = Color(0xFFe9eef6),
    onSurfaceVariant = Color(0xFF475569),
    error = Color(0xFFb3261e)
)

@Composable
fun AiubTheme(dark: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (dark) DarkScheme else LightScheme,
        content = content
    )
}
