package com.darling.spendwise.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary            = Color(0xFF1565C0),
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFFE3F2FD),
    secondary          = Color(0xFF1E88E5),
    onSecondary        = Color.White,
    background         = Color(0xFFF0F4F8),
    onBackground       = Color(0xFF1A1A2E),
    surface            = Color.White,
    onSurface          = Color(0xFF1A1A2E),
    surfaceVariant     = Color(0xFFF0F4F8),
    onSurfaceVariant   = Color(0xFF6B7280),
    error              = Color(0xFFE53935),
    onError            = Color.White,
    outline            = Color(0xFFEEF2F7)
)

private val DarkColorScheme = darkColorScheme(
    primary            = Color(0xFF42A5F5),
    onPrimary          = Color(0xFF003063),
    primaryContainer   = Color(0xFF1565C0),
    secondary          = Color(0xFF90CAF9),
    onSecondary        = Color(0xFF003063),
    background         = Color(0xFF0F1923),
    onBackground       = Color(0xFFE8EDF1),
    surface            = Color(0xFF1A2633),
    onSurface          = Color(0xFFE8EDF1),
    surfaceVariant     = Color(0xFF243040),
    onSurfaceVariant   = Color(0xFF9BABBF),
    error              = Color(0xFFFF6B6B),
    onError            = Color(0xFF690005),
    outline            = Color(0xFF2D3F52)
)

@Composable
fun SpendWiseTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}