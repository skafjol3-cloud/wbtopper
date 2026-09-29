package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RoyalBlue500,
    onPrimary = SurfaceWhite,
    primaryContainer = RoyalBlue800,
    onPrimaryContainer = RoyalBlue100,
    secondary = PurpleAccent,
    onSecondary = SurfaceWhite,
    secondaryContainer = IndigoPrimary,
    onSecondaryContainer = IndigoLight,
    background = SurfaceDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceCardDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = SurfaceBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalBlue800,
    onPrimary = SurfaceWhite,
    primaryContainer = RoyalBlue50,
    onPrimaryContainer = RoyalBlue800,
    secondary = IndigoPrimary,
    onSecondary = SurfaceWhite,
    secondaryContainer = IndigoLight,
    onSecondaryContainer = IndigoPrimary,
    tertiary = PurpleAccent,
    background = SurfaceWhite,
    surface = SurfaceWhite,
    surfaceVariant = SurfaceCardLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = SurfaceBorderLight
)

@Composable
fun BanglaExamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve brand Royal Blue styling
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
