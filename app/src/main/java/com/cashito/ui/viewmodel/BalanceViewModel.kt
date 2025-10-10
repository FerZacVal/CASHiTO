package com.cashito.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

// --- STATE ---
data class BalanceEntry(val label: String, val balance: Float)
data class BalanceSummary(val currentBalance: Float, val change: Float, val periodLabel: String)

data class BalanceUiState(
    val selectedPeriod: String = "Semanal",
    val balanceData: List<BalanceEntry> = emptyList(),
    val summary: BalanceSummary = BalanceSummary(0f, 0f, ""),
    val isLoading: Boolean = true
)

// --- VIEWMODEL ---
class BalanceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BalanceUiState())
    val uiState: StateFlow<BalanceUiState> = _uiState.asStateFlow()

    init {
        loadBalanceData()
    }

    fun onPeriodSelected(period: String) {
        _uiState.value = _uiState.value.copy(selectedPeriod = period, isLoading = true)
        loadBalanceData()
    }

    private fun loadBalanceData() {
        // TODO: Replace with actual data fetching from a repository
        val period = _uiState.value.selectedPeriod

        val data = when (period) {
            "Diario" -> (1..7).map { BalanceEntry("Día $it", Random.nextFloat() * 200 + 2000) }
            "Semanal" -> (1..4).map { BalanceEntry("Sem $it", Random.nextFloat() * 1000 + 5000) }
            "Mensual" -> (1..6).map { BalanceEntry("Mes $it", Random.nextFloat() * 3000 + 10000) }
            else -> emptyList()
        }

        val summary = when (period) {
            "Diario" -> BalanceSummary(2345.67f, -15.20f, "día anterior")
            "Semanal" -> BalanceSummary(8765.43f, 250.75f, "semana anterior")
            "Mensual" -> BalanceSummary(25432.10f, 1200.50f, "mes anterior")
            else -> BalanceSummary(0f, 0f, "")
        }

        _uiState.value = _uiState.value.copy(
            balanceData = data,
            summary = summary,
            isLoading = false
        )
    }
}
