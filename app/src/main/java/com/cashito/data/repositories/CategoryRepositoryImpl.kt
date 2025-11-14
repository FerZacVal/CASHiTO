package com.cashito.data.repositories

import com.cashito.data.datasources.firebase.CategoryDataSource
import com.cashito.data.dto.CategoryDto
import com.cashito.domain.entities.category.Category
import com.cashito.domain.repositories.category.CategoryRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val dataSource: CategoryDataSource,
    private val auth: FirebaseAuth
) : CategoryRepository {

    override suspend fun addCategory(category: Category) {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
        val categoryDto = category.toDto(userId)
        dataSource.addCategory(categoryDto)
    }

    override fun observeCategories(): Flow<List<Category>> {
        return dataSource.observeCategories().map { dtoList ->
            dtoList.map { it.toDomain() }
        }
    }
}

// --- Mappers privados para esta implementación ---

private fun Category.toDto(userId: String): CategoryDto {
    return CategoryDto(
        name = this.name,
        icon = this.icon,
        color = this.color,
        budget = this.budget,
        userId = userId,
        type = if (this.budget != null) "gasto" else "ingreso" // Asumimos que solo los gastos tienen presupuesto
    )
}

private fun CategoryDto.toDomain(): Category {
    return Category(
        id = this.id,
        name = this.name,
        icon = this.icon,
        color = this.color,
        budget = this.budget
    )
}
