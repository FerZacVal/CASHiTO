package com.cashito.domain.entities.income

import com.cashito.domain.entities.category.Category
import java.util.Date

/**
 * Representa una única transacción de ingreso.
 * En la base de datos (Firestore), esto corresponde a un documento dentro de la subcolección `Transacciones`,
 * específicamente aquellos donde el campo `tipo` es "ingreso".
 */
data class Income(
    /**
     * El ID único del ingreso.
     * Mapea desde el ID del documento de Firestore en la subcolección `Transacciones`.
     */
    val id: String,

    /**
     * Una descripción del ingreso (ej. "Salario Enero", "Venta de item").
     * Mapea desde el campo `descripcion` en el documento de Firestore.
     */
    val description: String,

    /**
     * El monto del ingreso.
     * Mapea desde el campo `monto` en el documento de Firestore.
     */
    val amount: Double,

    /**
     * La fecha en que se recibió el ingreso.
     * Mapea desde el campo `fecha` (de tipo Timestamp) en el documento de Firestore.
     */
    val date: Date,

    /**
     * La categoría a la que pertenece el ingreso (opcional).
     * Mapea desde el campo `categoria` (un String) en el documento de Firestore.
     * Aunque los ingresos no siempre se categorizan, mantener este campo asegura la simetría
     * con la entidad `Expense` y simplifica el mapeo desde la entidad `Transaccion` de la capa de datos.
     */
    val category: Category?,

    /**
     * El ID de la meta de ahorro a la que se asocia este ingreso. Es anulable (nullable)
     * porque no todos los ingresos son un aporte a una meta. Se rellena cuando el ingreso
     * se crea desde la pantalla de detalle de una meta.
     */
    val goalId: String? = null
)
