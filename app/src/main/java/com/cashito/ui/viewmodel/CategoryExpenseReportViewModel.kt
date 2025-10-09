package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import com.cashito.ui.theme.tertiaryLight
import com.cashito.ui.theme.errorLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// --- STATE ---
data class CategoryExpense(val categoryName: String, val amount: Float, val color: Color)

data class CategoryExpenseReportUiState(
    val expenses: List<CategoryExpense> = emptyList(),
    val isLoading: Boolean = true
)

// --- VIEWMODEL ---
class CategoryExpenseReportViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryExpenseReportUiState())
    val uiState: StateFlow<CategoryExpenseReportUiState> = _uiState.asStateFlow()

    init {
        loadCategoryExpenses()
    }

    private fun loadCategoryExpenses() {
        // TODO: Replace with actual data fetching from a repository
        val sampleExpenses = listOf(
            CategoryExpense("Comida", 750.50f, primaryLight),
            CategoryExpense("Transporte", 320.00f, secondaryLight),
            CategoryExpense("Ocio", 450.75f, tertiaryLight),
            CategoryExpense("Ropa", 250.00f, Color(0xFFF59E0B)),
            CategoryExpense("Pagos", 1200.00f, errorLight)
        )

        _uiState.value = CategoryExpenseReportUiState(expenses = sampleExpenses, isLoading = false)
    }
}
