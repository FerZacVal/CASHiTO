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
    val isLoading: Boolean = true
)

// --- VIEWMODEL ---
class GoalsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        loadGoals()
    }

    private fun loadGoals() {
        // TODO: Replace with actual data fetching
        val sampleGoals = listOf(
            Goal("1", "Viaje a Cusco", "3,420", "5,000", 0.65f, "✈️", primaryLight),
            Goal("2", "Laptop nueva", "800", "4,500", 0.18f, "💻", secondaryLight),
            Goal("3", "Ahorro de emergencia", "1,200", "10,000", 0.12f, "🛡️", tertiaryLight),
            Goal("4", "Regalo de aniversario", "150", "500", 0.30f, "🎁", Color(0xFFEC4899))
        )

        _uiState.value = GoalsUiState(goals = sampleGoals, isLoading = false)
    }
}
