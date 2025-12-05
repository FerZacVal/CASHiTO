package com.cashito.data.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class WeeklyChallengeDto(
    var weekId: String = "",
    val currentAmount: Double = 0.0,
    val targetAmount: Double = 200.0, // Default fallback, should be set from config
    @field:JvmField
    val isCompleted: Boolean = false,
    @field:JvmField
    val isRewardClaimed: Boolean = false,
    @ServerTimestamp
    val lastUpdated: Timestamp? = null
)
