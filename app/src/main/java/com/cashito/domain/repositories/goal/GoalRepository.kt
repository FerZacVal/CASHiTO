package com.cashito.domain.repositories.goal

import com.cashito.domain.entities.goal.Goal
import com.cashito.domain.entities.transaction.Transaction
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    suspend fun createGoal(goal: Goal)
    fun getGoals(): Flow<List<Goal>>
    suspend fun getGoalById(id: String): Flow<Goal?>
    suspend fun deleteGoal(id: String)
    suspend fun updateGoal(goal: Goal)
    
    // Nuevo método para Withdraw Atómico
    suspend fun withdrawFromGoal(goalId: String, amount: Double, transaction: Transaction)
}
