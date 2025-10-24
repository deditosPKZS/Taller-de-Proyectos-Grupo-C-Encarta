package com.tallerproyectos.encartacusquena.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Purple1 = Color(0xFFD863EB) // #D863EB
val Purple2 = Color(0xFFEB63BB) // #EB63BB
val Coral = Color(0xFFEB6369)   // #EB6369
val Violet = Color(0xFFAC63EB)  // #AC63EB
val Orangeish = Color(0xFFEB7863)// #EB7863

val LightColors = lightColorScheme(
    primary = Purple1,
    onPrimary = Color.White,
    secondary = Purple2,
    onSecondary = Color.White,
    error = Coral,
    background = Color(0xFFFFF8FF),
    onBackground = Color.Black,
    surface = Color(0xFFFFFFFF),
    onSurface = Color.Black,
)

val DarkColors = darkColorScheme(
    primary = Purple1,
    onPrimary = Color.White,
    secondary = Purple2,
    onSecondary = Color.White,
    background = Color(0xFF1A0B1A),
    onBackground = Color.White,
    surface = Color(0xFF221322),
    onSurface = Color.White
)
