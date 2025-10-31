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
}

private fun Goal.toDto(): GoalDto {
    return GoalDto(
        name = this.name,
        targetAmount = this.targetAmount,
        savedAmount = this.savedAmount,
        targetDate = this.targetDate,
        icon = this.icon,
        colorHex = this.colorHex
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
        colorHex = this.colorHex
    )
}
