package com.cashito.domain.usecases.category

import com.cashito.domain.entities.category.Category
import com.cashito.domain.repositories.category.CategoryRepository

class GetCategoryByIdUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(categoryId: String): Category? {
        return repository.getCategoryById(categoryId)
    }
}
