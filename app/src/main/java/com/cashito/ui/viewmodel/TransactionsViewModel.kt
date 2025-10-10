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
    val color: Color, // Este color es para el icono
    val category: String,
    val date: String, // Representación del grupo (e.g., "Hoy", "Ayer", "dd/MM/yyyy")
    val type: TransactionType // Needed to know if it's income or expense for deletion/edition
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
    val selectedTransaction: Transaction? = null, // Transaction currently selected by long press
    val showOptionsDialog: Boolean = false, // To show Edit/Delete options
    val showDeleteConfirmDialog: Boolean = false // To show the final delete confirmation
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

            getTransactionsUseCase().onSuccess { domainTransactions ->
                val uiTransactions = domainTransactions.map { it.toUiTransaction() }
                _uiState.update {
                    it.copy(
                        allTransactions = uiTransactions,
                        isLoading = false
                    )
                }
                filterTransactions() // Filtrar después de cargar
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

    // --- Actions for Long Press ---

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
            // TODO: Call actual DeleteTransactionUseCase

            // For now, just remove it from the local list for immediate feedback
            val updatedTransactions = _uiState.value.allTransactions.filterNot { it.id == transactionId }
            _uiState.update { state ->
                state.copy(
                    allTransactions = updatedTransactions,
                )
            }
            // After updating the list, we need to re-apply filters and grouping
            filterTransactions()
        }
        // Hide all dialogs regardless
        onDismissDialogs()
    }

    private fun filterTransactions() {
        val state = _uiState.value
        val filtered = state.allTransactions.filter { transaction ->
            val matchesFilter = when (state.selectedFilter) {
                "Ingresos" -> transaction.amount.startsWith("+")
                "Gastos" -> transaction.amount.startsWith("-")
                else -> true // "Todos"
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
        date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(this.date), // Usaremos una fecha formateada para agrupar
        type = this.type
    )
}
