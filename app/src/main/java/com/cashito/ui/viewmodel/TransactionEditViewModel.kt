package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.transaction.TransactionType
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
    val screenTitle: String = "Editar Gasto"
)

// --- VIEWMODEL ---
class TransactionEditViewModel(
    savedStateHandle: SavedStateHandle
    // Use cases will be added later
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
        // TODO: Implement GetTransactionByIdUseCase to get real data
        // For now, using dummy data. Let's assume it's an EXPENSE
        val type = TransactionType.EXPENSE // or INCOME based on what is passed.
        
        _uiState.update {
            it.copy(
                transactionId = transactionId,
                transactionType = type,
                amount = "123.45",
                description = "Gasto de prueba",
                selectedCategoryId = "2",
                screenTitle = if (type == TransactionType.EXPENSE) "Editar Gasto" else "Editar Ingreso",
                presetAmounts = if (type == TransactionType.EXPENSE) listOf("5", "10", "20", "50") else listOf("50", "100", "500", "1000"),
                categories = getCategoriesForType(type)
            )
        }
        validateConfirmButton()
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
        _uiState.update {
            it.copy(
                amount = newAmount,
                selectedPresetAmount = if (it.presetAmounts.contains(newAmount)) newAmount else null
            )
        }
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
        _uiState.update { it.copy(isConfirmEnabled = false) }
        // TODO: Call UpdateTransactionUseCase
        _uiState.update { it.copy(transactionUpdated = true) }
    }
}