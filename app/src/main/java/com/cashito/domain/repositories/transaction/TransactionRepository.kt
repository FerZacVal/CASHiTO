
package com.cashito.domain.repositories.transaction

import com.cashito.domain.entities.transaction.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun getTransactionById(transactionId: String): Transaction?
    suspend fun updateTransaction(transactionId: String, transaction: Transaction)
    suspend fun deleteTransaction(transactionId: String)
    suspend fun getTransactionsForGoal(goalId: String): Flow<List<Transaction>>
}
