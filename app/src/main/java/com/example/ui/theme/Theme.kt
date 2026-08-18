package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = EmeraldGreen,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = EmeraldGreen,
    secondary = SaffronPrimary,
    onSecondary = Color.White,
    secondaryContainer = SaffronContainer,
    onSecondaryContainer = SaffronPrimary,
    tertiary = NavyLight,
    onTertiary = Color.White,
    background = WarmBackground,
    onBackground = Color(0xFF1C1B1F),
    surface = WarmSurface,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = SurfaceVariantWarm,
    onSurfaceVariant = Color(0xFF49454F)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF003818),
    primaryContainer = Color(0xFF005227),
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = Color(0xFFFFB74D),
    onSecondary = Color(0xFF4E2600),
    secondaryContainer = Color(0xFF6D3800),
    onSecondaryContainer = Color(0xFFFFD180),
    tertiary = Color(0xFF90CAF9),
    onTertiary = Color(0xFF0D47A1),
    background = Color(0xFF121413),
    onBackground = Color(0xFFE2E3E0),
    surface = Color(0xFF1A1C1A),
    onSurface = Color(0xFFE2E3E0),
    surfaceVariant = Color(0xFF2C2F2C),
    onSurfaceVariant = Color(0xFFC2C8C2)
)

@Composable
fun YojanaSahayakTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use intentional palette
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
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
