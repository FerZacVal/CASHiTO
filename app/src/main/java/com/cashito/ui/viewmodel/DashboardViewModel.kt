package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.usecases.auth.GetCurrentUserUseCase
import com.cashito.domain.usecases.balance.GetBalanceUseCase
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

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
    val isLoading: Boolean = true
)

// --- VIEWMODEL ---
class DashboardViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getBalanceUseCase: GetBalanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refreshData() // Llamamos a la nueva función al iniciar
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Cargar nombre de usuario real
            val user = getCurrentUserUseCase()
            _uiState.update { it.copy(userName = user?.displayName ?: "") }

            // Cargar balance real
            val balance = getBalanceUseCase()
            val formattedBalance = NumberFormat.getCurrencyInstance(Locale("es", "PE")).format(balance)
            _uiState.update { it.copy(totalBalance = formattedBalance) }

            // TODO: Cargar el resto de los datos del dashboard (metas, transacciones recientes)
            _uiState.update { state ->
                state.copy(
                    mainGoalProgressText = "Meta principal: Viaje a Cusco — 65%",
                    mainGoalProgressPercentage = 65,
                    goals = listOf(
                        DashboardGoal("1", "Viaje a Cusco", "3,420", "5,000", 0.65f, "✈️", primaryLight),
                        DashboardGoal("2", "Laptop nueva", "800", "4,500", 0.18f, "💻", secondaryLight)
                    ),
                    transactions = listOf(
                        DashboardTransaction("Ingreso automático", "💰", primaryLight),
                        DashboardTransaction("Compra en supermercado", "🛒", secondaryLight)
                    ),
                    isLoading = false
                )
            }
        }
    }
}
