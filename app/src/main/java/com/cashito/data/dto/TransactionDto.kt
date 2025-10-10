package com.cashito.data.dto

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Data Transfer Object (DTO) que representa la estructura de un documento
 * en la colección 'Transacciones' de Firestore.
 */
data class TransactionDto(
    var userId: String = "", // Este campo será llenado por el DataSource antes de guardar
    val description: String = "",
    val amount: Double = 0.0,
    @ServerTimestamp
    val date: Date? = null,
    val type: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val categoryIcon: String = ""
)
