package com.cashito.domain.usecases.gamification

import com.cashito.domain.entities.gamification.WeeklyChallenge
import com.cashito.domain.repositories.gamification.GamificationRepository
import kotlinx.coroutines.flow.Flow

class GetWeeklyChallengeUseCase(
    private val gamificationRepository: GamificationRepository
) {
    operator fun invoke(): Flow<WeeklyChallenge?> {
        return gamificationRepository.getCurrentWeeklyChallenge()
    }
}
