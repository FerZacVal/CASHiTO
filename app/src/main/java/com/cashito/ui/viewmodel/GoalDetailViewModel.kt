package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.errorLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// --- STATE ---
data class GoalDetail(
    val id: String,
    val title: String,
    val savedAmount: String,
    val targetAmount: String,
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
    val isLoading: Boolean = true
)

// --- VIEWMODEL ---
class GoalDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val goalId: String = checkNotNull(savedStateHandle["goalId"])

    private val _uiState = MutableStateFlow(GoalDetailUiState())
    val uiState: StateFlow<GoalDetailUiState> = _uiState.asStateFlow()

    init {
        loadGoalDetails()
    }

    private fun loadGoalDetails() {
        // TODO: Replace with actual data fetching using goalId
        val sampleGoal = GoalDetail(
            id = goalId,
            title = "Viaje a Cusco",
            savedAmount = "3,420",
            targetAmount = "5,000",
            progress = 0.65f,
            icon = "✈️",
            color = primaryLight,
            targetDate = "15 Oct 2024"
        )

        val sampleTransactions = listOf(
            GoalTransaction("1", "Ingreso automático", "Hoy, 09:30", "+S/ 200", primaryLight, "💰", primaryLight),
            GoalTransaction("2", "Ingreso extra", "Ayer, 14:20", "+S/ 50", primaryLight, "💸", primaryLight),
            GoalTransaction("3", "Redondeo de compras", "Hace 2 días", "+S/ 15.50", primaryLight, "🔄", primaryLight)
        )

        _uiState.value = GoalDetailUiState(
            goal = sampleGoal,
            transactions = sampleTransactions,
            isLoading = false
        )
    }

    fun onRecurringChanged(isEnabled: Boolean) {
        _uiState.value = _uiState.value.copy(isRecurringEnabled = isEnabled)
    }

    fun onShowMenu(show: Boolean) {
        _uiState.value = _uiState.value.copy(showMenu = show)
    }
}
