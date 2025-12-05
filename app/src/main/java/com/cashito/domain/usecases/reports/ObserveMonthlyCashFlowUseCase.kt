package com.cashito.domain.usecases.reports

import com.cashito.domain.entities.transaction.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// 1. Modelo de datos exclusivo para este reporte (UI Model simplificado)
data class MonthlyCashFlow(
    val monthLabel: String,     // Ej: "Diciembre 2023"
    val year: Int,              // Para ordenar
    val monthIndex: Int,        // Para ordenar
    val totalIncome: Double,
    val totalExpense: Double,
    val netBalance: Double
)

// 2. El UseCase
class ObserveMonthlyCashFlowUseCase(
    private val observeExpenseReportUseCase: ObserveExpenseReportUseCase,
    private val observeIncomeReportUseCase: ObserveIncomeReportUseCase
) {
    operator fun invoke(): Flow<List<MonthlyCashFlow>> {
        // Combinamos los dos flujos (Gastos e Ingresos)
        return combine(
            observeExpenseReportUseCase(),
            observeIncomeReportUseCase()
        ) { expenses, incomes ->

            // Unimos ambas listas
            val allTransactions = expenses + incomes

            // Formateador para agrupar (Ej: "diciembre 2023")
            val dateFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
            val calendar = Calendar.getInstance()

            // Agrupar transacciones por la etiqueta de fecha
            val groupedMap = allTransactions.groupBy { transaction ->
                dateFormat.format(transaction.date)
            }

            // Transformar el mapa agrupado en nuestra lista de MonthlyCashFlow
            groupedMap.map { (dateLabel, transactions) ->
                // Obtenemos una fecha de referencia para sacar año/mes numérico
                val refDate = transactions.first().date
                calendar.time = refDate

                val incomeSum = transactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }

                val expenseSum = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }

                MonthlyCashFlow(
                    monthLabel = dateLabel.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                    year = calendar.get(Calendar.YEAR),
                    monthIndex = calendar.get(Calendar.MONTH),
                    totalIncome = incomeSum,
                    totalExpense = expenseSum,
                    netBalance = incomeSum - expenseSum
                )
            }.sortedWith(
                // Ordenar: Primero por Año descendente, luego por Mes descendente
                compareByDescending<MonthlyCashFlow> { it.year }.thenByDescending { it.monthIndex }
            )
        }
    }
}