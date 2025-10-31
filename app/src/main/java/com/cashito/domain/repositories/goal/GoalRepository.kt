package com.cashito.domain.repositories.goal

import com.cashito.domain.entities.goal.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    suspend fun createGoal(goal: Goal)
    fun getGoals(): Flow<List<Goal>>
}
