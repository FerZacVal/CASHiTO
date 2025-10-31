package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.usecases.goal.GetGoalsUseCase
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import com.cashito.ui.theme.tertiaryLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.cashito.domain.entities.goal.Goal as DomainGoal

// --- STATE ---
data class Goal(
    val id: String,
    val title: String,
    val savedAmount: String,
    val targetAmount: String,
    val progress: Float,
    val icon: String,
    val color: Color
)

data class GoalsUiState(
    val goals: List<Goal> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

// --- VIEWMODEL ---
class GoalsViewModel(
    private val getGoalsUseCase: GetGoalsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        observeGoals()
    }

    private fun observeGoals() {
        viewModelScope.launch {
            getGoalsUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { domainGoals ->
                    val uiGoals = domainGoals.map { it.toUiModel() }
                    _uiState.update { it.copy(goals = uiGoals, isLoading = false) }
                }
        }
    }
}

// --- Mapper ---
private fun DomainGoal.toUiModel(): Goal {
    val progress = if (this.targetAmount > 0) (this.savedAmount / this.targetAmount).toFloat() else 0f
    return Goal(
        id = this.id,
        title = this.name,
        savedAmount = "S/ ${String.format("%,.2f", this.savedAmount)}",
        targetAmount = "S/ ${String.format("%,.2f", this.targetAmount)}",
        progress = progress,
        icon = this.icon,
        color = try {
            Color(android.graphics.Color.parseColor(this.colorHex))
        } catch (e: IllegalArgumentException) {
            primaryLight // Color por defecto si el parseo falla
        }
    )
}
