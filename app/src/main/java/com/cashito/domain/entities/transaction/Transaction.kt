
package com.cashito.domain.entities.transaction

import com.cashito.domain.entities.category.Category
import java.util.Date

enum class TransactionType {
    INCOME, EXPENSE
}

data class Transaction(
    val id: String,
    val description: String,
    val amount: Double,
    val date: Date,
    val category: Category?,
    val type: TransactionType,
    val goalId: String? = null
)
