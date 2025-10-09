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
data class QuickSaveGoal(
    val id: String,
    val title: String,
    val icon: String,
    val color: Color
)

data class IncomeCategory(
    val id: String,
    val title: String,
    val icon: String,
    val color: Color
)

data class QuickSaveUiState(
    val presetAmounts: List<String> = listOf("5", "10", "20", "50"),
    val goals: List<QuickSaveGoal> = emptyList(),
    val categories: List<IncomeCategory> = emptyList(),
    val selectedAmount: String = "",
    val customAmount: String = "",
    val selectedGoalId: String = "",
    val selectedCategoryId: String = "",
    val isConfirmEnabled: Boolean = false,
    val incomeConfirmed: Boolean = false
)

// --- VIEWMODEL ---
class QuickSaveViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuickSaveUiState())
    val uiState: StateFlow<QuickSaveUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        // TODO: Replace with actual data fetching
        _uiState.value = _uiState.value.copy(
            goals = listOf(
                QuickSaveGoal("1", "Viaje a Cusco", "✈️", primaryLight),
                QuickSaveGoal("2", "Laptop nueva", "💻", secondaryLight)
            ),
            categories = listOf(
                IncomeCategory("1", "Trabajo principal", "💼", primaryLight),
                IncomeCategory("2", "Medio tiempo", "⏰", secondaryLight),
                IncomeCategory("3", "Recurrente", "🔄", tertiaryLight)
            )
        )
    }

    fun onPresetAmountSelected(amount: String) {
        _uiState.value = _uiState.value.copy(selectedAmount = amount, customAmount = "")
        validateConfirmButton()
    }

    fun onCustomAmountChanged(amount: String) {
        _uiState.value = _uiState.value.copy(customAmount = amount, selectedAmount = "")
        validateConfirmButton()
    }

    fun onGoalSelected(goalId: String) {
        _uiState.value = _uiState.value.copy(selectedGoalId = goalId)
        validateConfirmButton()
    }

    fun onCategorySelected(categoryId: String) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
        validateConfirmButton()
    }

    private fun validateConfirmButton() {
        val state = _uiState.value
        val isAmountSelected = state.customAmount.isNotEmpty() || state.selectedAmount.isNotEmpty()
        val isEnabled = isAmountSelected && state.selectedGoalId.isNotEmpty() && state.selectedCategoryId.isNotEmpty()
        _uiState.value = state.copy(isConfirmEnabled = isEnabled)
    }

    fun onConfirmIncome() {
        if (!_uiState.value.isConfirmEnabled) return
        // TODO: Implement income saving logic
        _uiState.value = _uiState.value.copy(incomeConfirmed = true)
    }
}
