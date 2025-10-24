package com.tallerproyectos.encartacusquena

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**

 * Utilidad para gestionar archivos multimedia locales (imágenes, videos, audios)
 * desde la carpeta 'assets/' hacia el almacenamiento interno de la app.
 *
 * Esta clase copia archivos desde assets a un directorio local interno,
 * para que puedan ser usados por ExoPlayer o Coil como si fueran archivos locales.
 */
object MediaUtils {

    /**

     * Copia un archivo desde assets (por ejemplo: "videos/mi_video.mp4" o "imagenes/foto1.jpg")
     * al directorio interno de la app (/data/data/tu.app/files/media/).
     *
     * @param context Contexto de la aplicación.
     * @param assetRelativePath Ruta relativa dentro de assets (sin "android_asset/").
     * @return Ruta absoluta al archivo copiado en el almacenamiento interno.
     */
    fun copyAssetToFiles(context: Context, assetRelativePath: String): String {
        // Carpeta destino dentro del almacenamiento interno: /files/media/
        val destDir = File(context.filesDir, "media")
        if (!destDir.exists()) destDir.mkdirs() // Crea la carpeta si no existe

        // Extrae el nombre del archivo (lo que está después del último "/")
        val fileName = assetRelativePath.substringAfterLast('/')
        val destFile = File(destDir, fileName)

        // Si ya existe una copia previa, la devuelve sin volver a copiar
        if (destFile.exists()) {
            return destFile.absolutePath
        }

        // Intentamos abrir el archivo desde assets y copiarlo
        try {
            context.assets.open(assetRelativePath).use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: IOException) {
            // Si hay error, se imprime en Logcat y devolvemos un path vacío
            e.printStackTrace()
            return ""
        }

        return destFile.absolutePath
    }
}

