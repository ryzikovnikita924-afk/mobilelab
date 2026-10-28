package com.lab.myapplication.ui.theme

import android.app.Activity
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
private val Turquoise = Color(0xFF00A6A6)
private val LightBackground = Color(0xFFF5F7F8)
private val DarkBackground = Color(0xFF121819)
private val DarkSurface = Color(0xFF202829)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF45D6D0),
    onPrimary = Color(0xFF003737),
    secondary = Color(0xFF80CBC4),
    background = DarkBackground,
    onBackground = Color(0xFFE5EAEA),
    surface = DarkSurface,
    onSurface = Color(0xFFE5EAEA)
)

private val LightColorScheme = lightColorScheme(
    primary = Turquoise,
    onPrimary = Color.White,
    secondary = Color(0xFF008B8B),
    background = LightBackground,
    onBackground = Color(0xFF20252B),
    surface = Color.White,
    onSurface = Color(0xFF20252B)
)



@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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