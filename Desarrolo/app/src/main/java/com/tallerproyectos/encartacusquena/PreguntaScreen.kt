@file:OptIn(ExperimentalMaterial3Api::class)

package com.tallerproyectos.encartacusquena

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack


@Composable
fun PreguntaScreen(
    preguntas: List<Pregunta>,
    triviaId: Int,
    repository: Repository,
    onBack: () -> Unit,
    onFinish: (Int) -> Unit
) {
    var index by remember { mutableStateOf(0) }
    var puntaje by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var showResultDialog by remember { mutableStateOf(false) }
    val total = preguntas.size

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Trivia") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        )
    }) { innerPadding ->
        if (preguntas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay preguntas para esta trivia")
            }
            return@Scaffold
        }

        val p = preguntas[index]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Pregunta ${index + 1} / $total")
            Spacer(Modifier.height(8.dp))
            Text(p.enunciado, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))

            val options = listOf("a" to p.opcionA, "b" to p.opcionB, "c" to p.opcionC, "d" to p.opcionD)
            options.forEach { (key, opt) ->
                opt?.let {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOption = key },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedOption == key),
                            onClick = { selectedOption = key }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(it)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (selectedOption == p.correcta) {
                        puntaje++
                    }
                    selectedOption = null

                    if (index + 1 < total) {
                        index++
                    } else {
                        repository.saveResultado(triviaId, puntaje)
                        showResultDialog = true
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (index + 1 < total) "Siguiente" else "Terminar")
            }
        }
    }

    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text("Resultado") },
            text = { Text("Puntaje: $puntaje / $total") },
            confirmButton = {
                TextButton(onClick = {
                    showResultDialog = false
                    onFinish(triviaId)
                }) {
                    Text("OK")
                }
            }
        )
    }
}
