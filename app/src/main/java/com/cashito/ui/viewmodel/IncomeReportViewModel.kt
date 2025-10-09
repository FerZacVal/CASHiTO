package com.cashito.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import com.cashito.ui.theme.tertiaryLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// --- STATE ---
data class IncomeReportUiState(
    val incomes: List<IncomeCategory> = emptyList(),
    val isLoading: Boolean = true
)

// --- VIEWMODEL ---
class IncomeReportViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(IncomeReportUiState())
    val uiState: StateFlow<IncomeReportUiState> = _uiState.asStateFlow()

    init {
        loadIncomeData()
    }

    private fun loadIncomeData() {
        // TODO: Replace with actual data fetching from a repository
        val sampleIncomes = listOf(
            IncomeCategory("1", "Trabajo principal", "💼", primaryLight),
            IncomeCategory("2", "Medio tiempo", "⏰", secondaryLight),
            IncomeCategory("3", "Recurrente", "🔄", tertiaryLight)
        )

        _uiState.value = IncomeReportUiState(incomes = sampleIncomes, isLoading = false)
    }
}
