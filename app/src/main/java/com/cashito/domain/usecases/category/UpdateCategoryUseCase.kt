package com.cashito.domain.usecases.category

import com.cashito.domain.entities.category.Category
import com.cashito.domain.repositories.category.CategoryRepository

class UpdateCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(category: Category) {
        repository.updateCategory(category)
    }
}
