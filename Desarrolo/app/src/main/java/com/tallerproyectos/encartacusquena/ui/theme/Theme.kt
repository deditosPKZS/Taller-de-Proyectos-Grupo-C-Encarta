package com.tallerproyectos.encartacusquena.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun EncartaCusquenaTheme(
    content: @Composable () -> Unit
) {
    val colors = LightColors // o lógica para dark/light
    MaterialTheme(
        colorScheme = colors,
        typography = Typography, // usa tu tipografía si la tienes
        content = content
    )
}
