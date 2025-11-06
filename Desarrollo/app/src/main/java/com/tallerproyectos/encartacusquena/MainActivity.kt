package com.tallerproyectos.encartacusquena

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tallerproyectos.encartacusquena.data.Repository
import com.tallerproyectos.encartacusquena.ui.theme.EncartaCusquenaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ========== DEBUG TEMPORAL: Verificar archivos en assets ==========
        Log.d("MainActivity", "========== VERIFICANDO ASSETS ==========")

        try {
            // Listar carpetas principales en assets
            val assetFolders = assets.list("") ?: emptyArray()
            Log.d("MainActivity", "📁 Carpetas en assets/: ${assetFolders.joinToString()}")

            // Listar imágenes
            try {
                val imageFiles = assets.list("imagenes") ?: emptyArray()
                Log.d("MainActivity", "🖼️ Imágenes encontradas (${imageFiles.size}): ${imageFiles.joinToString()}")
            } catch (e: Exception) {
                Log.e("MainActivity", "❌ No existe carpeta 'imagenes' o está vacía")
            }

            // Listar videos
            try {
                val videoFiles = assets.list("videos") ?: emptyArray()
                Log.d("MainActivity", "🎥 Videos encontrados (${videoFiles.size}): ${videoFiles.joinToString()}")
            } catch (e: Exception) {
                Log.e("MainActivity", "❌ No existe carpeta 'videos' o está vacía")
            }

            // Listar bases de datos
            try {
                val dbFiles = assets.list("databases") ?: emptyArray()
                Log.d("MainActivity", "💾 Bases de datos (${dbFiles.size}): ${dbFiles.joinToString()}")
            } catch (e: Exception) {
                Log.e("MainActivity", "❌ No existe carpeta 'databases' o está vacía")
            }

        } catch (e: Exception) {
            Log.e("MainActivity", "❌ Error listando assets: ${e.message}", e)
        }

        Log.d("MainActivity", "========================================")
        // ========== FIN DEBUG TEMPORAL ==========

        // Inicializamos repo con contexto
        val repository = Repository(this)

        setContent {
            EncartaCusquenaTheme {
                NavGraphScaffold(repository = repository)
            }
        }
    }
}