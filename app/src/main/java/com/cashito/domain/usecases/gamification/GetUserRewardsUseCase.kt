package com.cashito.domain.usecases.gamification

import com.cashito.domain.entities.gamification.Reward
import com.cashito.domain.repositories.gamification.GamificationRepository
import kotlinx.coroutines.flow.Flow

class GetUserRewardsUseCase(
    private val gamificationRepository: GamificationRepository
) {
    operator fun invoke(): Flow<List<Reward>> {
        return gamificationRepository.getUserRewards()
    }
}
