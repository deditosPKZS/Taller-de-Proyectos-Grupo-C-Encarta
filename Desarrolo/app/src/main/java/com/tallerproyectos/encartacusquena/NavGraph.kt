package com.tallerproyectos.encartacusquena

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavHostController

@Composable
fun AppNavGraph(navController: NavHostController, repository: Repository) {
    NavHost(navController = navController, startDestination = "idioma") {

        // Pantalla idioma (elige idioma y navega a categorias)
        composable("idioma") {
            IdiomaScreen { chosenLang ->
                repository.selectedLanguage = chosenLang
                navController.navigate("categorias")
            }
        }

        // Categorías (devuelve id + nombre)
        composable("categorias") {
            val categorias = repository.getCategorias()
            CategoriaScreen(
                categorias = categorias,
                onCategoriaClick = { categoria ->
                    navController.navigate("lecciones/${categoria.id}")
                }
            )
        }

        // Lecciones por categoriaId (Int)
        composable(
            route = "lecciones/{categoriaId}",
            arguments = listOf(navArgument("categoriaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val categoriaId = backStackEntry.arguments?.getInt("categoriaId") ?: 0
            val lecciones = repository.getLeccionesPorCategoria(categoriaId)
            LeccionScreen(
                lecciones = lecciones,
                onLeccionClick = { leccion ->
                    navController.navigate("preguntas/${leccion.id}")
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Preguntas por leccionId (Int)
        composable(
            route = "preguntas/{leccionId}",
            arguments = listOf(navArgument("leccionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val leccionId = backStackEntry.arguments?.getInt("leccionId") ?: 0
            val preguntas = repository.getPreguntasPorLeccion(leccionId)
            PreguntaScreen(
                preguntas = preguntas,
                leccionId = leccionId,
                onFinish = { puntaje ->
                    repository.saveResultado(leccionId, puntaje)
                    navController.popBackStack("lecciones/${0}", false) // volver a lista de lecciones (ok la navegación queda simple)
                    // mejor: navController.popBackStack() dos veces según navegación; para ahora, vuelve atrás
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
