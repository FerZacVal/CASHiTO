package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.category.Category
import com.cashito.domain.usecases.category.GetCategoryByIdUseCase
import com.cashito.domain.usecases.category.UpdateCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- STATE ---
data class CategoryEditUiState(
    val categoryId: String = "",
    val categoryName: String = "",
    val categoryIcon: String = "",
    val categoryColor: Color = Color.Gray,
    val budgetInput: String = "",
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null
)

// --- VIEWMODEL ---
class CategoryEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase
) : ViewModel() {

    private val categoryId: String = checkNotNull(savedStateHandle["categoryId"])

    private val _uiState = MutableStateFlow(CategoryEditUiState())
    val uiState: StateFlow<CategoryEditUiState> = _uiState.asStateFlow()

    private var originalCategory: Category? = null

    init {
        loadCategoryDetails()
    }

    private fun loadCategoryDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val category = getCategoryByIdUseCase(categoryId)
            if (category != null) {
                originalCategory = category
                val budgetValue = category.budget?.let { if (it % 1 == 0.0) it.toInt().toString() else it.toString() } ?: ""
                _uiState.update {
                    it.copy(
                        categoryId = category.id,
                        categoryName = category.name,
                        categoryIcon = category.icon ?: "❓",
                        categoryColor = try { Color(android.graphics.Color.parseColor(category.color)) } catch (e: Exception) { Color.Gray },
                        budgetInput = budgetValue,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Categoría no encontrada") }
            }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(categoryName = newName) }
    }

    fun onIconChange(newIcon: String) {
        _uiState.update { it.copy(categoryIcon = newIcon) }
    }

    fun onColorChange(newColor: Color) {
        _uiState.update { it.copy(categoryColor = newColor) }
    }

    fun onBudgetChange(newBudget: String) {
        if (newBudget.all { it.isDigit() || it == '.' }) {
            _uiState.update { it.copy(budgetInput = newBudget) }
        }
    }

    fun onSave() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val original = originalCategory ?: return@launch

            val updatedCategory = original.copy(
                name = currentState.categoryName,
                icon = currentState.categoryIcon,
                color = "#%06X".format(0xFFFFFF and currentState.categoryColor.toArgb()),
                budget = currentState.budgetInput.toDoubleOrNull()
            )

            updateCategoryUseCase(updatedCategory)
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
