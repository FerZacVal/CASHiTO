package com.cashito.domain.usecases.gamification

import com.cashito.domain.repositories.gamification.GamificationRepository

class UpdateChallengeProgressUseCase(
    private val gamificationRepository: GamificationRepository
) {
    suspend operator fun invoke(amount: Double) {
        gamificationRepository.updateChallengeProgress(amount)
    }
}
