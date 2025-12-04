package com.cashito.ui.screens.quick_out

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.R
import com.cashito.domain.entities.goal.Goal
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.components.buttons.SmallButton
import com.cashito.ui.components.inputs.CashitoTextField
import com.cashito.ui.theme.Radius
import com.cashito.ui.theme.Spacing
import com.cashito.ui.viewmodel.QuickOutCategory
import com.cashito.ui.viewmodel.QuickOutUiState
import com.cashito.ui.viewmodel.QuickOutViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun QuickOutScreen(
    navController: NavController,
    viewModel: QuickOutViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSuccessPopup by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.expenseConfirmed) {
        if (uiState.expenseConfirmed) {
            showSuccessPopup = true
            delay(2000)
            navController.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        QuickOutScreenContent(
            uiState = uiState,
            onPresetAmountSelected = viewModel::onPresetAmountSelected,
            onAmountChanged = viewModel::onAmountChanged,
            onCategorySelected = viewModel::onCategorySelected,
            onConfirmExpense = viewModel::onConfirmExpense,
            onNavigateBack = { navController.popBackStack() }
        )

        if (uiState.showShortfallDialog) {
            ShortfallDialog(
                deficit = uiState.shortfallAmount,
                totalBalance = uiState.totalBalance,
                availableGoals = uiState.availableGoals,
                onSelectGoal = viewModel::onConfirmShortfallWithdrawal,
                onDismiss = viewModel::onDismissShortfallDialog
            )
        }

        AnimatedVisibility(
            visible = showSuccessPopup,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            SuccessPopup()
        }
    }
}

@Composable
fun ShortfallDialog(
    deficit: Double,
    totalBalance: Double,
    availableGoals: List<Goal>,
    onSelectGoal: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "PE"))
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Saldo insuficiente") },
        text = {
            Column {
                Text(
                    "Tu saldo libre no alcanza para cubrir este gasto. Te faltan ${format.format(deficit)}.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    "Selecciona de qué meta quieres retirar el dinero restante:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                if (availableGoals.isEmpty()) {
                     Text(
                        "No tienes metas con saldo disponible.",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(availableGoals) { goal ->
                            if (goal.savedAmount > 0) {
                                ListItem(
                                    headlineContent = { Text(goal.name) },
                                    supportingContent = { Text("Saldo: ${format.format(goal.savedAmount)}") },
                                    leadingContent = { Text(goal.icon) },
                                    modifier = Modifier
                                        .clickable { onSelectGoal(goal.id) }
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    "Advertencia: Retirar de tus metas afectará tu progreso y cancelará cualquier Boost de APR activo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
             TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
fun QuickOutScreenContent(
    uiState: QuickOutUiState,
    onPresetAmountSelected: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onConfirmExpense: () -> Unit,
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
                        text = stringResource(id = R.string.quick_out_title),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.quick_out_close_button_description),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                Text(
                    text = stringResource(id = R.string.quick_out_select_amount_title),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    items(uiState.presetAmounts) { amount ->
                        PresetAmountButton(
                            amount = "S/ $amount",
                            isSelected = uiState.selectedPresetAmount == amount,
                            onClick = { onPresetAmountSelected(amount) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                CashitoTextField(
                    value = uiState.amount,
                    onValueChange = onAmountChanged,
                    label = stringResource(id = R.string.quick_out_custom_amount_label),
                    placeholder = stringResource(id = R.string.quick_out_custom_amount_placeholder),
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Spacing.xl))

                Text(
                    text = stringResource(id = R.string.quick_out_select_category_title),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    items(uiState.categories) { category ->
                        ExpenseCategoryChip(
                            category = category,
                            isSelected = uiState.selectedCategoryId == category.id,
                            onClick = { onCategorySelected(category.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xxxl))

                PrimaryButton(
                    text = stringResource(id = R.string.quick_out_confirm_button),
                    onClick = onConfirmExpense,
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
        isPrimary = isSelected
    )
}

@Composable
fun ExpenseCategoryChip(
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

@Composable
fun SuccessPopup() {
    Card(
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CheckCircle, stringResource(id = R.string.quick_out_success_icon_description), tint = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(modifier = Modifier.width(Spacing.md))
            Text(stringResource(id = R.string.quick_out_success_message), color = MaterialTheme.colorScheme.onSecondaryContainer, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}
