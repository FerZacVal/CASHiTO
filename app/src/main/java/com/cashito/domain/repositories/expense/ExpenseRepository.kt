package com.cashito.domain.repositories.expense

import com.cashito.domain.entities.expense.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {

    suspend fun addExpense(expense: Expense)

    // ⚡ Nuevo método para observar gastos en tiempo real
    fun observeExpenses(): Flow<List<Expense>>
    suspend fun getExpenses(): List<Expense>

}