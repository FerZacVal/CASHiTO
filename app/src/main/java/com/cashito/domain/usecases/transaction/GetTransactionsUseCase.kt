package com.cashito.domain.usecases.transaction

import com.cashito.domain.entities.transaction.Transaction
import com.cashito.domain.entities.transaction.TransactionType
import com.cashito.domain.repositories.expense.ExpenseRepository
import com.cashito.domain.repositories.income.IncomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class GetTransactionsUseCase(
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository
) {

    suspend operator fun invoke(): Result<List<Transaction>> = withContext(Dispatchers.IO) {
        try {
            // Obtener ingresos y gastos en paralelo para mayor eficiencia
            val incomesDeferred = async { incomeRepository.getIncomes() }
            val expensesDeferred = async { expenseRepository.getExpenses() }

            val incomes = incomesDeferred.await()
            val expenses = expensesDeferred.await()

            // Mapear a la entidad de dominio Transaction
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

            // Combinar, ordenar por fecha descendente y devolver
            val allTransactions = (incomeTransactions + expenseTransactions).sortedByDescending { it.date }
            Result.success(allTransactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
