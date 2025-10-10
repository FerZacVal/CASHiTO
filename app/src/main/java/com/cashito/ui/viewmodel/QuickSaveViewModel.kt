package com.cashito.ui.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.category.Category
import com.cashito.domain.entities.income.Income
import com.cashito.domain.usecases.income.AddIncomeUseCase
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import com.cashito.ui.theme.tertiaryLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

// --- STATE ---
data class QuickSaveCategory(
    val id: String,
    val title: String,
    val icon: String,
    val color: Color,
    val colorHex: String
)

data class QuickSaveUiState(
    val presetAmounts: List<String> = listOf("50", "100", "500", "1000"),
    val categories: List<QuickSaveCategory> = emptyList(),
    val selectedAmount: String = "",
    val customAmount: String = "",
    val selectedCategoryId: String = "",
    val isConfirmEnabled: Boolean = false,
    val incomeConfirmed: Boolean = false
)

// --- VIEWMODEL ---
class QuickSaveViewModel(
    private val addIncomeUseCase: AddIncomeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuickSaveUiState())
    val uiState: StateFlow<QuickSaveUiState> = _uiState.asStateFlow()

    init {
        Log.d("FlowDebug", "QUICKE SABE VIEW MODEL: CLASS")
        loadCategories()

    }

    private fun loadCategories() {
        _uiState.value = _uiState.value.copy(
            categories = listOf(
                QuickSaveCategory("5", "Nómina", "💼", primaryLight, "#FF6F00"),
                QuickSaveCategory("6", "Ventas", "📈", secondaryLight, "#FFAB00"),
                QuickSaveCategory("7", "Freelance", "💻", tertiaryLight, "#00BFA5"),
                QuickSaveCategory("8", "Regalo", "🎁", Color(0xFF10B981), "#10B981")
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

    fun onConfirmIncome() {
        if (!_uiState.value.isConfirmEnabled) return

        viewModelScope.launch {
            val state = _uiState.value
            val amount = state.customAmount.toDoubleOrNull() ?: state.selectedAmount.toDoubleOrNull() ?: 0.0
            val selectedCategory = state.categories.firstOrNull { it.id == state.selectedCategoryId }

            if (selectedCategory != null) {
                val income = Income(
                    id = "",
                    description = selectedCategory.title,
                    amount = amount,
                    date = Date(),
                    category = Category(
                        id = selectedCategory.id,
                        name = selectedCategory.title,
                        icon = selectedCategory.icon,
                        color = selectedCategory.colorHex
                    )
                )
                addIncomeUseCase(income)
                _uiState.value = _uiState.value.copy(incomeConfirmed = true)
            }
        }
    }
}
