package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.goal.Goal
import com.cashito.domain.usecases.goal.CreateGoalUseCase
import com.cashito.domain.usecases.goal.GetGoalByIdUseCase
import com.cashito.domain.usecases.goal.UpdateGoalUseCase
import com.cashito.ui.theme.primaryLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

// --- STATE ---
data class GoalFormUiState(
    val goalId: String? = null,
    val isEditing: Boolean = false,
    val goalName: String = "",
    val targetAmount: String = "",
    val selectedDate: Long? = null,
    val selectedIcon: String = "💻",
    val selectedColor: Color = primaryLight,
    val isRecurringEnabled: Boolean = false,
    val recurringAmount: String = "",
    val recurringFrequency: String = "Semanal",
    val showDatePicker: Boolean = false,
    val goalNameError: String? = null,
    val targetAmountError: String? = null,
    val isFormValid: Boolean = false,
    val goalSaved: Boolean = false,
    val isLoading: Boolean = false // Para mostrar feedback durante la carga
)

// --- VIEWMODEL ---
class GoalFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val createGoalUseCase: CreateGoalUseCase, 
    private val getGoalByIdUseCase: GetGoalByIdUseCase, // ARREGLADO: Inyectado
    private val updateGoalUseCase: UpdateGoalUseCase  // ARREGLADO: Inyectado
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalFormUiState())
    val uiState: StateFlow<GoalFormUiState> = _uiState.asStateFlow()

    private var initialGoal: Goal? = null

    init {
        val goalId: String? = savedStateHandle["goalId"]
        if (goalId != null) {
            loadGoalForEditing(goalId)
        }
    }

    private fun loadGoalForEditing(goalId: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            // ARREGLADO: Se obtienen los datos reales de la meta
            val goal = getGoalByIdUseCase(goalId).first() // Obtenemos la primera emisión del Flow
            if (goal != null) {
                initialGoal = goal // Guardamos el estado original para la actualización
                _uiState.update {
                    it.copy(
                        isEditing = true,
                        goalId = goal.id,
                        goalName = goal.name,
                        targetAmount = goal.targetAmount.toString(),
                        selectedDate = goal.targetDate?.time, // ARREGLADO: Se usa una llamada segura
                        selectedIcon = goal.icon,
                        selectedColor = Color(android.graphics.Color.parseColor(goal.colorHex)),
                        isLoading = false
                    )
                }
                validateForm()
            }
        }
    }
    
    fun onGoalNameChange(name: String) {
        _uiState.update { it.copy(goalName = name, goalNameError = null) }
        validateForm()
    }

    fun onTargetAmountChange(amount: String) {
        _uiState.update { it.copy(targetAmount = amount, targetAmountError = null) }
        validateForm()
    }

    fun onDateSelected(date: Long?) {
        _uiState.update { it.copy(selectedDate = date) }
        validateForm()
    }

    fun onIconSelected(icon: String) {
        _uiState.update { it.copy(selectedIcon = icon) }
    }

    fun onColorSelected(color: Color) {
        _uiState.update { it.copy(selectedColor = color) }
    }

    fun onRecurringEnabledChange(enabled: Boolean) {
        _uiState.update { it.copy(isRecurringEnabled = enabled) }
    }

    fun onRecurringAmountChange(amount: String) {
        _uiState.update { it.copy(recurringAmount = amount) }
    }

    fun onRecurringFrequencyChange(frequency: String) {
        _uiState.update { it.copy(recurringFrequency = frequency) }
    }

    fun onDatePickerDismiss(show: Boolean) {
        _uiState.update { it.copy(showDatePicker = show) }
    }

    private fun validateForm() {
        val state = _uiState.value
        val isValid = state.goalName.isNotBlank() && state.targetAmount.isNotBlank() && state.selectedDate != null
        _uiState.update { it.copy(isFormValid = isValid) }
    }

    fun onSaveGoal() {
        val state = _uiState.value
        val nameError = if (state.goalName.isBlank()) "El nombre es requerido" else null
        val amountError = if (state.targetAmount.isBlank()) "El monto es requerido" else null

        _uiState.update {
            it.copy(
                goalNameError = nameError,
                targetAmountError = amountError
            )
        }

        if (nameError == null && amountError == null && state.selectedDate != null) {
            viewModelScope.launch {
                if (state.isEditing && initialGoal != null) {
                    // ARREGLADO: Implementada la lógica de actualización
                    val updatedGoal = initialGoal!!.copy(
                        name = state.goalName,
                        targetAmount = state.targetAmount.toDoubleOrNull() ?: 0.0,
                        targetDate = Date(state.selectedDate),
                        icon = state.selectedIcon,
                        colorHex = "#%06X".format(0xFFFFFF and state.selectedColor.toArgb())
                    )
                    updateGoalUseCase(updatedGoal)
                } else {
                    val newGoal = Goal(
                        id = "", // Firestore lo genera
                        userId = "", // El DataSource lo añade
                        name = state.goalName,
                        targetAmount = state.targetAmount.toDoubleOrNull() ?: 0.0,
                        savedAmount = 0.0, // Siempre empieza en 0
                        targetDate = Date(state.selectedDate),
                        creationDate = Date(),
                        icon = state.selectedIcon,
                        colorHex = "#%06X".format(0xFFFFFF and state.selectedColor.toArgb())
                    )
                    createGoalUseCase(newGoal)
                }
                _uiState.update { it.copy(goalSaved = true) }
            }
        }
    }
}
