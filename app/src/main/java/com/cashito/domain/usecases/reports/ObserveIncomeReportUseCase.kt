package com.cashito.domain.usecases.reports

import com.cashito.domain.entities.transaction.Transaction
import com.cashito.domain.entities.transaction.TransactionType
import com.cashito.domain.repositories.income.IncomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveIncomeReportUseCase(
    private val incomeRepository: IncomeRepository
) {
    operator fun invoke(): Flow<List<Transaction>> {
        // Escuchar en tiempo real solo ingresos
        return incomeRepository.observeIncomes().map { incomes ->
            incomes.map {
                Transaction(
                    id = it.id,
                    description = it.description,
                    amount = it.amount,
                    date = it.date,
                    category = it.category,
                    type = TransactionType.INCOME
                )
            }
        }
    }
}
