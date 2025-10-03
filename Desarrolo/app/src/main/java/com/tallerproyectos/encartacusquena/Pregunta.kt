package com.tallerproyectos.encartacusquena

data class Pregunta(
    val id: Int,
    val idLeccion: Int,
    val idioma: String,
    val enunciado: String,
    val opcionA: String?,
    val opcionB: String?,
    val opcionC: String?,
    val opcionD: String?,
    val correcta: String?,
    val recurso: String? = null
)
