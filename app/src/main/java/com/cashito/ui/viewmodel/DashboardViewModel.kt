package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        // TODO: Replace with actual data fetching from a repository
        _uiState.value = DashboardUiState(
            userName = "Ana",
            totalBalance = "S/ 3,420",
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
