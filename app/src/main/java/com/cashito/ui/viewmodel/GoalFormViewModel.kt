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
    val isEditing: Boolean = false, // Flag que diferencia entre modo Creación y modo Edición
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
    val isLoading: Boolean = false // Para mostrar feedback durante la carga de una meta para editar
)

// --- VIEWMODEL ---

/**
 * ViewModel para la pantalla de formulario de metas (GoalFormScreen).
 * Este ViewModel es "inteligente" y maneja dos responsabilidades:
 * 1. La creación de una nueva meta desde cero.
 * 2. La carga y actualización de una meta existente (modo edición).
 * La diferenciación entre los modos se hace a través de la presencia de un `goalId` en los argumentos de navegación.
 */
class GoalFormViewModel(
    // --- INYECCIÓN DE DEPENDENCIAS ---
    // Gracias a Koin, recibimos todas las herramientas (Casos de Uso) que necesitamos para operar.
    savedStateHandle: SavedStateHandle,
    private val createGoalUseCase: CreateGoalUseCase, // Para la lógica de CREACIÓN.
    private val getGoalByIdUseCase: GetGoalByIdUseCase, // Para la lógica de LECTURA (en modo edición).
    private val updateGoalUseCase: UpdateGoalUseCase  // Para la lógica de ACTUALIZACIÓN (en modo edición).
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalFormUiState())
    val uiState: StateFlow<GoalFormUiState> = _uiState.asStateFlow()

    // Variable para almacenar la meta original cuando estamos en modo edición.
    // Es crucial para preservar campos que no se editan en la UI (como savedAmount, creationDate, etc.)
    // al momento de actualizar el documento en la base de datos.
    private var initialGoal: Goal? = null

    /**
     * El bloque de inicialización. Es el punto de entrada del ViewModel.
     * Aquí leemos los argumentos de navegación para determinar si estamos creando o editando.
     */
    init {
        val goalId: String? = savedStateHandle["goalId"]
        if (goalId != null) {
            // Si un goalId está presente, entramos inmediatamente en MODO EDICIÓN.
            loadGoalForEditing(goalId)
        }
    }

    /**
     * Carga los datos de una meta existente desde el repositorio para poblar el formulario.
     * Esta función es el corazón del MODO EDICIÓN.
     * @param goalId El ID de la meta a cargar.
     */
    private fun loadGoalForEditing(goalId: String) {
        _uiState.update { it.copy(isLoading = true) } // Notificamos a la UI que empiece a mostrar una animación de carga.
        viewModelScope.launch {
            // --- LÓGICA DE CARGA DE DATOS ---
            // Llamamos al caso de uso para obtener la meta. Usamos .first() porque solo necesitamos
            // el primer valor que emita el Flow (el estado actual de la meta), no necesitamos seguir escuchando cambios aquí.
            val goal = getGoalByIdUseCase(goalId).first()
            if (goal != null) {
                // Guardamos una copia del objeto original. Esto es VITAL.
                // Al actualizar, solo modificaremos los campos que el usuario cambió (nombre, monto, etc.),
                // pero necesitamos preservar los datos internos como `savedAmount` y `creationDate`.
                initialGoal = goal 

                // Actualizamos el estado de la UI con los datos reales de la meta cargada.
                _uiState.update {
                    it.copy(
                        isEditing = true, // Marcamos explícitamente que estamos en modo edición.
                        goalId = goal.id,
                        goalName = goal.name,
                        targetAmount = goal.targetAmount.toString(),
                        // --- MANEJO DE NULOS SEGURO ---
                        // Usamos el operador de llamada segura (?.) porque `targetDate` en la entidad podría ser nulo.
                        // Si no es nulo, obtenemos `.time`. Si es nulo, toda la expresión devuelve `null`,
                        // lo cual es un valor válido para `selectedDate` (Long?).
                        selectedDate = goal.targetDate?.time, 
                        selectedIcon = goal.icon,
                        selectedColor = Color(android.graphics.Color.parseColor(goal.colorHex)),
                        isLoading = false // Detenemos la animación de carga.
                    )
                }
                validateForm() // Validamos el formulario con los datos ya cargados.
            }
        }
    }
    
    // --- Manejadores de eventos de la UI (sin cambios) ---
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

    /**
     * Orquesta el guardado de la meta. Esta función ahora es "inteligente" y sabe si debe
     * crear una nueva meta o actualizar una existente basándose en el flag `isEditing`.
     */
    fun onSaveGoal() {
        val state = _uiState.value
        // ... (validación de errores)
        val nameError = if (state.goalName.isBlank()) "El nombre es requerido" else null
        val amountError = if (state.targetAmount.isBlank()) "El monto es requerido" else null
        _uiState.update { it.copy(goalNameError = nameError, targetAmountError = amountError) }

        if (nameError == null && amountError == null && state.selectedDate != null) {
            viewModelScope.launch {
                // --- LÓGICA DE DECISIÓN: CREAR vs. ACTUALIZAR ---
                if (state.isEditing && initialGoal != null) {
                    // --- LÓGICA DE ACTUALIZACIÓN ---
                    // Si estamos en modo edición y tenemos una meta inicial cargada...
                    // Usamos .copy() sobre el `initialGoal` original. Esto es CLAVE para no perder
                    // datos como `savedAmount` que no están en el formulario de edición.
                    val updatedGoal = initialGoal!!.copy(
                        name = state.goalName,
                        targetAmount = state.targetAmount.toDoubleOrNull() ?: 0.0,
                        targetDate = Date(state.selectedDate),
                        icon = state.selectedIcon,
                        colorHex = "#%06X".format(0xFFFFFF and state.selectedColor.toArgb())
                    )
                    // Llamamos al Caso de Uso de actualización.
                    updateGoalUseCase(updatedGoal)
                } else {
                    // --- LÓGICA DE CREACIÓN (sin cambios) ---
                    // Si no estamos en modo edición, seguimos el flujo normal de creación.
                    val newGoal = Goal(
                        id = "",
                        userId = "",
                        name = state.goalName,
                        targetAmount = state.targetAmount.toDoubleOrNull() ?: 0.0,
                        savedAmount = 0.0, 
                        targetDate = Date(state.selectedDate),
                        creationDate = Date(),
                        icon = state.selectedIcon,
                        colorHex = "#%06X".format(0xFFFFFF and state.selectedColor.toArgb())
                    )
                    createGoalUseCase(newGoal)
                }
                // Notificamos a la UI que la meta se guardó para que pueda cerrar la pantalla.
                _uiState.update { it.copy(goalSaved = true) }
            }
        }
    }
}
