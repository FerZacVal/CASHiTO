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
data class QuickOutCategory(
    val id: String,
    val title: String,
    val icon: String,
    val color: Color
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
class QuickOutViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuickOutUiState())
    val uiState: StateFlow<QuickOutUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        // TODO: Replace with actual data fetching
        _uiState.value = _uiState.value.copy(
            categories = listOf(
                QuickOutCategory("1", "Comida", "🍔", primaryLight),
                QuickOutCategory("2", "Transporte", "🚌", secondaryLight),
                QuickOutCategory("3", "Compras", "🛒", tertiaryLight),
                QuickOutCategory("4", "Ocio", "🎉", Color(0xFFF59E0B))
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
        val isEnabled = (state.customAmount.isNotEmpty() || state.selectedAmount.isNotEmpty()) && state.selectedCategoryId.isNotEmpty()
        _uiState.value = state.copy(isConfirmEnabled = isEnabled)
    }

    fun onConfirmExpense() {
        if (!_uiState.value.isConfirmEnabled) return
        // TODO: Implement expense saving logic
        _uiState.value = _uiState.value.copy(expenseConfirmed = true)
    }
}
