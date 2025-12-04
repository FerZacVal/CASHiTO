package com.cashito.data.repositories

import com.cashito.data.datasources.firebase.FirebaseGamificationDataSource
import com.cashito.data.dto.RewardDto
import com.cashito.data.dto.WeeklyChallengeDto
import com.cashito.domain.entities.gamification.Reward
import com.cashito.domain.entities.gamification.RewardType
import com.cashito.domain.entities.gamification.WeeklyChallenge
import com.cashito.domain.repositories.gamification.GamificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date
import java.util.UUID

class GamificationRepositoryImpl(
    private val dataSource: FirebaseGamificationDataSource
) : GamificationRepository {

    private fun getCurrentWeekId(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val week = calendar.get(Calendar.WEEK_OF_YEAR)
        return "${year}_W$week"
    }

    private fun getStartAndEndDate(weekId: String): Pair<Date, Date> {
        // Parse weekId simply or just use current week dates for now.
        // Ideally we should parse the weekId to get accurate dates if we are viewing history,
        // but for "Current" challenge, using current calendar is fine.
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startDate = calendar.time

        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val endDate = calendar.time
        return Pair(startDate, endDate)
    }

    override fun getCurrentWeeklyChallenge(): Flow<WeeklyChallenge?> = flow {
        val weekId = getCurrentWeekId()
        // We fetch the config once per stream collection start.
        val targetAmount = try {
            dataSource.getGlobalConfigWeeklyTarget()
        } catch (e: Exception) {
            200.0
        }

        dataSource.observeWeeklyChallenge(weekId).collect { dto ->
            if (dto != null) {
                emit(dto.toDomain())
            } else {
                // Create in-memory default challenge (Lazy creation on read)
                val (startDate, endDate) = getStartAndEndDate(weekId)
                emit(
                    WeeklyChallenge(
                        id = weekId,
                        title = "Reto Semanal",
                        description = "Ingresa S/ $targetAmount esta semana para ganar recompensas.",
                        targetAmount = targetAmount,
                        currentAmount = 0.0,
                        startDate = startDate,
                        endDate = endDate,
                        isCompleted = false,
                        isRewardClaimed = false
                    )
                )
            }
        }
    }

    override suspend fun updateChallengeProgress(amount: Double) {
        val targetAmount = dataSource.getGlobalConfigWeeklyTarget()
        dataSource.createOrUpdateChallenge(getCurrentWeekId(), amount, targetAmount)
    }

    override suspend fun claimReward(challengeId: String): Reward {
        // Generate random reward
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
        
        // If it's NONE or RETRY, we might handle it differently, but for the DB we persist the reward.
        // Even if it is NONE, we might want to record that they claimed the reward for the week.
        // However, the Mock implementation just returned it.
        // The plan says: "Actualizamos el reto: isRewardClaimed = true. Creamos el nuevo documento en users/{id}/rewards/."
        
        // If the reward is NONE, we still mark the challenge as claimed? Usually yes.
        // If it is RETRY, maybe not? The Mock said:
        // "Mark challenge as reward claimed if it's not a retry"
        
        if (reward.type != RewardType.RETRY_CHANCE) {
             dataSource.claimReward(challengeId, reward.toDto())
        }
        
        return reward
    }

    override fun getUserRewards(): Flow<List<Reward>> {
        return dataSource.observeUserRewards().map { list ->
            list.map { it.toDomain() }
                .filter { !it.isUsed && it.type != RewardType.NONE }
        }
    }

    override suspend fun useReward(rewardId: String, goalId: String?) {
        dataSource.markRewardAsUsed(rewardId, goalId)
    }

    // --- Mappers ---

    private fun WeeklyChallengeDto.toDomain(): WeeklyChallenge {
        val (startDate, endDate) = getStartAndEndDate(this.weekId)
        return WeeklyChallenge(
            id = this.weekId,
            title = "Reto Semanal",
            description = "Ingresa S/ ${this.targetAmount} esta semana para ganar recompensas.",
            targetAmount = this.targetAmount,
            currentAmount = this.currentAmount,
            startDate = startDate,
            endDate = endDate,
            isCompleted = this.isCompleted,
            isRewardClaimed = this.isRewardClaimed
        )
    }

    private fun RewardDto.toDomain(): Reward {
        return Reward(
            id = this.id,
            type = try { RewardType.valueOf(this.type) } catch (e: Exception) { RewardType.NONE },
            value = this.value,
            durationDays = this.durationDays,
            description = this.description,
            earnedDate = this.earnedDate?.toDate() ?: Date(),
            isUsed = this.isUsed,
            appliedToGoalId = this.appliedToGoalId
        )
    }

    private fun Reward.toDto(): RewardDto {
        return RewardDto(
            id = this.id,
            type = this.type.name,
            value = this.value,
            durationDays = this.durationDays,
            description = this.description,
            earnedDate = null, // Server timestamp will fill this
            isUsed = this.isUsed,
            appliedToGoalId = this.appliedToGoalId,
            expiryDate = null // Not using expiry yet
        )
    }
}
