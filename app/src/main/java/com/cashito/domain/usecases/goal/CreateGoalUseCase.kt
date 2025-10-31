package com.cashito.domain.usecases.goal

import com.cashito.domain.entities.goal.Goal
import com.cashito.domain.repositories.goal.GoalRepository

class CreateGoalUseCase(private val repository: GoalRepository) {
    suspend operator fun invoke(goal: Goal) {
        repository.createGoal(goal)
    }
}
