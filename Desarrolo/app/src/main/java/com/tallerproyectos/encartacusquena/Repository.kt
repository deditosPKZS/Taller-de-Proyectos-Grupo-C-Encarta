package com.tallerproyectos.encartacusquena

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class Repository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Idioma seleccionado; default "ES". Puede cambiarse desde IdiomaScreen.
    var selectedLanguage: String = "ES"

    fun getCategorias(): List<Categoria> {
        val lista = mutableListOf<Categoria>()
        val db: SQLiteDatabase = dbHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT id_categoria, nombre FROM Categoria ORDER BY nombre", null)
        while (cursor.moveToNext()) {
            lista.add(Categoria(cursor.getInt(0), cursor.getString(1)))
        }
        cursor.close()
        db.close()
        return lista
    }

    fun getLeccionesPorCategoria(categoriaId: Int): List<Leccion> {
        val lista = mutableListOf<Leccion>()
        val db: SQLiteDatabase = dbHelper.openDatabase()
        val cursor: Cursor = db.rawQuery(
            "SELECT id_leccion, id_categoria, titulo, descripcion FROM Leccion WHERE id_categoria = ?",
            arrayOf(categoriaId.toString())
        )
        while (cursor.moveToNext()) {
            lista.add(Leccion(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3)))
        }
        cursor.close()
        db.close()
        return lista
    }

    fun getPreguntasPorLeccion(leccionId: Int): List<Pregunta> {
        val lista = mutableListOf<Pregunta>()
        val db: SQLiteDatabase = dbHelper.openDatabase()
        val cursor: Cursor = db.rawQuery(
            """
            SELECT id_pregunta, id_leccion, idioma, enunciado, opcion_a, opcion_b, opcion_c, opcion_d, correcta, recurso
            FROM Pregunta
            WHERE id_leccion = ? AND idioma = ?
            """.trimIndent(),
            arrayOf(leccionId.toString(), selectedLanguage)
        )
        while (cursor.moveToNext()) {
            lista.add(
                Pregunta(
                    id = cursor.getInt(0),
                    idLeccion = cursor.getInt(1),
                    idioma = cursor.getString(2) ?: selectedLanguage,
                    enunciado = cursor.getString(3) ?: "",
                    opcionA = cursor.getString(4),
                    opcionB = cursor.getString(5),
                    opcionC = cursor.getString(6),
                    opcionD = cursor.getString(7),
                    correcta = cursor.getString(8),
                    recurso = cursor.getString(9)
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }

    // Guarda resultado simple (fecha en millis)
    fun saveResultado(leccionId: Int, puntaje: Int) {
        val db = dbHelper.openDatabase()
        val fecha = System.currentTimeMillis().toString()
        val stmt = db.compileStatement("INSERT INTO Resultado (id_leccion, puntaje, fecha) VALUES (?, ?, ?)")
        stmt.bindLong(1, leccionId.toLong())
        stmt.bindLong(2, puntaje.toLong())
        stmt.bindString(3, fecha)
        stmt.executeInsert()
        db.close()
    }

    fun getResultadosPorLeccion(leccionId: Int): List<Resultado> {
        val lista = mutableListOf<Resultado>()
        val db = dbHelper.openDatabase()
        val cursor = db.rawQuery(
            "SELECT id_resultado, id_leccion, puntaje, fecha FROM Resultado WHERE id_leccion = ? ORDER BY id_resultado DESC",
            arrayOf(leccionId.toString())
        )
        while (cursor.moveToNext()) {
            lista.add(Resultado(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3)))
        }
        cursor.close()
        db.close()
        return lista
    }
}
