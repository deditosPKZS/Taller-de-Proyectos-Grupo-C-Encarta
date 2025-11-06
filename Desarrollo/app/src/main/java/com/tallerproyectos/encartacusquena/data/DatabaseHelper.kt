package com.tallerproyectos.encartacusquena.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.FileOutputStream

/**
 * Copia la DB prellenada desde assets/databases/ a la carpeta de bases de datos.
 * Luego abre la DB con SQLiteDatabase.openDatabase.
 */
class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        private const val DB_NAME = "Encarta3.db"
        private const val DB_VERSION = 1
        private const val TAG = "DatabaseHelper"
    }

    private val dbPath: String
        get() = context.getDatabasePath(DB_NAME).path

    override fun onCreate(db: SQLiteDatabase?) {
        // La DB viene prellenada, pero creamos tabla Resultado si no existe
        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS Resultado (
                id_resultado INTEGER PRIMARY KEY AUTOINCREMENT,
                id_trivia INTEGER NOT NULL,
                puntaje INTEGER NOT NULL,
                fecha TEXT NOT NULL,
                FOREIGN KEY (id_trivia) REFERENCES Trivia(id_trivia)
            )
        """.trimIndent())
        Log.d(TAG, "onCreate ejecutado")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Implementa lógica de upgrade si es necesario
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        // Aseguramos que la tabla Resultado existe
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Resultado (
                id_resultado INTEGER PRIMARY KEY AUTOINCREMENT,
                id_trivia INTEGER NOT NULL,
                puntaje INTEGER NOT NULL,
                fecha TEXT NOT NULL,
                FOREIGN KEY (id_trivia) REFERENCES Trivia(id_trivia)
            )
        """.trimIndent())
        Log.d(TAG, "onOpen ejecutado - Tabla Resultado verificada")
    }

    /**
     * Abre la base de datos. Si no existe, la copia desde assets.
     */
    fun openDatabase(): SQLiteDatabase {
        val dbFile = context.getDatabasePath(DB_NAME)

        // Si no existe, copiamos desde assets
        if (!dbFile.exists()) {
            Log.d(TAG, "DB no existe, copiando desde assets...")
            copyDatabaseFromAssets()
        }

        // Abrimos la base de datos
        val db = SQLiteDatabase.openDatabase(
            dbPath,
            null,
            SQLiteDatabase.OPEN_READWRITE or SQLiteDatabase.CREATE_IF_NECESSARY
        )

        // Llamamos manualmente a onOpen para crear tabla Resultado
        onOpen(db)

        // Verificamos que la DB tenga datos
        verifyDatabase(db)

        return db
    }

    /**
     * Copia la base de datos desde assets a la carpeta de la app
     */
    private fun copyDatabaseFromAssets() {
        val dbFile = context.getDatabasePath(DB_NAME)
        dbFile.parentFile?.mkdirs()

        try {
            // Intenta primero con extensión .db
            val assetFileName = "databases/$DB_NAME"

            context.assets.open(assetFileName).use { input ->
                FileOutputStream(dbFile).use { output ->
                    val buffer = ByteArray(8192)
                    var length: Int
                    while (input.read(buffer).also { length = it } > 0) {
                        output.write(buffer, 0, length)
                    }
                    output.flush()
                }
            }
            Log.d(TAG, "✅ DB copiada exitosamente a ${dbFile.path}")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al copiar DB desde assets", e)

            // Intenta sin extensión como fallback
            try {
                val assetFileNameNoExt = "databases/Encarta3"
                context.assets.open(assetFileNameNoExt).use { input ->
                    FileOutputStream(dbFile).use { output ->
                        val buffer = ByteArray(8192)
                        var length: Int
                        while (input.read(buffer).also { length = it } > 0) {
                            output.write(buffer, 0, length)
                        }
                        output.flush()
                    }
                }
                Log.d(TAG, "✅ DB copiada (sin extensión) a ${dbFile.path}")
            } catch (e2: Exception) {
                Log.e(TAG, "❌ Error en ambos intentos de copia", e2)
                throw e2
            }
        }
    }

    /**
     * Verifica que la base de datos tenga las tablas esperadas
     */
    private fun verifyDatabase(db: SQLiteDatabase) {
        try {
            val cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table'",
                null
            )
            val tables = mutableListOf<String>()
            while (cursor.moveToNext()) {
                tables.add(cursor.getString(0))
            }
            cursor.close()

            Log.d(TAG, "📊 Tablas en DB: $tables")

            // Verificar tabla Categoria específicamente
            val categoriaCount = db.rawQuery(
                "SELECT COUNT(*) FROM Categoria",
                null
            )
            if (categoriaCount.moveToFirst()) {
                val count = categoriaCount.getInt(0)
                Log.d(TAG, "📊 Total categorías en DB: $count")
            }
            categoriaCount.close()

            // Verificar idiomas disponibles
            val idiomasCursor = db.rawQuery(
                "SELECT DISTINCT id_idioma FROM Categoria",
                null
            )
            val idiomas = mutableListOf<String>()
            while (idiomasCursor.moveToNext()) {
                idiomas.add(idiomasCursor.getString(0))
            }
            idiomasCursor.close()
            Log.d(TAG, "🌐 Idiomas en DB: $idiomas")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al verificar DB", e)
        }
    }

    /**
     * Elimina la base de datos (útil para debugging)
     */
    fun deleteDatabase() {
        val dbFile = context.getDatabasePath(DB_NAME)
        if (dbFile.exists()) {
            dbFile.delete()
            Log.d(TAG, "🗑️ DB eliminada: ${dbFile.path}")
        }
    }

    /**
     * Fuerza recopiar la DB desde assets
     */
    fun resetDatabase() {
        deleteDatabase()
        openDatabase()
    }
}