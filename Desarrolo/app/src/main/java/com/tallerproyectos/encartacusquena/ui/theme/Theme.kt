package com.tallerproyectos.encartacusquena.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun EncartaCusquenaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = LightBlue,
            secondary = LightGreen,
            background = SoftGray,
            surface = SoftGray,
            onPrimary = Color.White,
            onSecondary = Color.Black
        )
    } else {
        lightColorScheme(
            primary = LightBlue,
            secondary = LightGreen,
            background = SoftGray,
            surface = Color.White,
            onPrimary = Color.White,
            onSecondary = Color.Black
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
