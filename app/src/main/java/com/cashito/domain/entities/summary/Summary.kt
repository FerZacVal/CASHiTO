package com.cashito.domain.entities.summary

/**
 * Representa un resumen de los totales para un período de tiempo específico.
 * En la base de datos (Firestore), esto corresponde a un documento dentro de la subcolección `Resumen`.
 */
data class Summary(
    /**
     * El total de gastos para el período.
     * Mapea desde el campo `totalGastos` en el documento de Firestore.
     */
    val totalExpenses: Double,

    /**
     * El total de ingresos para el período.
     * Mapea desde el campo `totalIngresos` en el documento de Firestore.
     */
    val totalIncome: Double,

    /**
     * El balance final (ingresos - gastos).
     * Mapea desde el campo `balance` en el documento de Firestore.
     */
    val balance: Double,

    /**
     * El período de tiempo que cubre este resumen (ej. "mensual", "semanal").
     * Mapea desde el campo `periodo` en el documento de Firestore.
     */
    val period: String
)
