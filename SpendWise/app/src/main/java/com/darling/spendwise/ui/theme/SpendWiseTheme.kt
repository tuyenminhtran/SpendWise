package com.darling.spendwise.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Màu chủ đạo từ figma design
val TurquoisePrimary = Color(0xFF26D7D0) // Màu xanh ngọc header
val TurquoiseLight = Color(0xFF6FE8E3)
val TurquoiseDark = Color(0xFF00A8A3)
val Gray50 = Color(0xFFF5F5F5)

private val LightColorScheme = lightColorScheme(
    primary = TurquoisePrimary,
    secondary = TurquoiseDark,
    tertiary = TurquoiseLight,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun SpendWiseTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}