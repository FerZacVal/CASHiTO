package com.cashito.ui.screens.category_form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.components.inputs.CashitoTextField
import com.cashito.ui.screens.goal_form.getGoalColors
import com.cashito.ui.screens.goal_form.getGoalIcons
import com.cashito.ui.theme.CASHiTOTheme
import com.cashito.ui.theme.ComponentSize
import com.cashito.ui.theme.Radius
import com.cashito.ui.theme.Spacing
import com.cashito.ui.viewmodel.CategoryFormUiState
import com.cashito.ui.viewmodel.CategoryFormViewModel
import com.cashito.ui.viewmodel.CategoryType

@Composable
fun CategoryFormScreen(
    navController: NavController,
    viewModel: CategoryFormViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.categorySaved) {
        if (uiState.categorySaved) {
            navController.popBackStack()
        }
    }

    CategoryFormScreenContent(
        uiState = uiState,
        onCategoryNameChange = viewModel::onCategoryNameChange,
        onCategoryTypeChange = viewModel::onCategoryTypeChange,
        onIconSelected = viewModel::onIconSelected,
        onColorSelected = viewModel::onColorSelected,
        onSaveCategory = viewModel::onSaveCategory,
        onNavigateBack = { navController.popBackStack() }
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreenContent(
    uiState: CategoryFormUiState,
    onCategoryNameChange: (String) -> Unit,
    onCategoryTypeChange: (CategoryType) -> Unit,
    onIconSelected: (String) -> Unit,
    onColorSelected: (Color) -> Unit,
    onSaveCategory: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val categoryIcons = getGoalIcons()
    val categoryColors = getGoalColors()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(Radius.lg),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.lg)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Nueva Categoría", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        shape = RoundedCornerShape(topStart = Radius.round, bottomStart = Radius.round),
                        selected = uiState.categoryType == CategoryType.INCOME,
                        onClick = { onCategoryTypeChange(CategoryType.INCOME) },
                        label = { Text("Ingreso") }
                    )
                    SegmentedButton(
                        shape = RoundedCornerShape(topEnd = Radius.round, bottomEnd = Radius.round),
                        selected = uiState.categoryType == CategoryType.EXPENSE,
                        onClick = { onCategoryTypeChange(CategoryType.EXPENSE) },
                        label = { Text("Gasto") }
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.xl))
                
                CashitoTextField(
                    value = uiState.categoryName,
                    onValueChange = onCategoryNameChange,
                    label = "Nombre de la categoría",
                    placeholder = "Ej: Comida, Sueldo, etc.",
                    isError = uiState.categoryNameError != null,
                    errorMessage = uiState.categoryNameError
                )

                Spacer(modifier = Modifier.height(Spacing.lg))
                Text("Icono", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(Spacing.sm))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    items(categoryIcons) { icon ->
                        IconSelectionButton(icon = icon, isSelected = uiState.selectedIcon == icon, onClick = { onIconSelected(icon) })
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.lg))
                Text("Color", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(Spacing.sm))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    items(categoryColors) { color ->
                        ColorSelectionButton(color = color, isSelected = uiState.selectedColor == color, onClick = { onColorSelected(color) })
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.xxxl))
                
                PrimaryButton(text = "Guardar Categoría", onClick = onSaveCategory, enabled = uiState.isFormValid)
            }
        }
    }
}

@Composable
fun IconSelectionButton(icon: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(ComponentSize.iconSize * 2)
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = icon, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun ColorSelectionButton(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(ComponentSize.iconSize * 2)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(modifier = Modifier.size(ComponentSize.iconSize).clip(CircleShape).background(Color.White.copy(alpha = 0.5f)))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryFormScreenPreview() {
    CASHiTOTheme {
        CategoryFormScreenContent(
            uiState = CategoryFormUiState(categoryType = CategoryType.EXPENSE, isFormValid = true),
            onCategoryNameChange = {},
            onCategoryTypeChange = {},
            onIconSelected = {},
            onColorSelected = {},
            onSaveCategory = {},
            onNavigateBack = {}
        )
    }
}
