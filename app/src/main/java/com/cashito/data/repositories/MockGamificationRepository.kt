package com.cashito.data.repositories

import com.cashito.domain.entities.gamification.Reward
import com.cashito.domain.entities.gamification.RewardType
import com.cashito.domain.entities.gamification.WeeklyChallenge
import com.cashito.domain.repositories.gamification.GamificationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date
import java.util.UUID

class MockGamificationRepository : GamificationRepository {

    private val _currentChallenge = MutableStateFlow(createWeeklyChallenge())
    private val _rewards = MutableStateFlow<List<Reward>>(emptyList())

    private fun createWeeklyChallenge(): WeeklyChallenge {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startDate = calendar.time

        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val endDate = calendar.time
        
        // Mock: if it's not Monday, we still want a challenge for testing.
        // Real implementation would check current date vs start/end.

        return WeeklyChallenge(
            id = "week_${calendar.get(Calendar.WEEK_OF_YEAR)}",
            title = "Reto Semanal",
            description = "Ingresa S/ 200 esta semana para ganar recompensas.",
            targetAmount = 200.0,
            currentAmount = 0.0, // This should be synced with income
            startDate = startDate,
            endDate = endDate
        )
    }

    override fun getCurrentWeeklyChallenge(): Flow<WeeklyChallenge?> {
        return _currentChallenge
    }

    override suspend fun updateChallengeProgress(amount: Double) {
        val current = _currentChallenge.value
        if (current != null && !current.isCompleted) {
            val newAmount = current.currentAmount + amount
            val isCompleted = newAmount >= current.targetAmount
            _currentChallenge.value = current.copy(
                currentAmount = newAmount,
                isCompleted = isCompleted
            )
        }
    }

    override suspend fun claimReward(challengeId: String): Reward {
        // Simulate lottery
        delay(1000) // Simulating network delay
        val random = Math.random()
        val reward = when {
            random < 0.3 -> Reward( // 30% chance
                id = UUID.randomUUID().toString(),
                type = RewardType.APR_BOOST,
                value = 20.0,
                durationDays = 7,
                description = "APR 20% por 7 días"
            )
            random < 0.4 -> Reward( // 10% chance
                id = UUID.randomUUID().toString(),
                type = RewardType.APR_BOOST,
                value = 350.0,
                durationDays = 3,
                description = "APR 350% por 3 días"
            )
            random < 0.6 -> Reward( // 20% chance
                id = UUID.randomUUID().toString(),
                type = RewardType.APR_BOOST,
                value = 5.0,
                durationDays = 10,
                description = "APR 5% por 10 días"
            )
            random < 0.7 -> Reward( // 10% chance
                id = UUID.randomUUID().toString(),
                type = RewardType.RETRY_CHANCE,
                value = 1.0,
                durationDays = 0,
                description = "Tira otra vez"
            )
            else -> Reward( // 30% chance
                id = UUID.randomUUID().toString(),
                type = RewardType.NONE,
                value = 0.0,
                durationDays = 0,
                description = "¡Sigue intentando!"
            )
        }

        if (reward.type != RewardType.NONE) {
            val currentList = _rewards.value.toMutableList()
            currentList.add(reward)
            _rewards.value = currentList
        }
        
        // Mark challenge as reward claimed if it's not a retry
        if (reward.type != RewardType.RETRY_CHANCE) {
             val current = _currentChallenge.value
             if (current != null) {
                 _currentChallenge.value = current.copy(isRewardClaimed = true)
             }
        }

        return reward
    }

    override fun getUserRewards(): Flow<List<Reward>> {
        return _rewards.map { list -> list.filter { !it.isUsed && it.type != RewardType.NONE } }
    }

    override suspend fun useReward(rewardId: String, goalId: String?) {
        val currentList = _rewards.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == rewardId }
        if (index != -1) {
            val reward = currentList[index]
            currentList[index] = reward.copy(isUsed = true, appliedToGoalId = goalId)
            _rewards.value = currentList
        }
    }
}
