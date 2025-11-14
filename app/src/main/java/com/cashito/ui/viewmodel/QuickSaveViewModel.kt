package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.category.Category
import com.cashito.domain.entities.income.Income
import com.cashito.domain.repositories.category.CategoryRepository
import com.cashito.domain.usecases.income.AddIncomeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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
    private val categoryRepository: CategoryRepository, // AÑADIDO
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuickSaveUiState())
    val uiState: StateFlow<QuickSaveUiState> = _uiState.asStateFlow()

    init {
        observeCategories() // CAMBIADO
        val transactionId: String? = savedStateHandle["transactionId"]
        if (transactionId != null) {
            loadIncomeForEditing(transactionId)
        }
    }

    private fun observeCategories() {
        categoryRepository.observeCategories()
            .map { domainCategories -> domainCategories.filter { it.budget == null } } // Filtramos para ingresos (sin presupuesto)
            .onEach { categories ->
                val uiCategories = categories.map { it.toQuickSaveCategory() }
                _uiState.update { it.copy(categories = uiCategories) }
            }
            .catch { /* TODO: Handle error */ }
            .launchIn(viewModelScope)
    }

    private fun loadIncomeForEditing(transactionId: String) {
        // TODO: Implementar un GetIncomeByIdUseCase
        _uiState.update {
            it.copy(
                transactionId = transactionId,
                isEditing = true,
                amount = "150.00",
                selectedCategoryId = "6"
            )
        }
        validateConfirmButton()
    }

    fun onPresetAmountSelected(preset: String) {
        _uiState.update { it.copy(amount = preset, selectedPresetAmount = preset) }
        validateConfirmButton()
    }

    fun onAmountChanged(newAmount: String) {
        _uiState.update { it.copy(amount = newAmount, selectedPresetAmount = if (it.presetAmounts.contains(newAmount)) newAmount else null) }
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
            _uiState.update { it.copy(incomeConfirmed = true) }
        } else {
            viewModelScope.launch {
                val state = _uiState.value
                val amountValue = state.amount.toDoubleOrNull() ?: 0.0
                val selectedCategory = state.categories.firstOrNull { it.id == state.selectedCategoryId }

                if (selectedCategory != null) {
                    val income = Income(
                        id = "",
                        description = selectedCategory.title,
                        amount = amountValue,
                        date = Date(),
                        category = Category(
                            id = selectedCategory.id,
                            name = selectedCategory.title,
                            icon = selectedCategory.icon,
                            color = selectedCategory.colorHex,
                            budget = null
                        )
                    )
                    addIncomeUseCase(income)
                    _uiState.update { it.copy(incomeConfirmed = true) }
                }
            }
        }
    }
}

private fun Category.toQuickSaveCategory(): QuickSaveCategory {
    val color = try {
        Color(android.graphics.Color.parseColor(this.color))
    } catch (e: Exception) {
        Color.Gray
    }
    return QuickSaveCategory(
        id = this.id,
        title = this.name,
        icon = this.icon ?: "",
        color = color,
        colorHex = this.color ?: "#808080"
    )
}
