package com.cashito.domain.entities.expense

import com.cashito.domain.entities.category.Category
import java.util.Date

data class Expense(
    val id: String,
    /**
     * Una descripción del gasto.
     * Mapea desde el campo `descripcion` en el documento de Firestore.
     */
    val description: String,
    val amount: Double,
    val date: Date,
    val category: Category?
)
