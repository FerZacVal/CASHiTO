
package com.cashito.domain.usecases.goal

import com.cashito.domain.entities.goal.Goal
import com.cashito.domain.repositories.goal.GoalRepository
import kotlinx.coroutines.flow.Flow

class GetGoalByIdUseCase(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(id: String): Flow<Goal?> {
        return repository.getGoalById(id)
    }
}
