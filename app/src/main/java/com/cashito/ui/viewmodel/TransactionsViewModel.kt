package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.errorLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// --- STATE ---
data class Transaction(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: String,
    val amountColor: Color,
    val icon: String,
    val color: Color,
    val category: String = "",
    val date: String
)

data class TransactionGroup(
    val date: String,
    val transactions: List<Transaction>
)

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<TransactionGroup> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: String = "Todos",
    val isLoading: Boolean = true
)

// --- VIEWMODEL ---
class TransactionsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    private val sampleTransactions = listOf(
        Transaction("1", "Ingreso de sueldo", "Hoy, 09:30", "+S/ 2,500", primaryLight, "💰", primaryLight, "Trabajo principal", "Hoy"),
        Transaction("2", "Compra en supermercado", "Hoy, 18:45", "-S/ 85.50", errorLight, "🛒", errorLight, "Comida", "Hoy"),
        Transaction("3", "Ingreso freelance", "Ayer, 14:20", "+S/ 500", primaryLight, "📥", primaryLight, "Medio tiempo", "Ayer"),
        Transaction("4", "Pago de servicios", "Ayer, 10:15", "-S/ 120", errorLight, "💡", errorLight, "Pagos", "Ayer")
    )

    init {
        _uiState.value = _uiState.value.copy(transactions = sampleTransactions)
        filterTransactions()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterTransactions()
    }

    fun onFilterChanged(filter: String) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        filterTransactions()
    }

    private fun filterTransactions() {
        val state = _uiState.value
        val filtered = state.transactions.filter { transaction ->
            val matchesFilter = when (state.selectedFilter) {
                "Ingresos" -> transaction.amount.startsWith("+")
                "Gastos" -> transaction.amount.startsWith("-")
                else -> true
            }
            val matchesSearch = state.searchQuery.isEmpty() ||
                    transaction.title.contains(state.searchQuery, ignoreCase = true) ||
                    transaction.amount.contains(state.searchQuery, ignoreCase = true) ||
                    transaction.category.contains(state.searchQuery, ignoreCase = true)

            matchesFilter && matchesSearch
        }

        val grouped = filtered.groupBy { it.date }.map { (date, transactions) ->
            TransactionGroup(date, transactions)
        }

        _uiState.value = _uiState.value.copy(filteredTransactions = grouped, isLoading = false)
    }
}
