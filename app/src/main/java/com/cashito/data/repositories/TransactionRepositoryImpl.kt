package com.cashito.data.repositories

import com.cashito.data.datasources.firebase.FirebaseTransactionDataSource
import com.cashito.data.dto.TransactionDto
import com.cashito.domain.entities.category.Category
import com.cashito.domain.entities.transaction.Transaction
import com.cashito.domain.entities.transaction.TransactionType
import com.cashito.domain.repositories.transaction.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val dataSource: FirebaseTransactionDataSource
) : TransactionRepository {

    override suspend fun getTransactionById(transactionId: String): Transaction? {
        val transactionDto = dataSource.getTransactionById(transactionId)
        return transactionDto?.toDomainTransaction()
    }

    /**
     * ARREGLADO: La lógica de actualización ahora es completa y robusta.
     * Convierte la entidad de dominio `Transaction` actualizada a un `TransactionDto`
     * y lo usa para reemplazar el documento antiguo en Firestore.
     */
    override suspend fun updateTransaction(transactionId: String, transaction: Transaction) {
        // Convertimos la entidad de dominio COMPLETA a un DTO.
        val transactionDto = transaction.toDto()
        // Llamamos a un método en el DataSource que debería usar `set` para reemplazar el documento.
        // (Asumiremos y luego verificaremos que el dataSource tiene un método `updateTransaction` que acepta un Dto)
        dataSource.updateTransaction(transactionId, transactionDto)
    }

    override suspend fun deleteTransaction(transactionId: String) {
        dataSource.deleteTransaction(transactionId)
    }

    override suspend fun getTransactionsForGoal(goalId: String): Flow<List<Transaction>> {
        return dataSource.observeTransactionsByGoal(goalId).map { dtoList ->
            dtoList.map { it.toDomainTransaction() }
        }
    }
}

/**
 * Convierte la entidad de dominio `Transaction` a `TransactionDto`.
 */
private fun Transaction.toDto(): TransactionDto {
    return TransactionDto(
        id = this.id,
        description = this.description,
        amount = this.amount,
        date = this.date,
        type = if (this.type == TransactionType.INCOME) "ingreso" else "gasto",
        categoryId = this.category?.id ?: "",
        categoryName = this.category?.name ?: "",
        categoryIcon = this.category?.icon ?: "",
        goalId = this.goalId
    )
}

/**
 * Convierte `TransactionDto` a la entidad de dominio `Transaction`.
 */
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
        type = if (this.type == "ingreso") TransactionType.INCOME else TransactionType.EXPENSE,
        goalId = this.goalId
    )
}
