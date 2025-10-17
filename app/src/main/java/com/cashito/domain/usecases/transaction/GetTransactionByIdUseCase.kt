package com.cashito.domain.usecases.transaction

import com.cashito.domain.entities.transaction.Transaction
import com.cashito.domain.repositories.transaction.TransactionRepository

class GetTransactionByIdUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(transactionId: String): Transaction? {
        return repository.getTransactionById(transactionId)
    }
}
