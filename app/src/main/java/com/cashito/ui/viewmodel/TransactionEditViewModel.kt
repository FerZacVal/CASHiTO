package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.category.Category
import com.cashito.domain.entities.transaction.Transaction
import com.cashito.domain.entities.transaction.TransactionType
import com.cashito.domain.usecases.transaction.GetTransactionByIdUseCase
import com.cashito.domain.usecases.transaction.UpdateTransactionUseCase
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import com.cashito.ui.theme.tertiaryLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

// --- STATE ---
data class TransactionEditCategory(
    val id: String,
    val title: String,
    val icon: String,
    val color: Color,
    val colorHex: String
)

data class TransactionEditUiState(
    val transactionId: String? = null,
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val presetAmounts: List<String> = emptyList(),
    val categories: List<TransactionEditCategory> = emptyList(),
    val amount: String = "",
    val description: String = "",
    val selectedPresetAmount: String? = null,
    val selectedCategoryId: String = "",
    val isConfirmEnabled: Boolean = false,
    val transactionUpdated: Boolean = false,
    val screenTitle: String = "Editar Transacción",
    val isLoading: Boolean = true // Empezamos en modo carga
)

// --- VIEWMODEL ---
class TransactionEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase // AÑADIDO
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionEditUiState())
    val uiState: StateFlow<TransactionEditUiState> = _uiState.asStateFlow()

    init {
        val transactionId: String? = savedStateHandle["transactionId"]
        if (transactionId != null) {
            loadTransactionForEditing(transactionId)
        }
    }

    private fun loadTransactionForEditing(transactionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, transactionId = transactionId) }

            val transaction = getTransactionByIdUseCase(transactionId)

            if (transaction != null) {
                _uiState.update {
                    it.copy(
                        transactionType = transaction.type,
                        amount = transaction.amount.toString(),
                        description = transaction.description,
                        selectedCategoryId = transaction.category?.id ?: "",
                        screenTitle = if (transaction.type == TransactionType.EXPENSE) "Editar Gasto" else "Editar Ingreso",
                        presetAmounts = if (transaction.type == TransactionType.EXPENSE) listOf("5", "10", "20", "50") else listOf("50", "100", "500", "1000"),
                        categories = getCategoriesForType(transaction.type),
                        isLoading = false
                    )
                }
                validateConfirmButton()
            } else {
                _uiState.update { it.copy(isLoading = false) } // TODO: Handle transaction not found
            }
        }
    }
    
    private fun getCategoriesForType(type: TransactionType): List<TransactionEditCategory> {
        return if (type == TransactionType.EXPENSE) {
             listOf(
                TransactionEditCategory("1", "Comida", "🍔", primaryLight, "#FF6F00"),
                TransactionEditCategory("2", "Transporte", "🚌", secondaryLight, "#FFAB00"),
                TransactionEditCategory("3", "Compras", "🛒", tertiaryLight, "#00BFA5"),
                TransactionEditCategory("4", "Ocio", "🎉", Color(0xFFF59E0B), "#F59E0B")
            )
        } else {
            listOf(
                TransactionEditCategory("5", "Nómina", "💼", primaryLight, "#FF6F00"),
                TransactionEditCategory("6", "Ventas", "📈", secondaryLight, "#FFAB00"),
                TransactionEditCategory("7", "Freelance", "💻", tertiaryLight, "#00BFA5"),
                TransactionEditCategory("8", "Regalo", "🎁", Color(0xFF10B981), "#10B981")
            )
        }
    }

    fun onAmountChanged(newAmount: String) {
        _uiState.update { it.copy(amount = newAmount) }
        validateConfirmButton()
    }
    
    fun onDescriptionChanged(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
        validateConfirmButton()
    }

    fun onCategorySelected(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
        validateConfirmButton()
    }

    private fun validateConfirmButton() {
        _uiState.update { state ->
            val amountValue = state.amount.toDoubleOrNull() ?: 0.0
            val isEnabled = amountValue > 0 && state.selectedCategoryId.isNotEmpty() && state.description.isNotBlank()
            state.copy(isConfirmEnabled = isEnabled)
        }
    }

    fun onConfirmUpdate() {
        if (!_uiState.value.isConfirmEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(isConfirmEnabled = false) }

            val state = _uiState.value
            val transactionId = state.transactionId
            val selectedCategory = state.categories.firstOrNull { it.id == state.selectedCategoryId }

            if (transactionId != null && selectedCategory != null) {
                val updatedTransaction = Transaction(
                    id = transactionId,
                    description = state.description,
                    amount = state.amount.toDoubleOrNull() ?: 0.0,
                    date = Date(), // La fecha no es editable en esta UI, por lo que no se modifica.
                    category = Category(
                        id = selectedCategory.id,
                        name = selectedCategory.title,
                        icon = selectedCategory.icon,
                        color = selectedCategory.colorHex
                    ),
                    type = state.transactionType
                )
                updateTransactionUseCase(transactionId, updatedTransaction)
                _uiState.update { it.copy(transactionUpdated = true) }
            }
        }
    }
}