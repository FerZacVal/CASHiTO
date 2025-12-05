package com.cashito.domain.entities.gamification

import java.util.Date

enum class RewardType {
    APR_BOOST,
    RETRY_CHANCE,
    NONE
}

data class Reward(
    val id: String,
    val type: RewardType,
    val value: Double, // Percentage for APR, 1 for retry
    val durationDays: Int, // Duration in days for APR
    val description: String,
    val earnedDate: Date = Date(),
    val isUsed: Boolean = false,
    val appliedToGoalId: String? = null
)
