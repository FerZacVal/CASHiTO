package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import com.cashito.ui.theme.tertiaryLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// --- STATE ---
data class CategoryIncome(val categoryName: String, val amount: Float, val color: Color)

data class IncomeReportUiState(
    val incomes: List<CategoryIncome> = emptyList(),
    val chartType: ChartType = ChartType.PIE,
    val isLoading: Boolean = true
)

// --- VIEWMODEL ---
class IncomeReportViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(IncomeReportUiState())
    val uiState: StateFlow<IncomeReportUiState> = _uiState.asStateFlow()

    init {
        loadIncomeData()
    }

    fun onChartTypeChange(newType: ChartType) {
        _uiState.value = _uiState.value.copy(chartType = newType)
    }

    private fun loadIncomeData() {
        // TODO: Replace with actual data fetching from a repository
        val sampleIncomes = listOf(
            CategoryIncome("Trabajo principal", 5200.00f, primaryLight),
            CategoryIncome("Medio tiempo", 950.50f, secondaryLight),
            CategoryIncome("Recurrente", 300.00f, tertiaryLight),
            CategoryIncome("Otros", 150.00f, Color(0xFF8B5CF6))
        )

        _uiState.value = _uiState.value.copy(
            incomes = sampleIncomes,
            isLoading = false
        )
    }
}
