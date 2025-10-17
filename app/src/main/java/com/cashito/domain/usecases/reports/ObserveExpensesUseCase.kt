package com.cashito.domain.usecases.reports

import com.cashito.domain.entities.transaction.Transaction
import com.cashito.domain.entities.transaction.TransactionType
import com.cashito.domain.repositories.expense.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveExpenseReportUseCase(
    private val expenseRepository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<Transaction>> {
        // Escuchar en tiempo real solo gastos
        return expenseRepository.observeExpenses().map { expenses ->
            expenses.map {
                Transaction(
                    id = it.id,
                    description = it.description,
                    amount = it.amount,
                    date = it.date,
                    category = it.category,
                    type = TransactionType.EXPENSE
                )
            }
        }
    }
}
