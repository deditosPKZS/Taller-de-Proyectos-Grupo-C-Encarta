package com.tallerproyectos.encartacusquena

import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import com.tallerproyectos.encartacusquena.data.Repository
import com.tallerproyectos.encartacusquena.model.Trivia
import com.tallerproyectos.encartacusquena.model.Categoria
import com.tallerproyectos.encartacusquena.ui.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraphScaffold(repository: Repository) {
    val navController = rememberNavController()

    // Estado para forzar recarga de categorías
    var reloadTrigger by remember { mutableStateOf(0) }

    val bottomItems = listOf(BottomNavItem.Home, BottomNavItem.Results, BottomNavItem.Settings)

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: ""

            // Solo mostrar bottom bar después de seleccionar idioma
            if (currentRoute != "idioma") {
                NavigationBar {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute.startsWith(item.route),
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "idioma",
            modifier = Modifier.padding(innerPadding)
        ) {

            // Pantalla de selección de idioma
            composable("idioma") {
                IdiomaScreen { lang ->
                    Log.d("Navigation", "Idioma seleccionado: $lang")
                    repository.selectedLanguage = lang
                    reloadTrigger++ // Incrementar para forzar recarga

                    navController.navigate("categorias") {
                        popUpTo("idioma") { inclusive = true }
                    }
                }
            }

            // Pantalla de categorías (Home)
            composable("categorias") {
                var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
                var isLoading by remember { mutableStateOf(true) }

                // Recargar categorías cada vez que se navega aquí o cambia el idioma
                LaunchedEffect(reloadTrigger) {
                    Log.d("Navigation", "Cargando categorías para idioma: ${repository.selectedLanguage}")
                    isLoading = true
                    categorias = repository.safeGetCategorias()
                    Log.d("Navigation", "Categorías cargadas: ${categorias.size}")
                    isLoading = false
                }

                if (isLoading) {
                    // Mostrar indicador de carga
                    Scaffold(topBar = { TopAppBar(title = { Text("Categorías") }) }) { inner ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(inner),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    CategoriaScreen(
                        categorias = categorias,
                        onCategoriaClick = { cat ->
                            Log.d("Navigation", "Categoría seleccionada: ${cat.nombre} (id=${cat.id})")
                            navController.navigate("conceptos/${cat.id}")
                        }
                    )
                }
            }

            // Pantalla de resultados globales
            composable("mis_resultados") {
                val anyTriviaId = repository.safeGetTriviasPorCategoria(1).firstOrNull()?.id ?: 1
                ResultadosScreen(
                    triviaId = anyTriviaId,
                    repository = repository,
                    onBack = { /* no-op */ }
                )
            }

            // Pantalla de conceptos y trivias
            composable(
                "conceptos/{categoriaId}",
                arguments = listOf(navArgument("categoriaId") { type = NavType.IntType })
            ) { backStackEntry ->
                val catId = backStackEntry.arguments?.getInt("categoriaId") ?: 0
                val conceptos = remember(catId) {
                    repository.safeGetConceptosPorCategoria(catId)
                }

                ConceptoScreen(
                    categoriaId = catId,
                    conceptos = conceptos,
                    repository = repository,
                    onTriviaClick = { trivia: Trivia ->
                        Log.d("Navigation", "Trivia seleccionada: ${trivia.nombre} (id=${trivia.id})")
                        navController.navigate("preguntas/${trivia.id}")
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // Pantalla de preguntas (trivia)
            composable(
                "preguntas/{triviaId}",
                arguments = listOf(navArgument("triviaId") { type = NavType.IntType })
            ) { backStackEntry ->
                val triviaId = backStackEntry.arguments?.getInt("triviaId") ?: 0
                val preguntas = remember(triviaId) {
                    repository.safeGetPreguntasPorTrivia(triviaId)
                }

                PreguntaScreen(
                    preguntas = preguntas,
                    triviaId = triviaId,
                    repository = repository,
                    onViewResults = { tId ->
                        navController.navigate("resultados/$tId")
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // Pantalla de resultados de una trivia específica
            composable(
                "resultados/{triviaId}",
                arguments = listOf(navArgument("triviaId") { type = NavType.IntType })
            ) { backStackEntry ->
                val triviaId = backStackEntry.arguments?.getInt("triviaId") ?: 0

                ResultadosScreen(
                    triviaId = triviaId,
                    repository = repository,
                    onBack = {
                        val catId = repository.getCategoriaIdPorTrivia(triviaId)
                        navController.navigate("conceptos/$catId") {
                            popUpTo("conceptos/$catId") { inclusive = false }
                        }
                    }
                )
            }
        }
    }
}
