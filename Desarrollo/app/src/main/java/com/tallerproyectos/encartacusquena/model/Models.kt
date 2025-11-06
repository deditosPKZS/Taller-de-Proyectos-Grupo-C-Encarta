package com.tallerproyectos.encartacusquena.model

// Modelos de datos simples (Kotlin data classes)

data class Categoria(
    val id: Int,
    val idIdioma: String,
    val nombre: String
)

data class Concepto(
    val id: Int,
    val idCategoria: Int,
    val titulo: String,
    val contenido: String,
    val recurso: String? = null,
    val imagen: String? = null,
    val video: String? = null
)

data class Trivia(
    val id: Int,
    val idCategoria: Int,
    val nombre: String
)

data class Pregunta(
    val idPregunta: Int,
    val idTrivia: Int,
    val enunciado: String,
    val opcionA: String,
    val opcionB: String,
    val opcionC: String,
    val opcionD: String,
    val correcta: String
) {
    // Helper para obtener solo las opciones no vacías
    fun getOpcionesValidas(): List<Pair<String, String>> {
        return listOfNotNull(
            if (opcionA.isNotBlank()) "a" to opcionA else null,
            if (opcionB.isNotBlank()) "b" to opcionB else null,
            if (opcionC.isNotBlank()) "c" to opcionC else null,
            if (opcionD.isNotBlank()) "d" to opcionD else null
        )
    }
}

data class Resultado(
    val id: Int,
    val idTrivia: Int,
    val puntaje: Int,
    val fecha: String
)