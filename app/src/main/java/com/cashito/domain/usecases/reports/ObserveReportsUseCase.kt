package com.cashito.domain.usecases.reports

import com.cashito.domain.repositories.expense.ExpenseRepository
import com.cashito.domain.repositories.income.IncomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveReportsUseCase(
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository
) {
    operator fun invoke(): Flow<Triple<Float, Float, Float>> {
        return combine(
            incomeRepository.observeIncomes(),
            expenseRepository.observeExpenses()
        ) { incomes, expenses ->
            val totalIncome = incomes.sumOf { it.amount.toDouble() }.toFloat()
            val totalExpenses = expenses.sumOf { it.amount.toDouble() }.toFloat()
            val balance = totalIncome - totalExpenses
            Triple(totalIncome, totalExpenses, balance)
        }
    }
}
