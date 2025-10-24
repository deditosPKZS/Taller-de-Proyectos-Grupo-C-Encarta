package com.tallerproyectos.encartacusquena

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController


@Composable
fun AppNavGraph(navController: NavHostController, repository: Repository) {

    NavHost(navController = navController, startDestination = "idioma") {

        // 1️⃣ Idioma
        composable("idioma") {
            IdiomaScreen { chosenLang: String ->
                repository.selectedLanguage = chosenLang
                Log.d("AppNavGraph", "Idioma elegido: $chosenLang")
                navController.navigate("categorias")
            }
        }

        // 2️⃣ Categorías
        composable("categorias") {
            val categorias = repository.safeGetCategorias()
            CategoriaScreen(
                categorias = categorias,
                onCategoriaClick = { categoria ->
                    navController.navigate("conceptos/${categoria.id}")
                },
                onBack = { navController.popBackStack() } // 🔹 aquí pasamos el back
            )
        }

        // 3️⃣ Conceptos
        composable(
            route = "conceptos/{categoriaId}",
            arguments = listOf(navArgument("categoriaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val categoriaId = backStackEntry.arguments?.getInt("categoriaId") ?: 0
            val conceptos = repository.safeGetConceptosPorCategoria(categoriaId)

            ConceptoScreen(
                categoriaId = categoriaId,
                conceptos = conceptos,
                repository = repository,
                onTriviaClick = { trivia ->
                    navController.navigate("preguntas/${trivia.id}")
                },
                onBack = {
                    navController.popBackStack()
                },
                navController = navController
            )
        }

// ❌ elimina la vieja versión de media_demo que estaba aquí

// 4️⃣ Preguntas
        composable(
            route = "preguntas/{triviaId}",
            arguments = listOf(navArgument("triviaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val triviaId = backStackEntry.arguments?.getInt("triviaId") ?: 0
            val preguntas = repository.safeGetPreguntasPorTrivia(triviaId)

            PreguntaScreen(
                preguntas = preguntas,
                triviaId = triviaId,
                repository = repository,
                onBack = { navController.popBackStack() },
                onFinish = { id -> navController.navigate("resultados/$id") }
            )
        }

// 5️⃣ Resultados
        composable(
            route = "resultados/{triviaId}",
            arguments = listOf(navArgument("triviaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val triviaId = backStackEntry.arguments?.getInt("triviaId") ?: 0

            ResultadosScreen(
                triviaId = triviaId,
                repository = repository,
                onBack = {
                    val categoriaId = repository.getCategoriaIdPorTrivia(triviaId)
                    navController.navigate("conceptos/$categoriaId")
                },
                navController = navController
            )
        }

// 6️⃣ Media Demo
        composable("media_demo") {
            ImageVideoScreen(
                onBack = { navController.popBackStack() },
                imagenAssets = listOf("imagenes/qorikancha.jpg", "imagenes/otra.jpg"),
                videoAsset = "videos/qorikancha.mp4"
            )
        }

    }
}
