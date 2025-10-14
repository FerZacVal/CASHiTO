package com.cashito.data.repositories

import android.util.Log
import com.cashito.data.datasources.firebase.FirebaseTransactionDataSource
import com.cashito.data.dto.TransactionDto
import com.cashito.domain.entities.category.Category
import com.cashito.domain.entities.income.Income
import com.cashito.domain.repositories.income.IncomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    override fun observeIncomes(): Flow<List<Income>> {
        return dataSource.observeTransactions()
            .map { list ->
                list.filter { it.type == "ingreso" }
                    .map { it.toIncome() }
            }
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

private fun TransactionDto.toIncome(): Income {
    return Income(
        id = this.id,
        description = this.description,
        amount = this.amount,
        date = this.date ?: java.util.Date(),
        category = Category(
            id = this.categoryId,
            name = this.categoryName,
            icon = this.categoryIcon,
            // El color no se guarda en el DTO, se puede obtener de una fuente de categorías si es necesario
            color = ""
        )
    )
}
