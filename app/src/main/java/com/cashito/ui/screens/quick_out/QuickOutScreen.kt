package com.cashito.ui.screens.quick_out

import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.components.buttons.SmallButton
import com.cashito.ui.components.inputs.CashitoTextField
import com.cashito.ui.viewmodel.QuickOutCategory
import com.cashito.ui.viewmodel.QuickOutViewModel
import com.cashito.ui.theme.Radius
import com.cashito.ui.theme.Spacing

@Composable
fun QuickOutScreen(
    navController: NavController,
    viewModel: QuickOutViewModel = viewModel(),
    onNavigateBack: () -> Unit = { navController.popBackStack() }
) {
    Log.d("FlowDebug", "QuickOutScreen: Composable - INICIO de la función. Si este log no aparece, el crash ocurre en la inyección del ViewModel.")

    Log.d("FlowDebug", "QuickOutScreen: Declarando uiState...")
    val uiState by viewModel.uiState.collectAsState()
    Log.d("FlowDebug", "QuickOutScreen: uiState declarado.")

    Log.d("FlowDebug", "QuickOutScreen: Declarando LaunchedEffect...")
    LaunchedEffect(uiState.expenseConfirmed) {
        Log.d("FlowDebug", "QuickOutScreen: LaunchedEffect - INICIO del bloque. expenseConfirmed: ${uiState.expenseConfirmed}")
        if (uiState.expenseConfirmed) {
            onNavigateBack()
        }
    }
    Log.d("FlowDebug", "QuickOutScreen: LaunchedEffect declarado.")

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
                        text = "Gasto rápido",
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
                            onClick = { viewModel.onPresetAmountSelected(amount) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                Text(
                    text = "O ingresa un monto personalizado",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                CashitoTextField(
                    value = uiState.customAmount,
                    onValueChange = viewModel::onCustomAmountChanged,
                    label = "Monto personalizado",
                    placeholder = "S/ 0.00",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Spacing.xl))

                Text(
                    text = "Selecciona una categoría",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    items(uiState.categories) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = uiState.selectedCategoryId == category.id,
                            onClick = { viewModel.onCategorySelected(category.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                PrimaryButton(
                    text = "Confirmar gasto",
                    onClick = viewModel::onConfirmExpense,
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

@Composable
fun CategoryChip(
    category: QuickOutCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) category.color else category.color.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(Radius.round)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = category.icon,
                style = MaterialTheme.typography.headlineSmall,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else category.color
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = category.title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else category.color,
                textAlign = TextAlign.Center
            )
        }
    }
}
