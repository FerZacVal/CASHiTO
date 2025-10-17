package com.cashito.domain.usecases.transaction

import com.cashito.domain.repositories.transaction.TransactionRepository

class DeleteTransactionUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(transactionId: String) {
        repository.deleteTransaction(transactionId)
    }
}
