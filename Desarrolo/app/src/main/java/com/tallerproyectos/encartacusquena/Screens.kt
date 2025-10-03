@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.tallerproyectos.encartacusquena

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog

// ----------------- IdiomaScreen -----------------
@Composable
fun IdiomaScreen(onIdiomaSeleccionado: (String) -> Unit) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Seleccione el idioma", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = { onIdiomaSeleccionado("ES") }) {
                Text("Español")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = { onIdiomaSeleccionado("QH") }) {
                Text("Quechua")
            }
        }
    }
}

// ----------------- CategoriaScreen -----------------
@Composable
fun CategoriaScreen(categorias: List<Categoria>, onCategoriaClick: (Categoria) -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Categorías") })
    }) { innerPadding ->
        if (categorias.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("No hay categorías", textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
                items(categorias) { cat ->
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onCategoriaClick(cat) }
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(cat.nombre, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------- LeccionScreen -----------------
@Composable
fun LeccionScreen(lecciones: List<Leccion>, onLeccionClick: (Leccion) -> Unit, onBack: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Lecciones") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        )
    }) { innerPadding ->
        if (lecciones.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("No hay lecciones", textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
                items(lecciones) { lec ->
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onLeccionClick(lec) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(lec.titulo, fontWeight = FontWeight.SemiBold)
                            lec.descripcion?.let { desc ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(desc, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------- PreguntaScreen (quiz básico) -----------------
@Composable
fun PreguntaScreen(
    preguntas: List<Pregunta>,
    leccionId: Int,
    onFinish: (puntaje: Int) -> Unit,
    onBack: () -> Unit
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
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        )
    }) { innerPadding ->
        if (preguntas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("No hay preguntas para esta lección")
            }
            return@Scaffold
        }

        val p = preguntas.getOrNull(index)
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
        ) {
            Text(text = "Pregunta ${index + 1} / $total", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = p?.enunciado ?: "", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Opciones
            val options = listOfNotNull(p?.opcionA, p?.opcionB, p?.opcionC, p?.opcionD)
            Column {
                options.forEach { opt ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { selectedOption = opt }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = (selectedOption == opt), onClick = { selectedOption = opt })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(opt)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    // evaluar
                    if (selectedOption != null) {
                        if (selectedOption == p?.correcta) puntaje++
                        selectedOption = null
                        if (index + 1 < total) {
                            index++
                        } else {
                            // fin
                            showResultDialog = true
                        }
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
            onDismissRequest = { /* no-op */ },
            confirmButton = {
                TextButton(onClick = {
                    showResultDialog = false
                    onFinish(puntaje)
                }) { Text("OK") }
            },
            title = { Text("Resultado") },
            text = { Text("Puntaje: $puntaje  / $total") }
        )
    }
}
