package com.example.gamifikalt_fitnessz_alkalmazas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AppBlack,
    onPrimary = AppWhite,

    secondary = AppDarkGray,
    onSecondary = AppWhite,

    tertiary = AppGray,
    onTertiary = AppWhite,

    background = ScreenBackground,
    onBackground = AppBlack,

    surface = CardBackground,
    onSurface = AppBlack,

    surfaceVariant = AppVeryLightGray,
    onSurfaceVariant = AppGray,

    outline = BorderColor,

    error = DangerRed,
    onError = AppWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = AppWhite,
    onPrimary = AppBlack,

    secondary = AppLightGray,
    onSecondary = AppBlack,

    tertiary = AppGray,
    onTertiary = AppWhite,

    background = AppBlack,
    onBackground = AppWhite,

    surface = AppDarkGray,
    onSurface = AppWhite,

    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = AppLightGray,

    outline = Color(0xFF3A3A3A),

    error = DangerRed,
    onError = AppWhite
)

@Composable
fun Gamifikalt_Fitnessz_AlkalmazasTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}