package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.usecases.reports.ObserveIncomeReportUseCase
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import com.cashito.ui.theme.tertiaryLight
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CategoryIncome(val categoryName: String, val amount: Float, val color: Color)

data class IncomeReportUiState(
    val incomes: List<CategoryIncome> = emptyList(),
    val chartType: ChartType = ChartType.PIE,
    val isLoading: Boolean = true
)

class IncomeReportViewModel(
    private val observeIncomeReportUseCase: ObserveIncomeReportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomeReportUiState())
    val uiState: StateFlow<IncomeReportUiState> = _uiState.asStateFlow()

    init {
        observeIncomeData()
    }

    fun onChartTypeChange(newType: ChartType) {
        _uiState.update { it.copy(chartType = newType) }
    }

    private fun observeIncomeData() {
        viewModelScope.launch {
            observeIncomeReportUseCase().collect { transactions ->
                val grouped = transactions.groupBy { it.category?.name ?: "Sin categoría" }

                val categoryColors = listOf(primaryLight, secondaryLight, tertiaryLight, Color(0xFF8B5CF6))
                val incomes = grouped.entries.mapIndexed { index, (category, list) ->
                    CategoryIncome(
                        categoryName = category,
                        amount = list.sumOf { it.amount.toDouble() }.toFloat(),
                        color = categoryColors[index % categoryColors.size]
                    )
                }

                _uiState.value = IncomeReportUiState(
                    incomes = incomes,
                    chartType = _uiState.value.chartType,
                    isLoading = false
                )
            }
        }
    }
}

