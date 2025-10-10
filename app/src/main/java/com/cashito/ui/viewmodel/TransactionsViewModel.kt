package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.transaction.TransactionType
import com.cashito.domain.usecases.auth.GetCurrentUserUseCase
import com.cashito.domain.usecases.transaction.GetTransactionsUseCase
import com.cashito.ui.theme.errorLight
import com.cashito.ui.theme.primaryLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import com.cashito.domain.entities.transaction.Transaction as DomainTransaction

// --- STATE ---
data class Transaction(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: String,
    val amountColor: Color,
    val icon: String,
    val color: Color, // Este color es para el icono
    val category: String,
    val date: String // Representación del grupo (e.g., "Hoy", "Ayer", "dd/MM/yyyy")
)

data class TransactionGroup(
    val date: String,
    val transactions: List<Transaction>
)

data class TransactionsUiState(
    val userName: String = "", // AÑADIDO: Campo para el nombre del usuario
    val allTransactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<TransactionGroup> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: String = "Todos",
    val isLoading: Boolean = true,
    val error: String? = null
)

// --- VIEWMODEL ---
class TransactionsViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase // AÑADIDO: Inyección del nuevo UseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Cargar nombre de usuario
            val user = getCurrentUserUseCase()
            // CORRECCIÓN: Usar 'displayName' en lugar de 'nombre'
            _uiState.update { it.copy(userName = user?.displayName ?: "") }

            // Cargar transacciones
            getTransactionsUseCase().onSuccess { domainTransactions ->
                val uiTransactions = domainTransactions.map { it.toUiTransaction() }
                _uiState.update {
                    it.copy(
                        allTransactions = uiTransactions,
                        isLoading = false
                    )
                }
                filterTransactions()
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterTransactions()
    }

    fun onFilterChanged(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
        filterTransactions()
    }

    private fun filterTransactions() {
        val state = _uiState.value
        val filtered = state.allTransactions.filter { transaction ->
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

        _uiState.update { it.copy(filteredTransactions = grouped) }
    }
}

// --- MAPPERS ---

private fun DomainTransaction.toUiTransaction(): Transaction {
    val amountPrefix = if (this.type == TransactionType.INCOME) "+S/ " else "-S/ "
    val amountColor = if (this.type == TransactionType.INCOME) primaryLight else errorLight
    val iconColor = if (this.type == TransactionType.INCOME) primaryLight else errorLight

    return Transaction(
        id = this.id,
        title = this.description,
        subtitle = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(this.date),
        amount = amountPrefix + String.format("%,.2f", this.amount),
        amountColor = amountColor,
        icon = this.category?.icon ?: "❓",
        color = iconColor,
        category = this.category?.name ?: "Sin categoría",
        date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(this.date)
    )
}
