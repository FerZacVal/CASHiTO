package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.category.Category
import com.cashito.domain.entities.income.Income
import com.cashito.domain.usecases.income.AddIncomeUseCase
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
data class QuickSaveCategory(
    val id: String,
    val title: String,
    val icon: String,
    val color: Color,
    val colorHex: String
)

data class QuickSaveUiState(
    val transactionId: String? = null,
    val isEditing: Boolean = false,
    val presetAmounts: List<String> = listOf("50", "100", "500", "1000"),
    val categories: List<QuickSaveCategory> = emptyList(),
    val amount: String = "",
    val selectedPresetAmount: String? = null,
    val selectedCategoryId: String = "",
    val isConfirmEnabled: Boolean = false,
    val incomeConfirmed: Boolean = false
)

// --- VIEWMODEL ---
class QuickSaveViewModel(
    private val addIncomeUseCase: AddIncomeUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuickSaveUiState())
    val uiState: StateFlow<QuickSaveUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        val transactionId: String? = savedStateHandle["transactionId"]
        if (transactionId != null) {
            loadIncomeForEditing(transactionId)
        }
    }

    private fun loadCategories() {
        _uiState.update {
            it.copy(
                categories = listOf(
                    QuickSaveCategory("5", "Nómina", "💼", primaryLight, "#FF6F00"),
                    QuickSaveCategory("6", "Ventas", "📈", secondaryLight, "#FFAB00"),
                    QuickSaveCategory("7", "Freelance", "💻", tertiaryLight, "#00BFA5"),
                    QuickSaveCategory("8", "Regalo", "🎁", Color(0xFF10B981), "#10B981")
                )
            )
        }
    }

    private fun loadIncomeForEditing(transactionId: String) {
        // TODO: Implementar un GetIncomeByIdUseCase para obtener los datos reales
        _uiState.update {
            it.copy(
                transactionId = transactionId,
                isEditing = true,
                amount = "150.00", // Dato de ejemplo
                selectedCategoryId = "6" // ID de "Ventas", dato de ejemplo
            )
        }
        validateConfirmButton() // Validar el botón después de cargar
    }

    fun onPresetAmountSelected(preset: String) {
        _uiState.update {
            it.copy(
                amount = preset,
                selectedPresetAmount = preset
            )
        }
        validateConfirmButton()
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

    fun onCategorySelected(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
        validateConfirmButton()
    }

    private fun validateConfirmButton() {
        _uiState.update { state ->
            val amountValue = state.amount.toDoubleOrNull() ?: 0.0
            val isEnabled = amountValue > 0 && state.selectedCategoryId.isNotEmpty()
            state.copy(isConfirmEnabled = isEnabled)
        }
    }

    fun onConfirmIncome() {
        if (!_uiState.value.isConfirmEnabled) return

        _uiState.update { it.copy(isConfirmEnabled = false) }

        if (_uiState.value.isEditing) {
            // TODO: Llamar a UpdateIncomeUseCase
            _uiState.update { it.copy(incomeConfirmed = true) } // Simular éxito por ahora
        } else {
            viewModelScope.launch {
                val state = _uiState.value
                val amountValue = state.amount.toDoubleOrNull() ?: 0.0
                val selectedCategory = state.categories.firstOrNull { it.id == state.selectedCategoryId }

                if (selectedCategory != null) {
                    val income = Income(
                        id = "", // Firestore will generate the ID
                        description = selectedCategory.title,
                        amount = amountValue,
                        date = Date(),
                        category = Category(
                            id = selectedCategory.id,
                            name = selectedCategory.title,
                            icon = selectedCategory.icon,
                            color = selectedCategory.colorHex
                        )
                    )
                    addIncomeUseCase(income)
                    _uiState.update { it.copy(incomeConfirmed = true) }
                }
            }
        }
    }
}
