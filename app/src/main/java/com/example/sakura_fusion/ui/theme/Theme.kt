package com.example.sakura_fusion.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SakuraPinkMain,
    secondary = SakuraPinkLight,
    tertiary = Pink80,
    background = SakuraBlack,
    surface = SakuraBlack,
    onPrimary = SakuraWhite,
    onSecondary = SakuraBlack,
    onBackground = SakuraWhite,
    onSurface = SakuraWhite,
)

private val LightColorScheme = lightColorScheme(
    primary = SakuraPinkMain,
    secondary = SakuraPinkDark,
    tertiary = Pink40,
    background = SakuraWhite,
    surface = SakuraWhite,
    onPrimary = SakuraWhite,
    onSecondary = SakuraWhite,
    onBackground = SakuraBlack,
    onSurface = SakuraBlack,
    primaryContainer = SakuraPinkLight,
    onPrimaryContainer = SakuraPinkDark
)

@Composable
fun Sakura_fusionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
