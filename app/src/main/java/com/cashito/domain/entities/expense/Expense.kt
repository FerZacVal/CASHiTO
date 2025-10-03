package com.cashito.domain.entities.expense

import com.cashito.domain.entities.category.Category

data class Expense(
    val id: String,
    val title: String,
    val amount: Double,
    val category: Category?
)


