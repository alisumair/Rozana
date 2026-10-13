package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RoyalElectricBlueDarkTheme,
    onPrimary = AppBackgroundDark,
    primaryContainer = RoyalBlueDark,
    onPrimaryContainer = IceBlueContainer,
    secondary = ElectricCyan,
    onSecondary = AppBackgroundDark,
    secondaryContainer = AppSurfaceVariantDark,
    onSecondaryContainer = IceBlueContainer,
    tertiary = VibrantSky,
    background = AppBackgroundDark,
    onBackground = TextPrimaryDark,
    surface = AppSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = AppSurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = AppOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalElectricBlue,
    onPrimary = AppSurfaceLight,
    primaryContainer = IceBlueContainer,
    onPrimaryContainer = IceBlueOnContainer,
    secondary = RoyalBlueDark,
    onSecondary = AppSurfaceLight,
    secondaryContainer = IceBlueContainer,
    onSecondaryContainer = IceBlueOnContainer,
    tertiary = VibrantSky,
    background = AppBackgroundLight,
    onBackground = TextPrimaryLight,
    surface = AppSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = AppSurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = AppOutlineLight
)

@Composable
fun RozanaTheme(
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
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
