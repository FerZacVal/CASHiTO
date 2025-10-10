package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.category.Category
import com.cashito.domain.entities.expense.Expense
import com.cashito.domain.usecases.expense.AddExpenseUseCase
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import com.cashito.ui.theme.tertiaryLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

// --- STATE ---
data class QuickOutCategory(
    val id: String,
    val title: String,
    val icon: String,
    val color: Color,
    val colorHex: String
)

data class QuickOutUiState(
    val presetAmounts: List<String> = listOf("5", "10", "20", "50"),
    val categories: List<QuickOutCategory> = emptyList(),
    val selectedAmount: String = "",
    val customAmount: String = "",
    val selectedCategoryId: String = "",
    val isConfirmEnabled: Boolean = false,
    val expenseConfirmed: Boolean = false
)

// --- VIEWMODEL ---
class QuickOutViewModel(
    private val addExpenseUseCase: AddExpenseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuickOutUiState())
    val uiState: StateFlow<QuickOutUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        _uiState.value = _uiState.value.copy(
            categories = listOf(
                QuickOutCategory("1", "Comida", "🍔", primaryLight, "#FF6F00"),
                QuickOutCategory("2", "Transporte", "🚌", secondaryLight, "#FFAB00"),
                QuickOutCategory("3", "Compras", "🛒", tertiaryLight, "#00BFA5"),
                QuickOutCategory("4", "Ocio", "🎉", Color(0xFFF59E0B), "#F59E0B")
            )
        )
    }

    fun onPresetAmountSelected(amount: String) {
        _uiState.value = _uiState.value.copy(
            selectedAmount = amount,
            customAmount = ""
        )
        validateConfirmButton()
    }

    fun onCustomAmountChanged(amount: String) {
        _uiState.value = _uiState.value.copy(
            customAmount = amount,
            selectedAmount = ""
        )
        validateConfirmButton()
    }

    fun onCategorySelected(categoryId: String) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
        validateConfirmButton()
    }

    private fun validateConfirmButton() {
        val state = _uiState.value
        val amount = state.customAmount.toDoubleOrNull() ?: state.selectedAmount.toDoubleOrNull() ?: 0.0
        val isEnabled = amount > 0 && state.selectedCategoryId.isNotEmpty()
        _uiState.value = state.copy(isConfirmEnabled = isEnabled)
    }

    fun onConfirmExpense() {
        if (!_uiState.value.isConfirmEnabled) return

        _uiState.value = _uiState.value.copy(isConfirmEnabled = false)

        viewModelScope.launch {
            val state = _uiState.value
            val amount = state.customAmount.toDoubleOrNull() ?: state.selectedAmount.toDoubleOrNull() ?: 0.0
            val selectedCategory = state.categories.firstOrNull { it.id == state.selectedCategoryId }

            if (selectedCategory != null) {
                val expense = Expense(
                    id = "", // Firestore will generate the ID
                    description = selectedCategory.title,
                    amount = amount,
                    date = Date(),
                    category = Category( // Creating the domain category object
                        id = selectedCategory.id,
                        name = selectedCategory.title,
                        icon = selectedCategory.icon,
                        color = selectedCategory.colorHex
                    )
                )
                addExpenseUseCase(expense)
                _uiState.value = _uiState.value.copy(expenseConfirmed = true)
            }
        }
    }
}
