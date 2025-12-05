package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.category.Category
import com.cashito.domain.entities.expense.Expense
import com.cashito.domain.entities.goal.Goal
import com.cashito.domain.repositories.category.CategoryRepository
import com.cashito.domain.usecases.expense.AddExpenseUseCase
import com.cashito.domain.usecases.expense.InsufficientFreeBalanceException
import com.cashito.domain.usecases.goal.GetGoalsUseCase
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
data class QuickOutCategory(
    val id: String,
    val title: String,
    val icon: String,
    val color: Color,
    val colorHex: String
)

data class QuickOutUiState(
    val transactionId: String? = null,
    val isEditing: Boolean = false,
    val presetAmounts: List<String> = listOf("5", "10", "20", "50"),
    val categories: List<QuickOutCategory> = emptyList(),
    val amount: String = "",
    val selectedPresetAmount: String? = null,
    val selectedCategoryId: String = "",
    val isConfirmEnabled: Boolean = false,
    val expenseConfirmed: Boolean = false,
    // Shortfall State
    val showShortfallDialog: Boolean = false,
    val shortfallAmount: Double = 0.0,
    val totalBalance: Double = 0.0,
    val availableGoals: List<Goal> = emptyList(),
    val pendingExpense: Expense? = null
)

// --- VIEWMODEL ---
class QuickOutViewModel(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val categoryRepository: CategoryRepository,
    private val getGoalsUseCase: GetGoalsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuickOutUiState())
    val uiState: StateFlow<QuickOutUiState> = _uiState.asStateFlow()

    init {
        observeCategories()
        observeGoals()
        val transactionId: String? = savedStateHandle["transactionId"]
        if (transactionId != null) {
            loadExpenseForEditing(transactionId)
        }
    }

    private fun observeCategories() {
        categoryRepository.observeCategories()
            .map { domainCategories -> domainCategories.filter { it.budget != null } } // Filtramos para gastos
            .onEach { categories ->
                val uiCategories = categories.map { it.toQuickOutCategory() }
                _uiState.update { it.copy(categories = uiCategories) }
            }
            .catch { /* TODO: Handle error */ }
            .launchIn(viewModelScope)
    }
    
    private fun observeGoals() {
        getGoalsUseCase()
            .onEach { goals -> _uiState.update { it.copy(availableGoals = goals) } }
            .launchIn(viewModelScope)
    }

    private fun loadExpenseForEditing(transactionId: String) {
        // TODO: Implementar un GetExpenseByIdUseCase para obtener los datos reales
        _uiState.update {
            it.copy(
                transactionId = transactionId,
                isEditing = true,
                amount = "25.00",
                selectedCategoryId = "2"
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

    fun onConfirmExpense() {
        if (!_uiState.value.isConfirmEnabled) return
        
        if (_uiState.value.isEditing) {
            // TODO: Llamar a UpdateExpenseUseCase
            _uiState.update { it.copy(expenseConfirmed = true) }
        } else {
            viewModelScope.launch {
                _uiState.update { it.copy(isConfirmEnabled = false) } // Disable button to prevent double click
                val state = _uiState.value
                val amountValue = state.amount.toDoubleOrNull() ?: 0.0
                val selectedCategory = state.categories.firstOrNull { it.id == state.selectedCategoryId }

                if (selectedCategory != null) {
                    val expense = Expense(
                        id = "",
                        description = selectedCategory.title,
                        amount = amountValue,
                        date = Date(),
                        category = Category(
                            id = selectedCategory.id,
                            name = selectedCategory.title,
                            icon = selectedCategory.icon,
                            color = selectedCategory.colorHex,
                            budget = 0.0 
                        )
                    )
                    
                    try {
                        addExpenseUseCase(expense)
                        _uiState.update { it.copy(expenseConfirmed = true) }
                    } catch (e: InsufficientFreeBalanceException) {
                        // Shortfall detected!
                        _uiState.update { 
                            it.copy(
                                showShortfallDialog = true,
                                shortfallAmount = e.deficit,
                                totalBalance = e.totalBalance,
                                pendingExpense = expense,
                                isConfirmEnabled = true // Re-enable if user cancels dialog
                            ) 
                        }
                    } catch (e: Exception) {
                        // Generic error
                         _uiState.update { it.copy(isConfirmEnabled = true) }
                    }
                }
            }
        }
    }
    
    fun onConfirmShortfallWithdrawal(goalId: String) {
        val state = _uiState.value
        val expense = state.pendingExpense ?: return
        val deficit = state.shortfallAmount
        
        viewModelScope.launch {
            try {
                addExpenseUseCase.addExpenseWithWithdrawal(expense, goalId, deficit)
                _uiState.update { 
                    it.copy(
                        expenseConfirmed = true, 
                        showShortfallDialog = false,
                        pendingExpense = null
                    ) 
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun onDismissShortfallDialog() {
        _uiState.update { it.copy(showShortfallDialog = false, pendingExpense = null) }
    }
}

private fun Category.toQuickOutCategory(): QuickOutCategory {
    val color = try {
        Color(android.graphics.Color.parseColor(this.color))
    } catch (e: Exception) {
        Color.Gray
    }
    return QuickOutCategory(
        id = this.id,
        title = this.name,
        icon = this.icon ?: "",
        color = color,
        colorHex = this.color ?: "#808080"
    )
}
