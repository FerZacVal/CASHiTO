package com.cashito.domain.entities.goal

import java.util.Date

/**
 * Representa una meta de ahorro dentro del dominio de la aplicación.
 */
data class Goal(
    val id: String,
    val userId: String,
    val name: String,
    val targetAmount: Double,
    val savedAmount: Double,
    val targetDate: Date?,
    val creationDate: Date,
    val icon: String,
    val colorHex: String,
    // Nuevos campos para gamificación
    val activeBoostId: String? = null, // ID del reward activo
    val activeBoostApr: Double? = null, // Valor del APR activo
    val boostExpiryDate: Date? = null, // Fecha de vencimiento del boost
    val activeBoostProfit: Double? = null // Ganancia calculada fija (Snapshot)
)
