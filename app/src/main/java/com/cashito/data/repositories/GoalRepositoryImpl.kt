package com.cashito.data.repositories

import com.cashito.data.datasources.firebase.GoalDataSource
import com.cashito.data.dto.GoalDto
import com.cashito.domain.entities.goal.Goal
import com.cashito.domain.repositories.goal.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class GoalRepositoryImpl(
    private val dataSource: GoalDataSource
) : GoalRepository {

    override suspend fun createGoal(goal: Goal) {
        val goalDto = goal.toDto()
        dataSource.addGoal(goalDto)
    }

    override fun getGoals(): Flow<List<Goal>> {
        return dataSource.observeGoals().map { dtoList ->
            dtoList.map { it.toDomain() }
        }
    }

    override suspend fun getGoalById(id: String): Flow<Goal?> {
        return dataSource.observeGoalById(id).map { it?.toDomain() }
    }

    override suspend fun deleteGoal(id: String) {
        dataSource.deleteGoal(id)
    }

    override suspend fun updateGoal(goal: Goal) {
        val goalDto = goal.toDto()
        dataSource.updateGoal(goalDto)
    }
}

private fun Goal.toDto(): GoalDto {
    return GoalDto(
        id = this.id,
        userId = this.userId,
        name = this.name,
        targetAmount = this.targetAmount,
        savedAmount = this.savedAmount,
        targetDate = this.targetDate,
        creationDate = this.creationDate,
        icon = this.icon,
        colorHex = this.colorHex,
        activeBoostId = this.activeBoostId,
        activeBoostApr = this.activeBoostApr,
        boostExpiryDate = this.boostExpiryDate,
        activeBoostProfit = this.activeBoostProfit
    )
}

private fun GoalDto.toDomain(): Goal {
    return Goal(
        id = this.id,
        userId = this.userId,
        name = this.name,
        targetAmount = this.targetAmount,
        savedAmount = this.savedAmount,
        targetDate = this.targetDate,
        creationDate = this.creationDate ?: Date(),
        icon = this.icon,
        colorHex = this.colorHex,
        activeBoostId = this.activeBoostId,
        activeBoostApr = this.activeBoostApr,
        boostExpiryDate = this.boostExpiryDate,
        activeBoostProfit = this.activeBoostProfit
    )
}
