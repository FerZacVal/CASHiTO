package com.cashito.domain.repositories.income

import android.util.Log
import com.cashito.domain.entities.income.Income
import kotlinx.coroutines.flow.Flow

interface IncomeRepository {

    suspend fun addIncome(income: Income)
    fun observeIncomes(): Flow<List<Income>>
    suspend fun getIncomes(): List<Income>
}


