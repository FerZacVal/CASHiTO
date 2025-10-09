package com.cashito.domain.entities.category

/**
 * Representa una categoría para organizar los gastos.
 * En la base de datos (Firestore), esto corresponde a un documento dentro de una colección de nivel superior `Categorias`.
 */
data class Category(
    /**
     * El ID único de la categoría.
     * Mapea desde el ID del documento en la colección `Categorias`.
     */
    val id: String,

    /**
     * El nombre de la categoría (ej. "Comida", "Transporte").
     * Mapea desde el campo `name` en el documento de Firestore.
     */
    val name: String,

    /**
     * Un identificador para un icono que represente la categoría (opcional).
     * Mapea desde el campo `icon` en el documento de Firestore.
     */
    val icon: String? = null,

    /**
     * Un color en formato hexadecimal (ej. "#FF5733") para la categoría (opcional).
     * Mapea desde el campo `color` en el documento de Firestore.
     */
    val color: String? = null
)
