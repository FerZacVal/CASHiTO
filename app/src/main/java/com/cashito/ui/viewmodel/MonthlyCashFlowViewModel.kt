package com.cashito.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.usecases.reports.MonthlyCashFlow
import com.cashito.domain.usecases.reports.ObserveMonthlyCashFlowUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class MonthlyCashFlowUiState(
    val isLoading: Boolean = true,
    val items: List<MonthlyCashFlow> = emptyList(),
    val error: String? = null
)

class MonthlyCashFlowViewModel(
    private val observeMonthlyCashFlowUseCase: ObserveMonthlyCashFlowUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonthlyCashFlowUiState())
    val uiState: StateFlow<MonthlyCashFlowUiState> = _uiState.asStateFlow()

    init {
        loadReport()
    }

    private fun loadReport() {
        viewModelScope.launch {
            observeMonthlyCashFlowUseCase()
                .catch { exception ->
                    _uiState.value = MonthlyCashFlowUiState(isLoading = false, error = exception.message)
                }
                .collect { reportList ->
                    _uiState.value = MonthlyCashFlowUiState(isLoading = false, items = reportList)
                }
        }
    }
}