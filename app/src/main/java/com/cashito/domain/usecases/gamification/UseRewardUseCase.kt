package com.cashito.domain.usecases.gamification

import com.cashito.domain.repositories.gamification.GamificationRepository

class UseRewardUseCase(
    private val gamificationRepository: GamificationRepository
) {
    suspend operator fun invoke(rewardId: String, goalId: String?) {
        gamificationRepository.useReward(rewardId, goalId)
    }
}
