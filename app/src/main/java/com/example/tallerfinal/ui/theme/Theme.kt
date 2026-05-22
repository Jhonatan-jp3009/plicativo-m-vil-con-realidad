package com.example.tallerfinal.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

// Esquema de colores para el modo oscuro premium
private val DarkColorScheme = darkColorScheme(
    primary = CyanPrimary,
    secondary = EmeraldSecondary,
    tertiary = VioletTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkBackground,
    onSecondary = DarkBackground,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

// Esquema de colores para el modo claro (en este caso lo configuramos con contraste limpio)
private val LightColorScheme = lightColorScheme(
    primary = CyanPrimary,
    secondary = EmeraldSecondary,
    tertiary = VioletTertiary,
    background = TextPrimary,
    surface = Color(0xFFF1F5F9),
    onPrimary = DarkBackground,
    onSecondary = DarkBackground,
    onTertiary = TextPrimary,
    onBackground = DarkBackground,
    onSurface = DarkBackground
)

/**
 * Tema principal de la aplicación TallerFinalTheme.
 * Define la paleta de colores del sistema, garantizando que el modo oscuro sofisticado
 * sea la identidad principal de la app para no cansar la vista durante largas sesiones de dibujo.
 */
@Composable
fun TallerFinalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Usamos el esquema oscuro por defecto para dar un aspecto tecnológico premium
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme // Forzamos el tema oscuro premium

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Ajustamos la barra de estado superior con el color de fondo pizarra
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            // Ajustamos los iconos de estado a blanco para que resalten sobre el fondo oscuro
            windowInsetsController.isAppearanceLightStatusBars = false
            windowInsetsController.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
