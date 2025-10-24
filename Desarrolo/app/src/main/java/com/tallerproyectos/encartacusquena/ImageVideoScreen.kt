@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.tallerproyectos.encartacusquena

import android.net.Uri
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ui.PlayerView
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@Composable
fun ImageVideoScreen(
    onBack: () -> Unit = {},
    imagenAssets: List<String> = listOf("imagenes/PlazaCusco.webp", "imagenes/Sacsayhuaman.jpg"),
    videoAsset: String = "videos/Sacsayhuaman.mp4"
) {
    val ctx = LocalContext.current
    val scroll = rememberScrollState()

    // Copia el video a la memoria interna y obtiene su ruta local
    val videoLocalPath by remember {
        mutableStateOf(MediaUtils.copyAssetToFiles(ctx, videoAsset))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Demostración multimedia") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Imágenes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Muestra las imágenes locales desde assets (usando URI de assets)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(imagenAssets) { assetPath ->
                    val assetUri = "file:///android_asset/$assetPath"
                    AsyncImage(
                        model = assetUri,
                        contentDescription = "Imagen: $assetPath",
                        modifier = Modifier
                            .size(160.dp)
                            .clickable { /* Aquí podrías abrir detalle */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Video", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            if (videoLocalPath.isNotEmpty()) {
                VideoPlayerLocal(videoLocalPath)
            } else {
                Text("No se pudo cargar el video local.", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun VideoPlayerLocal(localPath: String) {
    val ctx = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(ctx).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(localPath)))
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                useController = true
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 620)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    )
}
