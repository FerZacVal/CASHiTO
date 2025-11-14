package com.cashito.domain.repositories.category

import com.cashito.domain.entities.category.Category
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio para gestionar las categorías.
 * Define las operaciones que la capa de dominio puede realizar sobre las categorías,
 * independientemente de la fuente de datos (Firebase, Room, etc.).
 */
interface CategoryRepository {

    /**
     * Añade una nueva categoría.
     *
     * @param category La categoría a añadir.
     */
    suspend fun addCategory(category: Category)

    /**
     * Observa la lista de categorías del usuario en tiempo real.
     *
     * @return Un Flow que emite la lista de categorías cada vez que hay un cambio.
     */
    fun observeCategories(): Flow<List<Category>>
}
