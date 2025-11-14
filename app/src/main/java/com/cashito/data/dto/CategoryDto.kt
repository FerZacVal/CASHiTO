package com.cashito.data.dto

import com.google.firebase.firestore.Exclude

/**
 * Data Transfer Object (DTO) para la entidad Category.
 * Representa la estructura de un documento en la subcolección 'Categorias' de un usuario en Firestore.
 */
data class CategoryDto(
    @get:Exclude var id: String = "", // ID del documento, se llena manualmente y se excluye de la escritura
    val name: String = "",
    val icon: String? = null,
    val color: String? = null,
    val type: String = "", // "ingreso" o "gasto"
    val budget: Double? = null, // Nuestro nuevo campo, opcional
    val userId: String = "" // Para saber a qué usuario pertenece la categoría
)
