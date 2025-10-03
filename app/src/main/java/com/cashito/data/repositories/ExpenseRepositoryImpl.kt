package com.cashito.data.repositories

import com.cashito.domain.entities.expense.Expense
import com.cashito.domain.repositories.ExpenseRepository

class ExpenseRepositoryImpl : ExpenseRepository {
    override suspend fun addExpense(expense: Expense) {
        // No-op placeholder
    }

    override suspend fun getExpenses(): List<Expense> {
        return emptyList()
    }
}


