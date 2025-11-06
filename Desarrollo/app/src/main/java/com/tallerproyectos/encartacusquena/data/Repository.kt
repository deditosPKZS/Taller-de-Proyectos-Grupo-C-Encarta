package com.tallerproyectos.encartacusquena.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.tallerproyectos.encartacusquena.model.*

/**
 * Repository: capa de acceso a datos (lee y escribe en la base de datos).
 * Gestiona categorías, conceptos, trivias, preguntas y resultados.
 */
class Repository(context: Context) {

    private val dbHelper = DatabaseHelper(context)
    var selectedLanguage: String = "es" // idioma actual, por defecto "es"

    // ---------------------------
    // CATEGORÍAS (filtradas por idioma y deduplicadas)
    // ---------------------------
    fun getCategorias(): List<Categoria> {
        val categorias = mutableListOf<Categoria>()
        var db: SQLiteDatabase? = null
        try {
            db = dbHelper.openDatabase()
            Log.d("Repository", "🔍 Buscando categorías para idioma: '$selectedLanguage'")

            // Verificar idiomas disponibles
            val checkCursor = db.rawQuery("SELECT DISTINCT id_idioma FROM Categoria", null)
            val availableLanguages = mutableListOf<String>()
            while (checkCursor.moveToNext()) {
                availableLanguages.add(checkCursor.getString(0) ?: "")
            }
            checkCursor.close()
            Log.d("Repository", "🌐 Idiomas disponibles en DB: $availableLanguages")

            // Obtener categorías DISTINTAS por nombre para el idioma seleccionado
            val cursor = db.rawQuery(
                """
                SELECT MIN(id_categoria) as id_categoria, id_idioma, nombre
                FROM Categoria
                WHERE id_idioma = ?
                GROUP BY nombre, id_idioma
                ORDER BY nombre
                """.trimIndent(),
                arrayOf(selectedLanguage)
            )

            Log.d("Repository", "📊 Cursor count: ${cursor.count}")

            while (cursor.moveToNext()) {
                val id = cursor.getInt(0)
                val idIdioma = cursor.getString(1) ?: selectedLanguage
                val nombre = cursor.getString(2) ?: ""

                categorias.add(
                    Categoria(
                        id = id,
                        idIdioma = idIdioma,
                        nombre = nombre
                    )
                )
                Log.d("Repository", "✅ Categoría: id=$id, nombre='$nombre', idioma='$idIdioma'")
            }
            cursor.close()

            Log.d("Repository", "✅ Total categorías cargadas (deduplicadas): ${categorias.size}")
        } catch (e: Exception) {
            Log.e("Repository", "❌ Error al obtener categorías", e)
            e.printStackTrace()
        } finally {
            db?.close()
        }
        return categorias
    }

    // ---------------------------
    // CONCEPTOS POR CATEGORÍA (CORREGIDO: incluye imagen y video)
    // ---------------------------
    fun getConceptosPorCategoria(categoriaId: Int): List<Concepto> {
        val conceptos = mutableListOf<Concepto>()
        var db: SQLiteDatabase? = null
        try {
            db = dbHelper.openDatabase()
            val cursor = db.rawQuery(
                """
                SELECT id_concepto, id_categoria, titulo, contenido, recurso, imagen, video
                FROM Concepto
                WHERE id_categoria = ?
                """.trimIndent(),
                arrayOf(categoriaId.toString())
            )

            while (cursor.moveToNext()) {
                conceptos.add(
                    Concepto(
                        id = cursor.getInt(0),
                        idCategoria = cursor.getInt(1),
                        titulo = cursor.getString(2) ?: "",
                        contenido = cursor.getString(3) ?: "",
                        recurso = cursor.getString(4),
                        imagen = cursor.getString(5),
                        video = cursor.getString(6)
                    )
                )
            }
            cursor.close()
            Log.d("Repository", "✅ Cargados ${conceptos.size} conceptos para categoría $categoriaId")
        } catch (e: Exception) {
            Log.e("Repository", "❌ Error al obtener conceptos", e)
            e.printStackTrace()
        } finally {
            db?.close()
        }
        return conceptos
    }

    // ---------------------------
    // TRIVIAS POR CATEGORÍA
    // ---------------------------
    fun getTriviasPorCategoria(categoriaId: Int): List<Trivia> {
        val trivias = mutableListOf<Trivia>()
        var db: SQLiteDatabase? = null
        try {
            db = dbHelper.openDatabase()
            val cursor = db.rawQuery(
                "SELECT id_trivia, id_categoria, nombre FROM Trivia WHERE id_categoria = ?",
                arrayOf(categoriaId.toString())
            )
            while (cursor.moveToNext()) {
                trivias.add(
                    Trivia(
                        id = cursor.getInt(0),
                        idCategoria = cursor.getInt(1),
                        nombre = cursor.getString(2) ?: ""
                    )
                )
            }
            cursor.close()
            Log.d("Repository", "✅ Cargadas ${trivias.size} trivias para categoría $categoriaId")
        } catch (e: Exception) {
            Log.e("Repository", "❌ Error al obtener trivias", e)
            e.printStackTrace()
        } finally {
            db?.close()
        }
        return trivias
    }

    // ---------------------------
    // PREGUNTAS POR TRIVIA (CORREGIDO: maneja opciones nulas)
    // ---------------------------
    fun getPreguntasPorTrivia(triviaId: Int): List<Pregunta> {
        val preguntas = mutableListOf<Pregunta>()
        var db: SQLiteDatabase? = null
        try {
            db = dbHelper.openDatabase()
            val cursor = db.rawQuery(
                """
                SELECT id_pregunta, id_trivia, enunciado, opcion_a, opcion_b, opcion_c, opcion_d, correcta
                FROM Pregunta
                WHERE id_trivia = ?
                """.trimIndent(),
                arrayOf(triviaId.toString())
            )
            while (cursor.moveToNext()) {
                preguntas.add(
                    Pregunta(
                        idPregunta = cursor.getInt(0),
                        idTrivia = cursor.getInt(1),
                        enunciado = cursor.getString(2) ?: "",
                        opcionA = cursor.getString(3) ?: "",
                        opcionB = cursor.getString(4) ?: "",
                        opcionC = cursor.getString(5) ?: "",
                        opcionD = cursor.getString(6) ?: "",
                        correcta = cursor.getString(7) ?: ""
                    )
                )
            }
            cursor.close()
            Log.d("Repository", "✅ Cargadas ${preguntas.size} preguntas para trivia $triviaId")
        } catch (e: Exception) {
            Log.e("Repository", "❌ Error al obtener preguntas", e)
            e.printStackTrace()
        } finally {
            db?.close()
        }
        return preguntas
    }

    // ---------------------------
    // GUARDAR RESULTADO
    // ---------------------------
    fun saveResultado(triviaId: Int, puntaje: Int) {
        var db: SQLiteDatabase? = null
        try {
            db = dbHelper.openDatabase()
            val fecha = System.currentTimeMillis().toString()
            val stmt = db.compileStatement(
                "INSERT INTO Resultado (id_trivia, puntaje, fecha) VALUES (?, ?, ?)"
            )
            stmt.bindLong(1, triviaId.toLong())
            stmt.bindLong(2, puntaje.toLong())
            stmt.bindString(3, fecha)
            stmt.executeInsert()
            Log.d("Repository", "✅ Resultado guardado: trivia=$triviaId, puntaje=$puntaje")
        } catch (e: Exception) {
            Log.e("Repository", "❌ Error al guardar resultado", e)
            e.printStackTrace()
        } finally {
            db?.close()
        }
    }

    // ---------------------------
    // RESULTADOS POR TRIVIA
    // ---------------------------
    fun getResultadosPorTrivia(triviaId: Int): List<Resultado> {
        val resultados = mutableListOf<Resultado>()
        var db: SQLiteDatabase? = null
        try {
            db = dbHelper.openDatabase()
            val cursor = db.rawQuery(
                "SELECT id_resultado, id_trivia, puntaje, fecha FROM Resultado WHERE id_trivia = ? ORDER BY id_resultado DESC",
                arrayOf(triviaId.toString())
            )
            while (cursor.moveToNext()) {
                resultados.add(
                    Resultado(
                        id = cursor.getInt(0),
                        idTrivia = cursor.getInt(1),
                        puntaje = cursor.getInt(2),
                        fecha = cursor.getString(3) ?: ""
                    )
                )
            }
            cursor.close()
            Log.d("Repository", "✅ Cargados ${resultados.size} resultados para trivia $triviaId")
        } catch (e: Exception) {
            Log.e("Repository", "❌ Error al obtener resultados", e)
            e.printStackTrace()
        } finally {
            db?.close()
        }
        return resultados
    }

    // ---------------------------
    // OBTENER ID DE CATEGORÍA DESDE ID DE TRIVIA
    // ---------------------------
    fun getCategoriaIdPorTrivia(triviaId: Int): Int {
        var db: SQLiteDatabase? = null
        var catId = 0
        try {
            db = dbHelper.openDatabase()
            val cursor = db.rawQuery(
                "SELECT id_categoria FROM Trivia WHERE id_trivia = ?",
                arrayOf(triviaId.toString())
            )
            if (cursor.moveToFirst()) {
                catId = cursor.getInt(0)
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("Repository", "❌ Error al obtener categoría por trivia", e)
            e.printStackTrace()
        } finally {
            db?.close()
        }
        return catId
    }

    // ---------------------------
    // WRAPPERS SEGUROS (para UI)
    // ---------------------------
    fun safeGetCategorias() = try {
        getCategorias()
    } catch (e: Exception) {
        Log.e("Repository", "❌ Error en safeGetCategorias", e)
        e.printStackTrace()
        emptyList()
    }

    fun safeGetConceptosPorCategoria(categoriaId: Int) = try {
        getConceptosPorCategoria(categoriaId)
    } catch (e: Exception) {
        Log.e("Repository", "❌ Error en safeGetConceptosPorCategoria", e)
        e.printStackTrace()
        emptyList()
    }

    fun safeGetTriviasPorCategoria(categoriaId: Int): List<Trivia> {
        return try {
            getTriviasPorCategoria(categoriaId)
        } catch (e: Exception) {
            Log.e("Repository", "❌ Error en safeGetTriviasPorCategoria", e)
            e.printStackTrace()
            emptyList()
        }
    }

    fun safeGetPreguntasPorTrivia(triviaId: Int): List<Pregunta> {
        return try {
            getPreguntasPorTrivia(triviaId)
        } catch (e: Exception) {
            Log.e("Repository", "❌ Error en safeGetPreguntasPorTrivia", e)
            e.printStackTrace()
            emptyList()
        }
    }

    fun safeGetResultadosPorTrivia(triviaId: Int) = try {
        getResultadosPorTrivia(triviaId)
    } catch (e: Exception) {
        Log.e("Repository", "❌ Error en safeGetResultadosPorTrivia", e)
        e.printStackTrace()
        emptyList()
    }
}