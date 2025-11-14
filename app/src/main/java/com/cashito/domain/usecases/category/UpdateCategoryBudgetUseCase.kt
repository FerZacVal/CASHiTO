package com.cashito.domain.usecases.category

import com.cashito.domain.repositories.category.CategoryRepository

class UpdateCategoryBudgetUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(categoryId: String, newBudget: Double?) {
        //repository.updateCategoryBudget(categoryId, newBudget)
    }
}
