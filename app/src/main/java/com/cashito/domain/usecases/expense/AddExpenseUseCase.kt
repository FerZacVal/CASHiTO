package com.cashito.domain.usecases.expense

import com.cashito.domain.entities.expense.Expense
import com.cashito.domain.repositories.ExpenseRepository

class AddExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense) {
        repository.addExpense(expense)
    }
}


