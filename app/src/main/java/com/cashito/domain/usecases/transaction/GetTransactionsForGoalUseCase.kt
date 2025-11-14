
package com.cashito.domain.usecases.transaction

import com.cashito.domain.entities.transaction.Transaction
import com.cashito.domain.repositories.transaction.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetTransactionsForGoalUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(goalId: String): Flow<List<Transaction>> {
        return repository.getTransactionsForGoal(goalId)
    }
}
