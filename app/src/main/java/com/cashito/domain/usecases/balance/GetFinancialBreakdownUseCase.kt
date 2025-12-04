package com.cashito.domain.usecases.balance

import com.cashito.domain.entities.balance.FinancialBreakdown
import com.cashito.domain.repositories.expense.ExpenseRepository
import com.cashito.domain.repositories.goal.GoalRepository
import com.cashito.domain.repositories.income.IncomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetFinancialBreakdownUseCase(
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository,
    private val goalRepository: GoalRepository
) {
    operator fun invoke(): Flow<FinancialBreakdown> {
        return combine(
            incomeRepository.observeIncomes(),
            expenseRepository.observeExpenses(),
            goalRepository.getGoals()
        ) { incomes, expenses, goals ->
            val totalIncome = incomes.sumOf { it.amount }
            val totalExpense = expenses.sumOf { it.amount }
            val totalBalance = totalIncome - totalExpense
            
            val goalsBalance = goals.sumOf { it.savedAmount }
            val freeBalance = totalBalance - goalsBalance

            FinancialBreakdown(
                totalBalance = totalBalance,
                goalsBalance = goalsBalance,
                freeBalance = freeBalance
            )
        }
    }
}
