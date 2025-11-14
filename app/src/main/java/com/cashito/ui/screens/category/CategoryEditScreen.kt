package com.cashito.ui.screens.category

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.components.inputs.CashitoTextField
import com.cashito.ui.screens.goal_form.getGoalColors
import com.cashito.ui.screens.goal_form.getGoalIcons
import com.cashito.ui.theme.ComponentSize
import com.cashito.ui.theme.Radius
import com.cashito.ui.theme.Spacing
import com.cashito.ui.viewmodel.CategoryEditUiState
import com.cashito.ui.viewmodel.CategoryEditViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CategoryEditScreen(
    navController: NavController,
    viewModel: CategoryEditViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.popBackStack()
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        CategoryEditScreenContent(
            uiState = uiState,
            onNameChange = viewModel::onNameChange,
            onIconChange = viewModel::onIconChange,
            onColorChange = viewModel::onColorChange,
            onBudgetChange = viewModel::onBudgetChange,
            onSave = viewModel::onSave,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

@Composable
fun CategoryEditScreenContent(
    uiState: CategoryEditUiState,
    onNameChange: (String) -> Unit,
    onIconChange: (String) -> Unit,
    onColorChange: (Color) -> Unit,
    onBudgetChange: (String) -> Unit,
    onSave: () -> Unit,
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(Radius.lg)
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
                    Text("Editar Categoría", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                CashitoTextField(
                    value = uiState.categoryName,
                    onValueChange = onNameChange,
                    label = "Nombre de la categoría",
                    placeholder = "Ej: 500",
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                CashitoTextField(
                    value = uiState.budgetInput,
                    onValueChange = onBudgetChange,
                    label = "Presupuesto mensual (opcional)",
                    placeholder = "Ej: 500",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Spacing.lg))
                Text("Icono", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(Spacing.sm))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    items(categoryIcons) { icon ->
                        IconSelectionButton(icon = icon, isSelected = uiState.categoryIcon == icon, onClick = { onIconChange(icon) })
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.lg))
                Text("Color", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(Spacing.sm))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    items(categoryColors) { color ->
                        ColorSelectionButton(color = color, isSelected = uiState.categoryColor == color, onClick = { onColorChange(color) })
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xxxl))

                PrimaryButton(text = "Guardar Cambios", onClick = onSave)
            }
        }
    }
}

@Composable
private fun IconSelectionButton(icon: String, isSelected: Boolean, onClick: () -> Unit) {
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
private fun ColorSelectionButton(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(ComponentSize.iconSize * 2)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(ComponentSize.iconSize)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f))
            )
        }
    }
}
