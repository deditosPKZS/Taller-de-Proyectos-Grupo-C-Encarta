package com.tallerproyectos.encartacusquena

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun IdiomaScreen(onIdiomaSeleccionado: () -> Unit) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Seleccione el idioma", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onIdiomaSeleccionado() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Español")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onIdiomaSeleccionado() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Quechua")
            }
        }
    }
}
