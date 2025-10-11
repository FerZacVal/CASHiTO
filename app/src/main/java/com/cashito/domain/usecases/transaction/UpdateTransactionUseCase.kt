package com.cashito.domain.usecases.transaction

import com.cashito.domain.entities.transaction.Transaction
import com.cashito.domain.repositories.transaction.TransactionRepository

class UpdateTransactionUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(transactionId: String, updatedTransaction: Transaction) {
        repository.updateTransaction(transactionId, updatedTransaction)
    }
}
