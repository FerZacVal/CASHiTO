package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.category.Category
import com.cashito.domain.repositories.category.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CategoryType { INCOME, EXPENSE }

data class CategoryFormUiState(
    val categoryName: String = "",
    val categoryType: CategoryType = CategoryType.EXPENSE,
    val budget: String = "", // AÑADIDO
    val selectedIcon: String = "❓",
    val selectedColor: Color? = null,
    val isFormValid: Boolean = false,
    val categorySaved: Boolean = false,
    val categoryNameError: String? = null
)

class CategoryFormViewModel(
    private val categoryRepository: CategoryRepository // AÑADIDO
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryFormUiState())
    val uiState: StateFlow<CategoryFormUiState> = _uiState.asStateFlow()

    fun onCategoryNameChange(name: String) {
        _uiState.update { it.copy(categoryName = name, categoryNameError = null) }
        validateForm()
    }

    fun onBudgetChange(budget: String) { // AÑADIDO
        // Permitir solo números y un punto decimal
        if (budget.all { it.isDigit() || it == '.' }) {
            _uiState.update { it.copy(budget = budget) }
        }
    }

    fun onCategoryTypeChange(type: CategoryType) {
        _uiState.update { it.copy(categoryType = type) }
    }
    
    fun onIconSelected(icon: String) {
        _uiState.update { it.copy(selectedIcon = icon) }
    }

    fun onColorSelected(color: Color) {
        _uiState.update { it.copy(selectedColor = color) }
        validateForm()
    }
    
    private fun validateForm() {
        val state = _uiState.value
        val isValid = state.categoryName.isNotBlank() && state.selectedColor != null
        _uiState.update { it.copy(isFormValid = isValid) }
    }

    fun onSaveCategory() {
        val state = _uiState.value
        val nameError = if (state.categoryName.isBlank()) "El nombre es requerido" else null
        
        _uiState.update { it.copy(categoryNameError = nameError) }

        if (nameError == null && state.selectedColor != null) {
            viewModelScope.launch { // AÑADIDO: Guardar en corrutina
                val newCategory = Category(
                    id = "", // Firestore la generará
                    name = state.categoryName,
                    icon = state.selectedIcon,
                    color = "#%06X".format(0xFFFFFF and state.selectedColor.toArgb()),
                    budget = if (state.categoryType == CategoryType.EXPENSE) state.budget.toDoubleOrNull() else null
                )
                categoryRepository.addCategory(newCategory)
                _uiState.update { it.copy(categorySaved = true) }
            }
        }
    }
}
