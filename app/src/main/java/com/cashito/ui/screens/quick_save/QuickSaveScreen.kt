package com.cashito.ui.screens.quick_save

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.components.buttons.SmallButton
import com.cashito.ui.components.chips.SelectionChip
import com.cashito.ui.components.inputs.CashitoTextField
import com.cashito.ui.theme.CASHiTOTheme
import com.cashito.ui.theme.Radius
import com.cashito.ui.theme.Spacing
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import com.cashito.ui.viewmodel.IncomeCategory
import com.cashito.ui.viewmodel.QuickSaveGoal
import com.cashito.ui.viewmodel.QuickSaveUiState
import com.cashito.ui.viewmodel.QuickSaveViewModel

@Composable
fun QuickSaveScreen(
    navController: NavController,
    viewModel: QuickSaveViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val onNavigateBack: () -> Unit = { navController.popBackStack() }

    LaunchedEffect(uiState.incomeConfirmed) {
        if (uiState.incomeConfirmed) {
            onNavigateBack()
        }
    }
    
    QuickSaveScreenContent(
        uiState = uiState,
        onPresetAmountSelected = viewModel::onPresetAmountSelected,
        onCustomAmountChanged = viewModel::onCustomAmountChanged,
        onCategorySelected = viewModel::onCategorySelected,
        onGoalSelected = viewModel::onGoalSelected,
        onConfirmIncome = viewModel::onConfirmIncome,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun QuickSaveScreenContent(
    uiState: QuickSaveUiState,
    onPresetAmountSelected: (String) -> Unit,
    onCustomAmountChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onGoalSelected: (String) -> Unit,
    onConfirmIncome: () -> Unit,
    onNavigateBack: () -> Unit
) {
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
            shape = RoundedCornerShape(Radius.lg),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ingreso rápido",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                Text(
                    text = "Selecciona un monto",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    items(uiState.presetAmounts) { amount ->
                        PresetAmountButton(
                            amount = "S/ $amount",
                            isSelected = uiState.selectedAmount == amount,
                            onClick = { onPresetAmountSelected(amount) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                CashitoTextField(
                    value = uiState.customAmount,
                    onValueChange = onCustomAmountChanged,
                    label = "Monto personalizado",
                    placeholder = "S/ 0.00",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Spacing.xl))

                Text(
                    text = "Selecciona una categoría de ingreso",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    items(uiState.categories) { category ->
                        SelectionChip(
                            title = category.title,
                            icon = category.icon,
                            color = category.color,
                            isSelected = uiState.selectedCategoryId == category.id,
                            onClick = { onCategorySelected(category.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                Text(
                    text = "Asignar a una meta (Opcional)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    items(uiState.goals) { goal ->
                        SelectionChip(
                            title = goal.title,
                            icon = goal.icon,
                            color = goal.color,
                            isSelected = uiState.selectedGoalId == goal.id,
                            onClick = { onGoalSelected(goal.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xxxl))

                PrimaryButton(
                    text = "Confirmar ingreso",
                    onClick = onConfirmIncome,
                    enabled = uiState.isConfirmEnabled
                )
            }
        }
    }
}

@Composable
fun PresetAmountButton(
    amount: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    SmallButton(
        text = amount,
        onClick = onClick,
        isPrimary = isSelected,
        modifier = Modifier.width(80.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun QuickSaveScreenPreview() {
    CASHiTOTheme {
        QuickSaveScreenContent(
            uiState = QuickSaveUiState(
                presetAmounts = listOf("5", "10", "20", "50"),
                goals = listOf(
                    QuickSaveGoal("1", "Viaje a Cusco", "✈️", primaryLight),
                    QuickSaveGoal("2", "Laptop nueva", "💻", secondaryLight)
                ),
                categories = listOf(
                    IncomeCategory("1", "Trabajo", "💼", primaryLight),
                    IncomeCategory("2", "Freelance", "✍️", secondaryLight),
                ),
                selectedAmount = "20",
                selectedCategoryId = "1",
                isConfirmEnabled = true
            ),
            onPresetAmountSelected = {},
            onCustomAmountChanged = {},
            onCategorySelected = {},
            onGoalSelected = {},
            onConfirmIncome = {},
            onNavigateBack = {}
        )
    }
}
