// Theme.kt
package com.example.yolofitclient.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

//private val DarkColorScheme = darkColorScheme(
//    primary = Color(0xFF00FF41),        // Зеленый
//    secondary = Color(0xFF00CC34),      // Темно-зеленый
//    tertiary = Color(0xFF66FF99),       // Светло-зеленый
//    background = Color(0xFF0A0E0A),     // Черный фон
//    surface = Color(0xFF1A1F1A),        // Темная поверхность
//    onPrimary = Color(0xFF0A0E0A),
//    onSecondary = Color.White,
//    onTertiary = Color(0xFF0A0E0A),
//    onBackground = Color(0xFFF0F0F0),
//    onSurface = Color(0xFFF0F0F0),
//    surfaceVariant = Color(0xFF2A3A2A),
//    onSurfaceVariant = Color(0xFFB0B0B0)
//)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0066FF),        // Синий
    secondary = Color(0xFFFF1744),      // Красный
    tertiary = Color(0xFF00E5FF),       // Циан
    background = Color(0xFF0A0A1A),     // Очень темный синий
    surface = Color(0xFF1A1A2E),        // Темно-синяя поверхность
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF0A0A1A),
    onBackground = Color(0xFFF0F0FF),
    onSurface = Color(0xFFF0F0FF),
    surfaceVariant = Color(0xFF2A2A4A),
    onSurfaceVariant = Color(0xFFB0B0CC)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6650a4),
    secondary = Color(0xFF625b71),
    tertiary = Color(0xFF7D5260)
)

@Composable
fun YoloFitClientTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = CustomTypography,
        content = content
    )
}