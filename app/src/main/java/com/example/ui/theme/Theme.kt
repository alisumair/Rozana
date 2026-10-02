package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = VioletBrandDarkTheme,
    onPrimary = AppBackgroundDark,
    primaryContainer = VioletBrandDark,
    onPrimaryContainer = VioletBrandContainer,
    secondary = VioletBrandLight,
    onSecondary = AppBackgroundDark,
    secondaryContainer = AppSurfaceVariantDark,
    onSecondaryContainer = VioletBrandContainer,
    tertiary = IconReportsBlue,
    background = AppBackgroundDark,
    onBackground = TextPrimaryDark,
    surface = AppSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = AppSurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = AppOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = VioletBrand,
    onPrimary = AppSurfaceLight,
    primaryContainer = VioletBrandContainer,
    onPrimaryContainer = VioletBrandOnContainer,
    secondary = VioletBrandDark,
    onSecondary = AppSurfaceLight,
    secondaryContainer = VioletBrandContainer,
    onSecondaryContainer = VioletBrandOnContainer,
    tertiary = IconReportsBlue,
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
