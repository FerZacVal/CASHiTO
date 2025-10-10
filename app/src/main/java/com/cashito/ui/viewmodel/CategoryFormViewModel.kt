package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class CategoryType { INCOME, EXPENSE }

data class CategoryFormUiState(
    val categoryName: String = "",
    val categoryType: CategoryType = CategoryType.EXPENSE,
    val selectedIcon: String = "❓",
    val selectedColor: Color? = null,
    val isFormValid: Boolean = false,
    val categorySaved: Boolean = false,
    val categoryNameError: String? = null
)

class CategoryFormViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryFormUiState())
    val uiState: StateFlow<CategoryFormUiState> = _uiState.asStateFlow()

    fun onCategoryNameChange(name: String) {
        _uiState.value = _uiState.value.copy(categoryName = name, categoryNameError = null)
        validateForm()
    }

    fun onCategoryTypeChange(type: CategoryType) {
        _uiState.value = _uiState.value.copy(categoryType = type)
    }
    
    fun onIconSelected(icon: String) {
        _uiState.value = _uiState.value.copy(selectedIcon = icon)
    }

    fun onColorSelected(color: Color) {
        _uiState.value = _uiState.value.copy(selectedColor = color)
        validateForm()
    }
    
    private fun validateForm() {
        val state = _uiState.value
        val isValid = state.categoryName.isNotBlank() && state.selectedColor != null
        _uiState.value = state.copy(isFormValid = isValid)
    }

    fun onSaveCategory() {
        val state = _uiState.value
        val nameError = if (state.categoryName.isBlank()) "El nombre es requerido" else null
        
        _uiState.value = _uiState.value.copy(categoryNameError = nameError)

        if (nameError == null && state.selectedColor != null) {
            // TODO: Implement actual save logic based on categoryType
            _uiState.value = _uiState.value.copy(categorySaved = true)
        }
    }
}
