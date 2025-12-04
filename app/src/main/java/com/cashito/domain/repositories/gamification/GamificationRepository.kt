package com.cashito.domain.repositories.gamification

import com.cashito.domain.entities.gamification.Reward
import com.cashito.domain.entities.gamification.WeeklyChallenge
import kotlinx.coroutines.flow.Flow

interface GamificationRepository {
    fun getCurrentWeeklyChallenge(): Flow<WeeklyChallenge?>
    suspend fun updateChallengeProgress(amount: Double)
    suspend fun claimReward(challengeId: String): Reward
    fun getUserRewards(): Flow<List<Reward>>
    suspend fun useReward(rewardId: String, goalId: String?)
}
