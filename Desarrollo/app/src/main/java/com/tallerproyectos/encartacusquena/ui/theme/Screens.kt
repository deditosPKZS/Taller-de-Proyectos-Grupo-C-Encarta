package com.tallerproyectos.encartacusquena.ui

import android.net.Uri
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import android.widget.MediaController
import android.widget.VideoView
import android.util.Log
import com.tallerproyectos.encartacusquena.data.Repository
import com.tallerproyectos.encartacusquena.model.*
import androidx.compose.material.icons.filled.Close

import java.io.File

// ----------------- IdiomaScreen -----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdiomaScreen(onIdiomaSeleccionado: (String) -> Unit) {
    Scaffold { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Seleccione el idioma", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(24.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = { onIdiomaSeleccionado("es") }) {
                Text("Español")
            }
            Spacer(Modifier.height(12.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = { onIdiomaSeleccionado("qu") }) {
                Text("Quechua")
            }
        }
    }
}

// ----------------- CategoriaScreen -----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaScreen(categorias: List<Categoria>, onCategoriaClick: (Categoria) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Categorías") })
        }
    ) { inner ->
        if (categorias.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay categorías", textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(Modifier.padding(inner).padding(16.dp)) {
                items(categorias) { cat ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onCategoriaClick(cat) }
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(cat.nombre, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

// ----------------- ConceptoScreen (CON IMÁGENES Y VIDEOS) -----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConceptoScreen(
    categoriaId: Int,
    conceptos: List<Concepto>,
    repository: Repository,
    onTriviaClick: (Trivia) -> Unit,
    onBack: () -> Unit
) {
    val trivias = remember { repository.safeGetTriviasPorCategoria(categoriaId) }
    var searchText by remember { mutableStateOf("") }

    // 🔍 Filtrar conceptos según lo que escriba el usuario
    val conceptosFiltrados = remember(searchText, conceptos) {
        if (searchText.isBlank()) conceptos
        else conceptos.filter {
            it.titulo.contains(searchText, ignoreCase = true) ||
                    it.contenido.contains(searchText, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conceptos y Trivias") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp)
        ) {
            // 🧠 Campo de búsqueda
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Buscar concepto...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true,
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Borrar búsqueda")
                        }
                    }
                }
            )

            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp)
            ) {
                item {
                    Text(
                        "Conceptos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                }

                if (conceptosFiltrados.isEmpty()) {
                    item {
                        Text("No hay conceptos que coincidan", modifier = Modifier.padding(8.dp))
                    }
                } else {
                    items(conceptosFiltrados) { concepto ->
                        ConceptoCard(concepto = concepto)
                    }
                }

                item {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Trivias disponibles",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                }

                if (trivias.isEmpty()) {
                    item {
                        Text("No hay trivias", modifier = Modifier.padding(8.dp))
                    }
                } else {
                    items(trivias) { trivia ->
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { onTriviaClick(trivia) },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(trivia.nombre, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}


// Componente separado para la tarjeta de concepto
@Composable
fun ConceptoCard(concepto: Concepto) {
    val context = LocalContext.current
    var showVideo by remember { mutableStateOf(false) }
    var imageLoadError by remember { mutableStateOf(false) }

    Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                concepto.titulo,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            // Mostrar imagen si existe
            concepto.imagen?.let { imagePath ->
                if (!imageLoadError) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(context)
                                .data("file:///android_asset/$imagePath")
                                .crossfade(true)
                                .listener(
                                    onError = { _, _ ->
                                        imageLoadError = true
                                        Log.e("ConceptoCard", "Error cargando imagen: $imagePath")
                                    },
                                    onSuccess = { _, _ ->
                                        Log.d("ConceptoCard", "Imagen cargada: $imagePath")
                                    }
                                )
                                .build()
                        ),
                        contentDescription = concepto.titulo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        "⚠️ Imagen no disponible: $imagePath",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Text(concepto.contenido, style = MaterialTheme.typography.bodyMedium)

            // Mostrar video si existe
            concepto.video?.let { videoPath ->
                Spacer(Modifier.height(8.dp))

                if (!showVideo) {
                    Button(
                        onClick = { showVideo = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                        Spacer(Modifier.width(8.dp))
                        Text("Ver video")
                    }
                } else {
                    VideoPlayer(
                        videoPath = videoPath,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                }
            }
        }
    }
}

// Componente separado para el reproductor de video usando MediaPlayer directamente
@Composable
fun VideoPlayer(videoPath: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var debugInfo by remember { mutableStateOf<String?>(null) }

    // Verificar archivos disponibles al crear el componente
    LaunchedEffect(videoPath) {
        try {
            val assetManager = context.assets
            val videoFiles = assetManager.list("videos")?.toList() ?: emptyList()
            val imageFiles = assetManager.list("imagenes")?.toList() ?: emptyList()

            debugInfo = "Videos disponibles: ${videoFiles.joinToString(", ")}\n\n" +
                    "Buscando: $videoPath"

            Log.d("VideoPlayer", "===== DEBUG INFO =====")
            Log.d("VideoPlayer", "Buscando: $videoPath")
            Log.d("VideoPlayer", "Videos disponibles: $videoFiles")
            Log.d("VideoPlayer", "======================")
        } catch (e: Exception) {
            Log.e("VideoPlayer", "Error listando assets: ${e.message}")
        }
    }

    if (errorMessage != null) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "⚠️ Error al cargar video",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    errorMessage ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )

                debugInfo?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    } else {
        AndroidView(
            factory = { ctx ->
                android.view.SurfaceView(ctx).apply {
                    holder.addCallback(object : android.view.SurfaceHolder.Callback {
                        var mediaPlayer: android.media.MediaPlayer? = null

                        override fun surfaceCreated(holder: android.view.SurfaceHolder) {
                            try {
                                Log.d("VideoPlayer", "Intentando abrir: $videoPath")

                                val assetManager = ctx.assets
                                val afd = assetManager.openFd(videoPath)

                                mediaPlayer = android.media.MediaPlayer().apply {
                                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                                    afd.close()

                                    setDisplay(holder)
                                    setOnPreparedListener { mp ->
                                        Log.d("VideoPlayer", "✅ Video preparado exitosamente")
                                        mp.start()
                                    }
                                    setOnErrorListener { _, what, extra ->
                                        Log.e("VideoPlayer", "MediaPlayer error: what=$what, extra=$extra")
                                        errorMessage = "Error de reproducción"
                                        true
                                    }
                                    setOnCompletionListener {
                                        Log.d("VideoPlayer", "Video completado")
                                    }
                                    prepareAsync()
                                }
                            } catch (e: Exception) {
                                Log.e("VideoPlayer", "❌ Error: ${e.message}", e)
                                errorMessage = "Archivo no encontrado: $videoPath"
                            }
                        }

                        override fun surfaceChanged(holder: android.view.SurfaceHolder, format: Int, width: Int, height: Int) {}

                        override fun surfaceDestroyed(holder: android.view.SurfaceHolder) {
                            mediaPlayer?.release()
                            mediaPlayer = null
                        }
                    })
                }
            },
            modifier = modifier
        )
    }
}

// ----------------- PreguntaScreen (MEJORADO) -----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreguntaScreen(
    preguntas: List<Pregunta>,
    triviaId: Int,
    repository: Repository,
    onViewResults: (Int) -> Unit,
    onBack: () -> Unit
) {
    var index by remember { mutableIntStateOf(0) }
    var puntaje by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var showResultDialog by remember { mutableStateOf(false) }
    val total = preguntas.size
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trivia") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { inner ->
        if (preguntas.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay preguntas para esta trivia")
            }
            return@Scaffold
        }

        val pregunta = preguntas.getOrNull(index) ?: return@Scaffold
        val opcionesValidas = pregunta.getOpcionesValidas()

        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {
            LinearProgressIndicator(
                progress = { (index + 1).toFloat() / total },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Text("Pregunta ${index + 1} de $total", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))

            Card(Modifier.fillMaxWidth()) {
                Text(
                    pregunta.enunciado,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            opcionesValidas.forEach { (key, texto) ->
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            selectedOption = if (selectedOption == key) null else key
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedOption == key) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption == key,
                            onClick = {
                                selectedOption = if (selectedOption == key) null else key
                            }
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(texto)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    if (selectedOption == null) return@Button
                    if (selectedOption == pregunta.correcta) puntaje++
                    selectedOption = null

                    if (index + 1 < total) {
                        index++
                    } else {
                        scope.launch {
                            try {
                                repository.saveResultado(triviaId, puntaje)
                            } catch (e: Exception) {
                                Log.e("PreguntaScreen", "Error guardando resultado", e)
                            }
                            showResultDialog = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedOption != null
            ) {
                Text(if (index + 1 < total) "Siguiente" else "Terminar")
            }

            if (showResultDialog) {
                AlertDialog(
                    onDismissRequest = { showResultDialog = false },
                    title = { Text("¡Trivia completada!") },
                    text = { Text("Tu puntaje: $puntaje de $total preguntas correctas") },
                    confirmButton = {
                        TextButton(onClick = {
                            showResultDialog = false
                            scope.launch {
                                delay(400)
                                onViewResults(triviaId)
                            }
                        }) {
                            Text("Ver resultados")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showResultDialog = false
                            onBack()
                        }) {
                            Text("Salir")
                        }
                    }
                )
            }
        }
    }
}

// ----------------- ResultadosScreen -----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadosScreen(
    triviaId: Int,
    repository: Repository,
    onBack: () -> Unit
) {
    val preguntas = remember { repository.safeGetPreguntasPorTrivia(triviaId) }
    val resultadosGuardados = remember { repository.safeGetResultadosPorTrivia(triviaId) }
    val ultimoPuntaje = resultadosGuardados.firstOrNull()?.puntaje ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Tu puntaje", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "$ultimoPuntaje / ${preguntas.size}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("Respuestas correctas:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            LazyColumn {
                items(preguntas) { pregunta ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(pregunta.enunciado, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(8.dp))
                            val respuestaTexto = when(pregunta.correcta) {
                                "a" -> pregunta.opcionA
                                "b" -> pregunta.opcionB
                                "c" -> pregunta.opcionC
                                "d" -> pregunta.opcionD
                                else -> "-"
                            }
                            Text(
                                "✓ $respuestaTexto",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------- BottomBarItem (simple) -----------------
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Home: BottomNavItem("categorias", "Categorías", Icons.Default.Home)
    object Results: BottomNavItem("mis_resultados", "Resultados", Icons.Default.List)
    object Settings: BottomNavItem("idioma", "Idioma", Icons.Default.Settings)
}