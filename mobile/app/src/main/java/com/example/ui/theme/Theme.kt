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
    primary = BentoPrimaryDark,
    onPrimary = BentoOnPrimaryContainer,
    primaryContainer = SahayaPrimaryDark,
    onPrimaryContainer = BentoPrimaryContainer,
    secondary = BentoSecondaryContainer,
    onSecondary = BentoOnSecondaryContainer,
    secondaryContainer = BentoSecondary,
    onSecondaryContainer = BentoSecondaryContainer,
    tertiary = BentoTertiaryContainer,
    background = SahayaBackgroundDark,
    surface = SahayaSurfaceDark,
    surfaceVariant = SahayaSurfaceVariantDark,
    onBackground = SahayaTextPrimaryDark,
    onSurface = SahayaTextPrimaryDark,
    outline = SahayaOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = BentoPrimary,
    onPrimary = BentoSurface,
    primaryContainer = BentoPrimaryContainer,
    onPrimaryContainer = BentoOnPrimaryContainer,
    secondary = BentoSecondary,
    onSecondary = BentoSurface,
    secondaryContainer = BentoSecondaryContainer,
    onSecondaryContainer = BentoOnSecondaryContainer,
    tertiary = BentoTertiary,
    background = BentoBackground,
    surface = BentoSurface,
    surfaceVariant = BentoSurfaceVariant,
    onBackground = BentoTextPrimary,
    onSurface = BentoTextPrimary,
    outline = BentoOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // false to preserve intentional Bento Grid aesthetic
    content: @Composable () -> Unit,
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
