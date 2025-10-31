package com.cashito.domain.usecases.goal

import com.cashito.domain.entities.goal.Goal
import com.cashito.domain.repositories.goal.GoalRepository
import kotlinx.coroutines.flow.Flow

class GetGoalsUseCase(private val repository: GoalRepository) {
    operator fun invoke(): Flow<List<Goal>> {
        return repository.getGoals()
    }
}
