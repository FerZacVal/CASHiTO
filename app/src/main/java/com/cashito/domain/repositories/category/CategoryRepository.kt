package com.cashito.domain.repositories.category

import com.cashito.domain.entities.category.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun addCategory(category: Category)
    fun observeCategories(): Flow<List<Category>>
    suspend fun getCategoryById(categoryId: String): Category? // AÑADIDO
    suspend fun updateCategory(category: Category) // ACTUALIZADO
}
