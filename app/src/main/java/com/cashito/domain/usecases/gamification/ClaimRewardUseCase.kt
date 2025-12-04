package com.cashito.domain.usecases.gamification

import com.cashito.domain.entities.gamification.Reward
import com.cashito.domain.repositories.gamification.GamificationRepository

class ClaimRewardUseCase(
    private val gamificationRepository: GamificationRepository
) {
    suspend operator fun invoke(challengeId: String): Reward {
        return gamificationRepository.claimReward(challengeId)
    }
}
