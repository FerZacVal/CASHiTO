package com.cashito.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.usecases.reports.ObserveReportsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

data class ReportsUiState(
    val income: String = "S/ 0.00",
    val expenses: String = "S/ 0.00",
    val balance: String = "S/ 0.00",
    val isLoading: Boolean = true
)

class ReportsViewModel(
    private val observeReportsUseCase: ObserveReportsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        observeReports()
    }

    private fun observeReports() {
        viewModelScope.launch {
            observeReportsUseCase().collectLatest { (income, expenses, balance) ->
                val format = NumberFormat.getCurrencyInstance(Locale("es", "PE"))
                _uiState.value = ReportsUiState(
                    income = format.format(income),
                    expenses = format.format(expenses),
                    balance = format.format(balance),
                    isLoading = false
                )
            }
        }
    }
}

