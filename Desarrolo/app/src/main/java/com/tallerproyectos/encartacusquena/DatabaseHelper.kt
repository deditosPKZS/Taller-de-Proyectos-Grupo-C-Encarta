package com.tallerproyectos.encartacusquena

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DatabaseHelper(private val context: Context) {
    companion object {
        private const val DB_NAME = "Encarta.db" // nombre exacto en assets
        private const val TAG = "DatabaseHelper"
    }

    private val dbFilePath: String
        get() = context.getDatabasePath(DB_NAME).path

    /**
     * Abre la DB. Si no existe, copia desde assets (primera ejecución).
     */
    fun openDatabase(): SQLiteDatabase {
        try {
            copyDatabaseIfNeeded()
        } catch (e: IOException) {
            Log.e(TAG, "Error copiando DB: ${e.message}")
            throw e
        }
        return SQLiteDatabase.openDatabase(dbFilePath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    @Throws(IOException::class)
    private fun copyDatabaseIfNeeded() {
        val dbFile = File(dbFilePath)
        if (dbFile.exists()) {
            // ya existe
            return
        }
        dbFile.parentFile?.mkdirs()
        context.assets.open(DB_NAME).use { input ->
            FileOutputStream(dbFile).use { output ->
                input.copyTo(output)
            }
        }
        Log.d(TAG, "DB copiada a $dbFilePath")
    }
}
