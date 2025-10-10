package com.cashito.domain.usecases.income

import android.util.Log
import com.cashito.domain.entities.income.Income
import com.cashito.domain.repositories.income.IncomeRepository

class AddIncomeUseCase(
    private val repository: IncomeRepository
) {
    init {
        Log.d("FlowDebug", "UseCase Add Income: CLASS")
    }
    suspend operator fun invoke(income: Income) {
        repository.addIncome(income)
    }
}
