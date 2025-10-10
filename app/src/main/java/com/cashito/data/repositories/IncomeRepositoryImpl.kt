package com.cashito.data.repositories

import android.util.Log
import com.cashito.data.datasources.firebase.FirebaseTransactionDataSource
import com.cashito.data.dto.TransactionDto
import com.cashito.domain.entities.income.Income
import com.cashito.domain.repositories.income.IncomeRepository

class IncomeRepositoryImpl(
    private val dataSource: FirebaseTransactionDataSource
) : IncomeRepository {
    init {
        Log.d("FlowDebug", "REPositoryImpl Income: CLASS")
    }

    override suspend fun addIncome(income: Income) {
        val transactionDto = income.toTransactionDto()
        dataSource.addTransaction(transactionDto)
    }

    override suspend fun getIncomes(): List<Income> {
        return emptyList()
    }
}

private fun Income.toTransactionDto(): TransactionDto {
    Log.d("FlowDebug", "ToTransactionFun: Funcion")
    return TransactionDto(
        description = this.description,
        amount = this.amount,
        type = "ingreso",
        categoryId = this.category?.id ?: "",
        categoryName = this.category?.name ?: "Sin categoría",
        categoryIcon = this.category?.icon ?: ""
    )
}
