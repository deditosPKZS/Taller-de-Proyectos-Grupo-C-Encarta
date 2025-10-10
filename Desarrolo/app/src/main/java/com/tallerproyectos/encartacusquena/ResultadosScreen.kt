@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.tallerproyectos.encartacusquena

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@Composable
fun ResultadosScreen(
    triviaId: Int,
    repository: Repository,
    onBack: () -> Unit
) {
    val preguntas = remember { repository.safeGetPreguntasPorTrivia(triviaId) }
    val resultadosGuardados = remember { repository.safeGetResultadosPorTrivia(triviaId) }
    val ultimoPuntaje = resultadosGuardados.firstOrNull()?.puntaje ?: 0

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Resultados") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                "Puntaje total: $ultimoPuntaje / ${preguntas.size}",
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn {
                items(preguntas) { pregunta ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(pregunta.enunciado, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Correcta: ${pregunta.correcta}")
                        }
                    }
                }
            }
        }
    }
}
