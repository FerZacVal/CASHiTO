package com.cashito.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// --- STATE ---
data class ReportsUiState(
    val income: String = "S/ 0.00",
    val expenses: String = "S/ 0.00",
    val balance: String = "S/ 0.00",
    val isLoading: Boolean = true
)

// --- VIEWMODEL ---
class ReportsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadReportData()
    }

    private fun loadReportData() {
        // TODO: Replace with actual data fetching from a repository
        // Simulate a network delay
        // viewModelScope.launch {
        //     delay(1500)
        _uiState.value = ReportsUiState(
            income = "S/ 5,000.00",
            expenses = "S/ 2,345.50",
            balance = "S/ 2,654.50",
            isLoading = false
        )
        // }
    }
}
