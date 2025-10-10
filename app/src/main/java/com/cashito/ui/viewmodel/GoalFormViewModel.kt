package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

// --- STATE ---
data class GoalFormUiState(
    val goalName: String = "",
    val targetAmount: String = "",
    val selectedDate: Long? = null,
    val selectedIcon: String = "💻",
    val selectedColor: Color? = null,
    val isRecurringEnabled: Boolean = false,
    val recurringAmount: String = "",
    val recurringFrequency: String = "Semanal",
    val showDatePicker: Boolean = false,
    val goalNameError: String? = null,
    val targetAmountError: String? = null,
    val isFormValid: Boolean = false,
    val goalSaved: Boolean = false
)

// --- VIEWMODEL ---
class GoalFormViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GoalFormUiState())
    val uiState: StateFlow<GoalFormUiState> = _uiState.asStateFlow()

    fun onGoalNameChange(name: String) {
        _uiState.value = _uiState.value.copy(goalName = name, goalNameError = null)
        validateForm()
    }

    fun onTargetAmountChange(amount: String) {
        _uiState.value = _uiState.value.copy(targetAmount = amount, targetAmountError = null)
        validateForm()
    }

    fun onDateSelected(date: Long?) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        validateForm()
    }

    fun onIconSelected(icon: String) {
        _uiState.value = _uiState.value.copy(selectedIcon = icon)
    }

    fun onColorSelected(color: Color) {
        _uiState.value = _uiState.value.copy(selectedColor = color)
    }

    fun onRecurringEnabledChange(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isRecurringEnabled = enabled)
    }

    fun onRecurringAmountChange(amount: String) {
        _uiState.value = _uiState.value.copy(recurringAmount = amount)
    }

    fun onRecurringFrequencyChange(frequency: String) {
        _uiState.value = _uiState.value.copy(recurringFrequency = frequency)
    }

    fun onDatePickerDismiss(show: Boolean) {
        _uiState.value = _uiState.value.copy(showDatePicker = show)
    }

    private fun validateForm() {
        val state = _uiState.value
        val isValid = state.goalName.isNotBlank() && state.targetAmount.isNotBlank() && state.selectedDate != null
        _uiState.value = state.copy(isFormValid = isValid)
    }

    fun onSaveGoal() {
        val state = _uiState.value
        val nameError = if (state.goalName.isBlank()) "El nombre es requerido" else null
        val amountError = if (state.targetAmount.isBlank()) "El monto es requerido" else null

        _uiState.value = _uiState.value.copy(
            goalNameError = nameError,
            targetAmountError = amountError
        )

        if (nameError == null && amountError == null && state.selectedDate != null) {
            // TODO: Implement actual save logic
            _uiState.value = _uiState.value.copy(goalSaved = true)
        }
    }
}
