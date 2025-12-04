package com.cashito.data.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class RewardDto(
    var id: String = "",
    val type: String = "", // Enum as String
    val value: Double = 0.0,
    val durationDays: Int = 0,
    val description: String = "",
    @ServerTimestamp
    val earnedDate: Timestamp? = null,
    @field:JvmField
    val isUsed: Boolean = false,
    val appliedToGoalId: String? = null,
    val expiryDate: Timestamp? = null
)
