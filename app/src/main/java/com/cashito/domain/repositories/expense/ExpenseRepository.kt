package com.cashito.domain.repositories.expense

import com.cashito.domain.entities.expense.Expense

interface ExpenseRepository {
    suspend fun addExpense(expense: Expense)
    suspend fun getExpenses(): List<Expense>
}