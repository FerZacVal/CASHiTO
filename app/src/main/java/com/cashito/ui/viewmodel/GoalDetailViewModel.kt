package com.cashito.ui.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.goal.Goal
import com.cashito.domain.entities.transaction.Transaction
import com.cashito.domain.usecases.goal.DeleteGoalUseCase
import com.cashito.domain.usecases.goal.GetGoalByIdUseCase
import com.cashito.domain.usecases.transaction.GetTransactionsForGoalUseCase
import com.cashito.ui.theme.primaryLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException

// --- STATE ---
/**
 * ARREGLADO: Los montos ahora son Doubles para evitar errores de formato.
 * La UI se encargará de formatearlos como texto.
 */
data class GoalDetail(
    val id: String,
    val title: String,
    val savedAmount: Double, // Ahora es Double
    val targetAmount: Double, // Ahora es Double
    val progress: Float,
    val icon: String,
    val color: Color,
    val targetDate: String
)

data class GoalTransaction(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: String,
    val amountColor: Color,
    val icon: String,
    val color: Color
)

data class GoalDetailUiState(
    val goal: GoalDetail? = null,
    val transactions: List<GoalTransaction> = emptyList(),
    val isRecurringEnabled: Boolean = true,
    val showMenu: Boolean = false,
    val isLoading: Boolean = true,
    val goalDeleted: Boolean = false,
    val error: String? = null
)

// --- VIEWMODEL ---
class GoalDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getGoalByIdUseCase: GetGoalByIdUseCase,
    private val getTransactionsForGoalUseCase: GetTransactionsForGoalUseCase,
    private val deleteGoalUseCase: DeleteGoalUseCase
) : ViewModel() {

    private val goalId: String = checkNotNull(savedStateHandle["goalId"])

    private val _uiState = MutableStateFlow(GoalDetailUiState())
    val uiState: StateFlow<GoalDetailUiState> = _uiState.asStateFlow()

    init {
        loadGoalDetails()
    }

    private fun loadGoalDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val goalFlow = getGoalByIdUseCase(goalId)
                val transactionsFlow = getTransactionsForGoalUseCase(goalId)

                combine(goalFlow, transactionsFlow) { goal, transactions ->
                    if (goal == null) {
                        return@combine GoalDetailUiState(isLoading = false, error = "Goal not found")
                    }
                    GoalDetailUiState(
                        goal = goal.toGoalDetail(),
                        transactions = transactions.map { it.toGoalTransaction() },
                        isLoading = false
                    )
                }.collect { newState ->
                    _uiState.value = newState
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("GoalDetailViewModel", "Error loading goal details", e)
                _uiState.update { it.copy(isLoading = false, error = "Failed to load details: ${e.message}") }
            }
        }
    }

    fun deleteGoal() {
        viewModelScope.launch {
            try {
                deleteGoalUseCase(goalId)
                _uiState.update { it.copy(goalDeleted = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to delete goal: ${e.message}") }
            }
        }
    }

    fun onRecurringChanged(isEnabled: Boolean) {
        _uiState.update { it.copy(isRecurringEnabled = isEnabled) }
    }

    fun onShowMenu(show: Boolean) {
        _uiState.update { it.copy(showMenu = show) }
    }
}

// --- Mappers ---

private fun Goal.toGoalDetail(): GoalDetail {
    val progress = if (this.targetAmount > 0) (this.savedAmount / this.targetAmount).toFloat() else 0f
    return GoalDetail(
        id = this.id,
        title = this.name,
        // ARREGLADO: Pasamos los Doubles directamente.
        savedAmount = this.savedAmount,
        targetAmount = this.targetAmount,
        progress = progress,
        icon = this.icon,
        color = Color(android.graphics.Color.parseColor(this.colorHex)),
        targetDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(this.targetDate)
    )
}

private fun Transaction.toGoalTransaction(): GoalTransaction {
    return GoalTransaction(
        id = this.id,
        title = this.description,
        subtitle = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(this.date),
        amount = "+S/ ${String.format("%,.2f", this.amount)}",
        amountColor = primaryLight, // TODO: Map from transaction type
        icon = "💰", // TODO: Map from category
        color = primaryLight
    )
}
