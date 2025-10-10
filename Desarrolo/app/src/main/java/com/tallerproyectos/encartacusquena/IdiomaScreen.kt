@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.tallerproyectos.encartacusquena

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
            Button(modifier = Modifier.fillMaxWidth(), onClick = { onIdiomaSeleccionado("es") }) {
                Text("Español")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = { onIdiomaSeleccionado("qu") }) {
                Text("Quechua")
            }
        }
    }
}
