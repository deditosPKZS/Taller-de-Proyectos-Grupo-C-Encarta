@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.tallerproyectos.encartacusquena

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ConceptoScreen(
    categoriaId: Int,
    conceptos: List<Concepto>,
    repository: Repository,
    onTriviaClick: (Trivia) -> Unit,
    onBack: () -> Unit,
    navController: NavController // ← mantenemos este parámetro
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conceptos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (conceptos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay conceptos disponibles", textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                items(conceptos) { concepto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                concepto.titulo,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(concepto.contenido)
                            Spacer(modifier = Modifier.height(10.dp))

                            // 🔹 Botón para ver imágenes y video del concepto
                            Button(
                                onClick = { navController.navigate("media_demo") },
                                modifier = Modifier.align(Alignment.Start)
                            ) {
                                Text("Ver Multimedia")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // 🔹 Botón para ir a la trivia del concepto
                            Button(
                                onClick = {
                                    val trivia = repository.getTriviaByCategoria(categoriaId)
                                    trivia?.let { onTriviaClick(it) }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Ir a Trivia")
                            }
                        }
                    }
                }
            }
        }
    }
}
