package com.cashito.domain.usecases.gamification

import com.cashito.domain.repositories.gamification.GamificationRepository
import com.cashito.domain.repositories.goal.GoalRepository
import com.cashito.domain.entities.gamification.RewardType
import kotlinx.coroutines.flow.first
import java.util.Calendar

class UseRewardUseCase(
    private val gamificationRepository: GamificationRepository,
    private val goalRepository: GoalRepository
) {
    suspend operator fun invoke(rewardId: String, goalId: String?) {
        // 1. Marcar el reward como usado en el sistema de gamificación
        gamificationRepository.useReward(rewardId, goalId)

        // 2. Si es un boost de APR y se aplicó a una meta, actualizar la meta
        if (goalId != null) {
            val rewards = gamificationRepository.getUserRewards().first()
            val reward = rewards.find { it.id == rewardId }
            
            if (reward != null && reward.type == RewardType.APR_BOOST) {
                val goal = goalRepository.getGoalById(goalId).first()
                if (goal != null) {
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_YEAR, reward.durationDays)
                    val expiryDate = calendar.time
                    
                    // Calcular el beneficio exacto (Snapshot)
                    val apr = reward.value
                    val dailyRate = apr / 365.0 / 100.0
                    val duration = reward.durationDays
                    val snapshotProfit = goal.savedAmount * dailyRate * duration
                    
                    val updatedGoal = goal.copy(
                        activeBoostId = reward.id,
                        activeBoostApr = reward.value,
                        boostExpiryDate = expiryDate,
                        activeBoostProfit = snapshotProfit // Guardamos el valor fijo
                    )
                    goalRepository.updateGoal(updatedGoal)
                }
            }
        }
    }
}
