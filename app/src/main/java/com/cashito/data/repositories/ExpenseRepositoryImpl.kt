package com.cashito.data.repositories

import com.cashito.data.datasources.firebase.FirebaseTransactionDataSource
import com.cashito.data.dto.TransactionDto
import com.cashito.domain.entities.category.Category
import com.cashito.domain.entities.expense.Expense
import com.cashito.domain.repositories.expense.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepositoryImpl(
    private val dataSource: FirebaseTransactionDataSource
) : ExpenseRepository {

    override suspend fun addExpense(expense: Expense) {
        val transactionDto = expense.toTransactionDto()
        dataSource.addTransaction(transactionDto)
    }

    override fun observeExpenses(): Flow<List<Expense>> {
        return dataSource.observeTransactions()
            .map { list ->
                list.filter { it.type == "gasto" }
                    .map { it.toExpense() }
            }
    }
}

private fun Expense.toTransactionDto(): TransactionDto {
    return TransactionDto(
        description = this.description,
        amount = this.amount,
        type = "gasto",
        categoryId = this.category?.id ?: "",
        categoryName = this.category?.name ?: "Sin categoría",
        categoryIcon = this.category?.icon ?: ""
    )
}

private fun TransactionDto.toExpense(): Expense {
    return Expense(
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
