package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimaryDark,
    onPrimary = GreenOnPrimaryDark,
    primaryContainer = GreenPrimaryContainerDark,
    onPrimaryContainer = GreenOnPrimaryContainerDark,
    secondary = GreenPrimaryDark,
    onSecondary = GreenOnPrimaryDark,
    secondaryContainer = GreenPrimaryContainerDark,
    onSecondaryContainer = GreenOnPrimaryContainerDark,
    tertiary = GreenTertiary,
    background = BackgroundDark,
    onBackground = Color(0xFFE2EBE4),
    surface = SurfaceDark,
    onSurface = Color(0xFFE2EBE4),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFB0C4B6),
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = GreenOnPrimary,
    primaryContainer = GreenPrimaryContainer,
    onPrimaryContainer = GreenOnPrimaryContainer,
    secondary = GreenSecondary,
    onSecondary = GreenOnSecondary,
    secondaryContainer = GreenSecondaryContainer,
    onSecondaryContainer = GreenOnSecondaryContainer,
    tertiary = GreenTertiary,
    background = BackgroundLight,
    onBackground = Color(0xFF131F16),
    surface = SurfaceLight,
    onSurface = Color(0xFF131F16),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF435A4B),
    outline = OutlineLight
)

@Composable
fun RozanaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep signature green and white brand
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
