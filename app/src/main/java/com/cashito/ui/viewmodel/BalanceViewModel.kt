package com.cashito.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.transaction.TransactionType
import com.cashito.domain.usecases.transaction.GetTransactionsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Locale

data class BalanceEntry(val label: String, val balance: Float)
data class BalanceSummary(val currentBalance: Float, val change: Float, val periodLabel: String)
data class BalanceUiState(
    val selectedPeriod: String = "Semanal",
    val balanceData: List<BalanceEntry> = emptyList(),
    val summary: BalanceSummary = BalanceSummary(0f, 0f, ""),
    val isLoading: Boolean = true,
    val error: String? = null
)

class BalanceViewModel(
    getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val selectedPeriod = MutableStateFlow("Semanal")

    val uiState: StateFlow<BalanceUiState> = combine(
        getTransactionsUseCase(),
        selectedPeriod
    ) { result, period ->
        result.fold(
            onSuccess = { transactions ->
                val grouped = when (period) {
                    "Diario" -> transactions.groupBy { it.date.toInstant().atZone(ZoneId.systemDefault()).dayOfMonth }
                    "Semanal" -> transactions.groupBy {
                        it.date.toInstant().atZone(ZoneId.systemDefault())
                            .get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
                    }
                    "Mensual" -> transactions.groupBy { it.date.toInstant().atZone(ZoneId.systemDefault()).monthValue }
                    else -> emptyMap()
                }

                val entries = grouped.map { (key, list) ->
                    BalanceEntry(
                        label = when (period) {
                            "Diario" -> key.toString() // <-- ¡AQUÍ!
                            "Semanal" -> "S$key"
                            "Mensual" -> "M$key"
                            else -> key.toString()
                        },
                        balance = list.sumOf {
                            if (it.type == TransactionType.INCOME) it.amount else -it.amount
                        }.toFloat()
                    )
                }

                val current = entries.lastOrNull()?.balance ?: 0f
                val previous = if (entries.size > 1) entries[entries.size - 2].balance else 0f

                BalanceUiState(
                    selectedPeriod = period,
                    balanceData = entries,
                    summary = BalanceSummary(
                        currentBalance = current,
                        change = current - previous,
                        periodLabel = when (period) {
                            "Diario" -> "día anterior"
                            "Semanal" -> "semana anterior"
                            "Mensual" -> "mes anterior"
                            else -> ""
                        }
                    ),
                    isLoading = false
                )
            },
            onFailure = { e ->
                BalanceUiState(
                    selectedPeriod = period,
                    isLoading = false,
                    error = e.message
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BalanceUiState(isLoading = true)
    )

    fun onPeriodSelected(period: String) {
        selectedPeriod.value = period
    }
}
