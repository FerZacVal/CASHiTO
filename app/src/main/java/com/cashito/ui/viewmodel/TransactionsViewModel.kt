package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.transaction.TransactionType
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
    val color: Color, // Color del icono
    val category: String,
    val date: String, // Agrupación: "Hoy", "Ayer", "dd/MM/yyyy"
    val type: TransactionType
)

data class TransactionGroup(
    val date: String,
    val transactions: List<Transaction>
)

data class TransactionsUiState(
    val allTransactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<TransactionGroup> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: String = "Todos",
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedTransaction: Transaction? = null,
    val showOptionsDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false
)

// --- VIEWMODEL ---
class TransactionsViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Aquí recolectamos el Flow que devuelve Result<List<DomainTransaction>>
                getTransactionsUseCase().collect { result ->
                    result.onSuccess { domainTransactions ->
                        val uiTransactions = domainTransactions.map { it.toUiTransaction() }
                        _uiState.update {
                            it.copy(
                                allTransactions = uiTransactions,
                                isLoading = false,
                                error = null
                            )
                        }
                        filterTransactions()
                    }.onFailure { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // --- Filtros y búsqueda ---
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterTransactions()
    }

    fun onFilterChanged(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
        filterTransactions()
    }

    // --- Long press actions ---
    fun onTransactionLongPressed(transaction: Transaction) {
        _uiState.update { it.copy(selectedTransaction = transaction, showOptionsDialog = true) }
    }

    fun onDismissDialogs() {
        _uiState.update { it.copy(selectedTransaction = null, showOptionsDialog = false, showDeleteConfirmDialog = false) }
    }

    fun onDeleteRequest() {
        _uiState.update { it.copy(showOptionsDialog = false, showDeleteConfirmDialog = true) }
    }

    fun onDeleteConfirm() {
        val transactionId = _uiState.value.selectedTransaction?.id
        if (transactionId != null) {
            // TODO: Llamar DeleteTransactionUseCase si existe
            val updatedTransactions = _uiState.value.allTransactions.filterNot { it.id == transactionId }
            _uiState.update { it.copy(allTransactions = updatedTransactions) }
            filterTransactions()
        }
        onDismissDialogs()
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

    // --- Mapper ---
    // Mapper a nivel de archivo, visible desde cualquier lugar
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
            date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(this.date),
            type = this.type
        )
    }
}


