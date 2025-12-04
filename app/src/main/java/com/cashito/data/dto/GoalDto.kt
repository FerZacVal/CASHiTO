package com.cashito.data.dto

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Data Transfer Object (DTO) que representa la estructura de un documento
 * en la subcolección 'Metas' de Firestore.
 */
data class GoalDto(
    @get:Exclude var id: String = "",
    var userId: String = "",
    val name: String = "",
    val targetAmount: Double = 0.0,
    val savedAmount: Double = 0.0,
    val targetDate: Date? = null,
    @ServerTimestamp val creationDate: Date? = null, // Anotado para que Firestore ponga la fecha del servidor
    val icon: String = "",
    val colorHex: String = "",
    // Nuevos campos para gamificación
    val activeBoostId: String? = null,
    val activeBoostApr: Double? = null,
    val boostExpiryDate: Date? = null,
    val activeBoostProfit: Double? = null
)
