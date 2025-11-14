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

    override suspend fun getIncomes(): List<Income> {
        return dataSource.getTransactions()
            .filter { it.type == "ingreso" }
            .map { it.toIncome() }
    }
}

/**
 * Convierte la entidad de dominio `Income` a un `TransactionDto` para ser guardado en Firestore.
 * Este es un punto CRÍTICO donde los datos de dominio se traducen a datos de la capa de datos.
 */
private fun Income.toTransactionDto(): TransactionDto {
    Log.d("FlowDebug", "ToTransactionFun: Mapeando Income a TransactionDto. GoalID: ${this.goalId}")
    return TransactionDto(
        description = this.description,
        amount = this.amount,
        type = "ingreso",
        categoryId = this.category?.id ?: "",
        categoryName = this.category?.name ?: "Sin categoría",
        categoryIcon = this.category?.icon ?: "",
        goalId = this.goalId // <-- ¡ARREGLADO! Ahora sí pasamos el goalId.
    )
}

/**
 * Convierte un `TransactionDto` (leído de Firestore) a la entidad de dominio `Income`.
 */
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
        ),
        goalId = this.goalId // <-- AÑADIDO por consistencia.
    )
}
