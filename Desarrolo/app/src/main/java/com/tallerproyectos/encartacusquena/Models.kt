package com.tallerproyectos.encartacusquena

// Modelo de Categoría
data class Categoria(
    val id: Int,
    val idIdioma: String,
    val nombre: String
)

// Modelo de Concepto (opcional, si quieres mostrar conceptos)
data class Concepto(
    val id: Int,
    val categoriaId: Int,
    val titulo: String,
    val contenido: String,
    val recurso: String?
)

// Modelo de Trivia
data class Trivia(
    val id: Int,
    val categoriaId: Int,
    val nombre: String
)

// Modelo de Pregunta
data class Pregunta(
    val id: Int,
    val idTrivia: Int,
    val enunciado: String,
    val opcionA: String,
    val opcionB: String,
    val opcionC: String,
    val opcionD: String,
    val correcta: String,
    val recurso: String? = null
)

// Modelo de Resultado
data class Resultado(
    val id: Int,
    val idTrivia: Int,
    val puntaje: Int,
    val fecha: String
)
