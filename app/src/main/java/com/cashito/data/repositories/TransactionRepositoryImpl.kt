package com.cashito.data.repositories

import com.cashito.data.datasources.firebase.FirebaseTransactionDataSource
import com.cashito.data.dto.TransactionDto
import com.cashito.domain.entities.category.Category
import com.cashito.domain.entities.transaction.Transaction
import com.cashito.domain.entities.transaction.TransactionType
import com.cashito.domain.repositories.transaction.TransactionRepository

class TransactionRepositoryImpl(
    private val dataSource: FirebaseTransactionDataSource
) : TransactionRepository {

    override suspend fun getTransactionById(transactionId: String): Transaction? {
        val transactionDto = dataSource.getTransactionById(transactionId)
        return transactionDto?.toDomainTransaction()
    }

    override suspend fun updateTransaction(transactionId: String, transaction: Transaction) {
        val updatedData = hashMapOf(
            "description" to transaction.description,
            "amount" to transaction.amount,
            "categoryId" to (transaction.category?.id ?: ""),
            "categoryName" to (transaction.category?.name ?: ""),
            "categoryIcon" to (transaction.category?.icon ?: "")
        )
        dataSource.updateTransaction(transactionId, updatedData)
    }

    override suspend fun deleteTransaction(transactionId: String) {
        dataSource.deleteTransaction(transactionId)
    }
}

// Mapper function to convert DTO to Domain entity
private fun TransactionDto.toDomainTransaction(): Transaction {
    return Transaction(
        id = this.id,
        description = this.description,
        amount = this.amount,
        date = this.date ?: java.util.Date(),
        category = Category(
            id = this.categoryId,
            name = this.categoryName,
            icon = this.categoryIcon,
            color = ""
        ),
        type = if (this.type == "ingreso") TransactionType.INCOME else TransactionType.EXPENSE
    )
}
