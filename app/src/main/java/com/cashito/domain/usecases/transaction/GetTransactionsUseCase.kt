// domain/usecases/transaction/GetTransactionsUseCase.kt
package com.cashito.domain.usecases.transaction

import com.cashito.domain.entities.transaction.Transaction
import com.cashito.domain.entities.transaction.TransactionType
import com.cashito.domain.repositories.expense.ExpenseRepository
import com.cashito.domain.repositories.income.IncomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.catch

class GetTransactionsUseCase(
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository
) {

    operator fun invoke(): Flow<Result<List<Transaction>>> =
        combine(
            incomeRepository.observeIncomes(),
            expenseRepository.observeExpenses()
        ) { incomes, expenses ->

            val incomeTransactions = incomes.map {
                Transaction(
                    id = it.id,
                    description = it.description,
                    amount = it.amount,
                    date = it.date,
                    category = it.category,
                    type = TransactionType.INCOME
                )
            }

            val expenseTransactions = expenses.map {
                Transaction(
                    id = it.id,
                    description = it.description,
                    amount = it.amount,
                    date = it.date,
                    category = it.category,
                    type = TransactionType.EXPENSE
                )
            }

            Result.success((incomeTransactions + expenseTransactions).sortedByDescending { it.date })
        }.catch { e ->
            emit(Result.failure(e))
        }
}

