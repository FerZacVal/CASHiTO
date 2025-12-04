package com.cashito.domain.entities.gamification

import java.util.Date

data class WeeklyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val startDate: Date,
    val endDate: Date,
    val isCompleted: Boolean = false,
    val isRewardClaimed: Boolean = false
)
