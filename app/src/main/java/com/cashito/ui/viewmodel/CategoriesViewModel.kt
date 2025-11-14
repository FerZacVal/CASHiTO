package com.cashito.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.repositories.category.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.util.Locale
import com.cashito.domain.entities.category.Category as DomainCategory

// --- STATE ---

/**
 * Representa una categoría formateada para ser mostrada en la UI.
 */
data class UiCategory(
    val id: String,
    val name: String,
    val icon: String,
    val color: Color,
    val budget: String? // El presupuesto ya formateado como texto, ej: "S/ 500.00"
)

data class CategoriesUiState(
    val isLoading: Boolean = true,
    val categories: List<UiCategory> = emptyList(),
    val error: String? = null
)

// --- VIEWMODEL ---
class CategoriesViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        observeCategories()
    }

    private fun observeCategories() {
        categoryRepository.observeCategories()
            .onEach { domainCategories ->
                val uiCategories = domainCategories.map { it.toUiCategory() }
                _uiState.update { it.copy(isLoading = false, categories = uiCategories) }
            }
            .catch { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
            .launchIn(viewModelScope)
    }
}

// --- Mapper ---

private fun DomainCategory.toUiCategory(): UiCategory {
    val color = try {
        Color(android.graphics.Color.parseColor(this.color))
    } catch (e: Exception) {
        Color.Gray
    }

    val formattedBudget = this.budget?.let {
        NumberFormat.getCurrencyInstance(Locale("es", "PE")).format(it)
    }

    return UiCategory(
        id = this.id,
        name = this.name,
        icon = this.icon ?: "❓",
        color = color,
        budget = formattedBudget
    )
}
