package com.cashito.domain.usecases.balance

import com.cashito.domain.repositories.expense.ExpenseRepository
import com.cashito.domain.repositories.income.IncomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext



class GetBalanceUseCase(
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(): Double = withContext(Dispatchers.IO) {
        // Obtenemos ingresos y gastos en paralelo para mayor eficiencia
        val totalIncomeDeferred = async { incomeRepository.getIncomes().sumOf { it.amount } }
        val totalExpenseDeferred = async { expenseRepository.getExpenses().sumOf { it.amount } }

        val totalIncome = totalIncomeDeferred.await()
        val totalExpense = totalExpenseDeferred.await()

        return@withContext totalIncome - totalExpense
    }

}
