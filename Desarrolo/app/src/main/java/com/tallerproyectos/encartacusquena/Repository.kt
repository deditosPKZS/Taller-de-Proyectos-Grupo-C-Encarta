package com.tallerproyectos.encartacusquena

import android.content.Context
import android.database.sqlite.SQLiteDatabase

class Repository(context: Context) {

    private val dbHelper = DatabaseHelper(context)
    var selectedLanguage: String = "es"

    // ---------------------------
    // Categorías filtradas por idioma
    // ---------------------------
    fun getCategorias(): List<Categoria> {
        val lista = mutableListOf<Categoria>()
        val db: SQLiteDatabase = dbHelper.openDatabase()
        val cursor = db.rawQuery(
            "SELECT id_categoria, id_idioma, nombre FROM Categoria WHERE id_idioma = ? ORDER BY nombre",
            arrayOf(selectedLanguage)
        )
        while (cursor.moveToNext()) {
            lista.add(
                Categoria(
                    id = cursor.getInt(0),
                    idIdioma = cursor.getString(1),
                    nombre = cursor.getString(2)
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }

    // ---------------------------
    // Conceptos por categoría
    // ---------------------------
    fun getConceptosPorCategoria(categoriaId: Int): List<Concepto> {
        val lista = mutableListOf<Concepto>()
        val db: SQLiteDatabase = dbHelper.openDatabase()
        val cursor = db.rawQuery(
            "SELECT id_concepto, id_categoria, titulo, contenido, recurso FROM Concepto WHERE id_categoria = ?",
            arrayOf(categoriaId.toString())
        )
        while (cursor.moveToNext()) {
            lista.add(
                Concepto(
                    id = cursor.getInt(0),
                    categoriaId = cursor.getInt(1),  // ✅ nombre corregido
                    titulo = cursor.getString(2),
                    contenido = cursor.getString(3),
                    recurso = cursor.getString(4)    // ✅ nullable
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }


    // ---------------------------
    // Trivias por categoría
    // ---------------------------
    fun getTriviaByCategoria(categoriaId: Int): Trivia? {
        val db = dbHelper.openDatabase()
        val cursor = db.rawQuery(
            "SELECT id_trivia, id_categoria, nombre FROM Trivia WHERE id_categoria = ? LIMIT 1",
            arrayOf(categoriaId.toString())
        )
        var trivia: Trivia? = null
        if (cursor.moveToFirst()) {
            trivia = Trivia(
                id = cursor.getInt(0),
                categoriaId = cursor.getInt(1),
                nombre = cursor.getString(2)
            )
        }
        cursor.close()
        db.close()
        return trivia
    }

    // ---------------------------
    // Preguntas por trivia
    // ---------------------------
    fun getPreguntasPorTrivia(triviaId: Int): List<Pregunta> {
        val lista = mutableListOf<Pregunta>()
        val db: SQLiteDatabase = dbHelper.openDatabase()
        val cursor = db.rawQuery(
            "SELECT id_pregunta, id_trivia, enunciado, opcion_a, opcion_b, opcion_c, opcion_d, correcta FROM Pregunta WHERE id_trivia = ?",
            arrayOf(triviaId.toString())
        )
        while (cursor.moveToNext()) {
            lista.add(
                Pregunta(
                    id = cursor.getInt(0),
                    idTrivia = cursor.getInt(1),
                    enunciado = cursor.getString(2),
                    opcionA = cursor.getString(3),
                    opcionB = cursor.getString(4),
                    opcionC = cursor.getString(5),
                    opcionD = cursor.getString(6),
                    correcta = cursor.getString(7)
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }

    // ---------------------------
    // Guardar resultados
    // ---------------------------
    fun saveResultado(triviaId: Int, puntaje: Int) {
        val db: SQLiteDatabase = dbHelper.openDatabase()
        val fecha = System.currentTimeMillis().toString()
        val stmt = db.compileStatement(
            "INSERT INTO Resultado (id_trivia, puntaje, fecha) VALUES (?, ?, ?)"
        )
        stmt.bindLong(1, triviaId.toLong())
        stmt.bindLong(2, puntaje.toLong())
        stmt.bindString(3, fecha)
        stmt.executeInsert()
        db.close()
    }

    // ---------------------------
    // Resultados de trivia
    // ---------------------------
    fun getResultadosPorTrivia(triviaId: Int): List<Resultado> {
        val lista = mutableListOf<Resultado>()
        val db: SQLiteDatabase = dbHelper.openDatabase()
        val cursor = db.rawQuery(
            "SELECT id_resultado, id_trivia, puntaje, fecha FROM Resultado WHERE id_trivia = ? ORDER BY id_resultado DESC",
            arrayOf(triviaId.toString())
        )
        while (cursor.moveToNext()) {
            lista.add(
                Resultado(
                    id = cursor.getInt(0),
                    idTrivia = cursor.getInt(1),
                    puntaje = cursor.getInt(2),
                    fecha = cursor.getString(3)
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }
    // ---------------------------
// Trivias por categoría
// ---------------------------
    fun getTriviasPorCategoria(categoriaId: Int): List<Trivia> {
        val lista = mutableListOf<Trivia>()
        val db = dbHelper.openDatabase()
        val cursor = db.rawQuery(
            "SELECT id_trivia, id_categoria, nombre FROM Trivia WHERE id_categoria = ?",
            arrayOf(categoriaId.toString())
        )
        while (cursor.moveToNext()) {
            lista.add(
                Trivia(
                    id = cursor.getInt(0),
                    categoriaId = cursor.getInt(1),
                    nombre = cursor.getString(2)
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }


    // ---------------------------
    // Wrappers seguros
    // ---------------------------
    fun safeGetCategorias() = try { getCategorias() } catch (e: Exception) { emptyList() }
    fun safeGetConceptosPorCategoria(categoriaId: Int) = try { getConceptosPorCategoria(categoriaId) } catch (e: Exception) { emptyList() }
    fun safeGetTriviasPorCategoria(categoriaId: Int) = try { getTriviasPorCategoria(categoriaId) } catch (e: Exception) { emptyList() }
    fun safeGetPreguntasPorTrivia(triviaId: Int) = try { getPreguntasPorTrivia(triviaId) } catch (e: Exception) { emptyList() }
    fun safeGetResultadosPorTrivia(triviaId: Int) = try { getResultadosPorTrivia(triviaId) } catch (e: Exception) { emptyList() }

    // ---------------------------
    // CategoriaId desde Trivia
    // ---------------------------
    fun getCategoriaIdPorTrivia(triviaId: Int): Int {
        val db: SQLiteDatabase = dbHelper.openDatabase()
        val cursor = db.rawQuery(
            "SELECT id_categoria FROM Trivia WHERE id_trivia = ?",
            arrayOf(triviaId.toString())
        )
        val categoriaId = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        db.close()
        return categoriaId
    }
}
