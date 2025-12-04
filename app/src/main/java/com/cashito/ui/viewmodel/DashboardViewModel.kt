package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.gamification.WeeklyChallenge
import com.cashito.domain.usecases.auth.GetCurrentUserUseCase
import com.cashito.domain.usecases.balance.GetBalanceUseCase
import com.cashito.domain.usecases.gamification.GetWeeklyChallengeUseCase
import com.cashito.domain.usecases.goal.GetGoalsUseCase
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import com.cashito.domain.entities.goal.Goal as DomainGoal

// --- STATE ---
data class DashboardGoal(
    val id: String,
    val title: String,
    val savedAmount: String,
    val targetAmount: String,
    val progress: Float,
    val icon: String,
    val color: Color
)

data class DashboardTransaction(
    val title: String,
    val icon: String,
    val color: Color
)

data class DashboardUiState(
    val userName: String = "",
    val totalBalance: String = "S/ 0.00",
    val mainGoalProgressText: String = "",
    val mainGoalProgressPercentage: Int = 0,
    val goals: List<DashboardGoal> = emptyList(),
    val transactions: List<DashboardTransaction> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val weeklyChallenge: WeeklyChallenge? = null // AÑADIDO
)

// --- VIEWMODEL ---
class DashboardViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getBalanceUseCase: GetBalanceUseCase,
    private val getGoalsUseCase: GetGoalsUseCase,
    private val getWeeklyChallengeUseCase: GetWeeklyChallengeUseCase // AÑADIDO
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refreshData()
        observeGoals()
        observeChallenge() // AÑADIDO
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val user = getCurrentUserUseCase()
            _uiState.update { it.copy(userName = user?.displayName ?: "") }

            val balance = getBalanceUseCase()
            val formattedBalance = NumberFormat.getCurrencyInstance(Locale("es", "PE")).format(balance)
            _uiState.update { it.copy(totalBalance = formattedBalance, isLoading = false) }

            // TODO: Cargar el resto de los datos (transacciones recientes, etc.)
        }
    }

    private fun observeGoals() {
        viewModelScope.launch {
            getGoalsUseCase()
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect { domainGoals ->
                    val uiGoals = domainGoals.map { it.toDashboardGoal() }
                    _uiState.update { it.copy(goals = uiGoals) }
                    // TODO: Podríamos añadir lógica para seleccionar la meta principal
                }
        }
    }

    private fun observeChallenge() {
        viewModelScope.launch {
            getWeeklyChallengeUseCase()
                .collect { challenge ->
                    _uiState.update { it.copy(weeklyChallenge = challenge) }
                }
        }
    }
}

// --- Mapper para el Dashboard ---
private fun DomainGoal.toDashboardGoal(): DashboardGoal {
    val progress = if (this.targetAmount > 0) (this.savedAmount / this.targetAmount).toFloat() else 0f
    return DashboardGoal(
        id = this.id,
        title = this.name,
        savedAmount = NumberFormat.getCurrencyInstance(Locale("es", "PE")).format(this.savedAmount),
        targetAmount = NumberFormat.getCurrencyInstance(Locale("es", "PE")).format(this.targetAmount),
        progress = progress,
        icon = this.icon,
        color = try {
            Color(android.graphics.Color.parseColor(this.colorHex))
        } catch (e: IllegalArgumentException) {
            primaryLight
        }
    )
}
