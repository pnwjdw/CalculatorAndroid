// app/src/main/kotlin/com/example/calculator_android/Theme.kt
package com.example.calculator_android

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class ThemeType {
    DARK, LIGHT, PINK
}

private val Purple80 = Color(0xFFD0BCFF)
private val PurpleGrey80 = Color(0xFFCCC2DC)
private val Pink80 = Color(0xFFEFB8C8)

private val Purple40 = Color(0xFF6650a4)
private val PurpleGrey40 = Color(0xFF625b71)
private val Pink40 = Color(0xFF7D5260)

private val Pink500 = Color(0xFFE91E63)
private val Pink200 = Color(0xFFF48FB1)
private val Pink700 = Color(0xFFC2185B)
private val Pink50 = Color(0xFFFCE4EC)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val PinkColorScheme = lightColorScheme(
    primary = Pink500,
    secondary = Pink700,
    tertiary = Pink200,
    background = Pink50,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

fun getColorScheme(themeType: ThemeType) = when (themeType) {
    ThemeType.LIGHT -> LightColorScheme
    ThemeType.DARK -> DarkColorScheme
    ThemeType.PINK -> PinkColorScheme
}