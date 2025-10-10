package com.cashito.domain.repositories.income

import android.util.Log
import com.cashito.domain.entities.income.Income

interface IncomeRepository {

    suspend fun addIncome(income: Income)
    suspend fun getIncomes(): List<Income>
}