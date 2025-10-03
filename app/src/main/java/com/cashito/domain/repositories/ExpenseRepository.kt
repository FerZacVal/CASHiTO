package com.cashito.domain.repositories

import com.cashito.domain.entities.expense.Expense

interface ExpenseRepository {
    suspend fun addExpense(expense: Expense)
    suspend fun getExpenses(): List<Expense>
}


