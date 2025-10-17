package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.usecases.reports.ObserveExpenseReportUseCase
import com.cashito.ui.theme.errorLight
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import com.cashito.ui.theme.tertiaryLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

enum class ChartType { PIE, BAR }

data class CategoryExpense(val categoryName: String, val amount: Float, val color: Color)

data class CategoryExpenseReportUiState(
    val expenses: List<CategoryExpense> = emptyList(),
    val chartType: ChartType = ChartType.PIE,
    val isLoading: Boolean = true,
    val error: String? = null
)

class CategoryExpenseReportViewModel(
    private val observeExpenseReportUseCase: ObserveExpenseReportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryExpenseReportUiState())
    val uiState: StateFlow<CategoryExpenseReportUiState> = _uiState.asStateFlow()

    private val categoryColors = listOf(
        primaryLight, secondaryLight, tertiaryLight, errorLight,
        Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF6366F1)
    )

    init {
        observeExpenses()
    }

    fun onChartTypeChange(newType: ChartType) {
        _uiState.value = _uiState.value.copy(chartType = newType)
    }

    private fun observeExpenses() {
        viewModelScope.launch {
            observeExpenseReportUseCase()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar los datos"
                    )
                }
                .collectLatest { transactions ->
                    val grouped = transactions
                        .groupBy { it.category?.name ?: "Sin categoría" }
                        .map { (categoryName, list) ->
                            CategoryExpense(
                                categoryName = categoryName,
                                amount = list.sumOf { it.amount.toDouble() }.toFloat(),
                                color = categoryColors[
                                    (categoryName.hashCode().absoluteValue) % categoryColors.size
                                ]
                            )
                        }

                    _uiState.value = _uiState.value.copy(
                        expenses = grouped,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }
}
