package com.cashito.data.dto

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Data Transfer Object (DTO) que representa la estructura de un documento
 * en la colección 'Transacciones' de Firestore.
 */
data class TransactionDto(
    @get:Exclude var id: String = "", // ID del documento de Firestore. Se excluye de la serialización.
    var userId: String = "", // ID del usuario al que pertenece la transacción
    val description: String = "",
    val amount: Double = 0.0,
    @ServerTimestamp
    val date: Date? = null,
    val type: String = "", // "ingreso" o "gasto"
    val categoryId: String = "",
    val categoryName: String = "",
    val categoryIcon: String = ""
)
