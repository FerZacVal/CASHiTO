package com.cashito.domain.usecases.expense

import com.cashito.domain.entities.expense.Expense
import com.cashito.domain.entities.transaction.Transaction
import com.cashito.domain.entities.transaction.TransactionType
import com.cashito.domain.repositories.expense.ExpenseRepository
import com.cashito.domain.repositories.goal.GoalRepository
import com.cashito.domain.usecases.balance.GetFinancialBreakdownUseCase
import kotlinx.coroutines.flow.first
import java.util.Date
import java.util.UUID

class AddExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
    private val getFinancialBreakdownUseCase: GetFinancialBreakdownUseCase,
    private val goalRepository: GoalRepository
) {
    /**
     * Intenta añadir un gasto. 
     * Si el gasto supera el saldo libre, lanza InsufficientFreeBalanceException.
     */
    suspend operator fun invoke(expense: Expense) {
        // 1. Obtener desglose financiero actual
        val breakdown = getFinancialBreakdownUseCase().first()
        
        // 2. Verificar si hay suficiente saldo libre
        if (expense.amount > breakdown.freeBalance) {
            val deficit = expense.amount - breakdown.freeBalance
            // Si el déficit es pequeño (ej. flotante), lo ignoramos. Pero aquí somos estrictos.
            if (deficit > 0.01) {
                throw InsufficientFreeBalanceException(deficit, breakdown.totalBalance)
            }
        }

        // 3. Si hay saldo, proceder normal
        expenseRepository.addExpense(expense)
    }

    /**
     * Método alternativo para cuando el usuario decide cubrir el déficit con una meta.
     * @param expense El gasto original completo.
     * @param goalId ID de la meta a sacrificar.
     * @param deficit El monto que se retirará de la meta.
     */
    suspend fun addExpenseWithWithdrawal(expense: Expense, goalId: String, deficit: Double) {
        // 1. Registrar el gasto completo
        expenseRepository.addExpense(expense)
        
        // 2. Crear la transacción de retiro para la meta
        // Nota: Esto debería ser atómico idealmente, pero por ahora lo hacemos secuencial.
        // El repositorio de Goal ya tiene withdrawFromGoal que es atómico para la meta+transacción.
        
        val withdrawalTransaction = Transaction(
            id = UUID.randomUUID().toString(), // Será sobrescrito por Firestore
            description = "Ajuste por gasto: ${expense.description}",
            amount = deficit, // El monto positivo a retirar
            date = Date(),
            type = TransactionType.EXPENSE,
            category = null, // No hay categoría asociada para el ajuste
            goalId = goalId
        )
        
        goalRepository.withdrawFromGoal(goalId, deficit, withdrawalTransaction)
    }
}

// Excepción para manejar el flujo de déficit
class InsufficientFreeBalanceException(
    val deficit: Double,
    val totalBalance: Double
) : Exception("Saldo libre insuficiente. Faltan: $deficit")
