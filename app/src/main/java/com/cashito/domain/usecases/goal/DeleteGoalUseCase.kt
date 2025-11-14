
package com.cashito.domain.usecases.goal

import com.cashito.domain.repositories.goal.GoalRepository

class DeleteGoalUseCase(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(id: String) {
        return repository.deleteGoal(id)
    }
}
