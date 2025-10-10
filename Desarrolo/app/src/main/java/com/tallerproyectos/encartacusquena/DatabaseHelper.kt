package com.tallerproyectos.encartacusquena

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DatabaseHelper(private val context: Context) {
    companion object {
        private const val DB_NAME = "Encarta_v2.db" // nombre exacto en assets
        private const val TAG = "DatabaseHelper"
    }

    private val dbFilePath: String
        get() = context.getDatabasePath(DB_NAME).path

    /**
     * Abre la DB. Si no existe o se fuerza, copia desde assets.
     * @param forceReplace si es true, reemplaza la DB existente
     */
    fun openDatabase(forceReplace: Boolean = false): SQLiteDatabase {
        try {
            copyDatabaseIfNeeded(forceReplace)
        } catch (e: IOException) {
            Log.e(TAG, "Error copiando DB: ${e.message}")
            throw e
        }
        return SQLiteDatabase.openDatabase(dbFilePath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    @Throws(IOException::class)
    private fun copyDatabaseIfNeeded(forceReplace: Boolean) {
        val dbFile = File(dbFilePath)

        if (dbFile.exists()) {
            if (!forceReplace) {
                Log.d(TAG, "DB ya existe, no se copia")
                return
            } else {
                Log.d(TAG, "DB existente se reemplazará")
                dbFile.delete()
            }
        } else {
            dbFile.parentFile?.mkdirs()
        }

        context.assets.open(DB_NAME).use { input ->
            FileOutputStream(dbFile).use { output ->
                input.copyTo(output)
            }
        }
        Log.d(TAG, "DB copiada a $dbFilePath")
    }

    /**
     * Borra la DB actual. Útil para limpiar datos fantasma.
     */
    fun deleteDatabase() {
        val dbFile = File(dbFilePath)
        if (dbFile.exists()) {
            dbFile.delete()
            Log.d(TAG, "DB borrada de $dbFilePath")
        }
    }

}
