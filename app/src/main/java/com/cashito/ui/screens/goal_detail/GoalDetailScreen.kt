package com.cashito.ui.screens.goal_detail

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.R
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.components.buttons.SecondaryButton
import com.cashito.ui.components.list.CashitoListItem
import com.cashito.ui.theme.CASHiTOTheme
import com.cashito.ui.theme.Spacing
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.viewmodel.GoalDetail
import com.cashito.ui.viewmodel.GoalDetailUiState
import com.cashito.ui.viewmodel.GoalDetailViewModel
import com.cashito.ui.viewmodel.GoalTransaction
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.log10

@Composable
fun GoalDetailScreen(
    navController: NavController,
    viewModel: GoalDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.goalDeleted) {
        if (uiState.goalDeleted) {
            navController.popBackStack()
        }
    }

    GoalDetailScreenContent(
        uiState = uiState,
        onNavigateBack = { navController.popBackStack() },
        onNavigateToIncome = { goalId -> navController.navigate("quick_save?goalId=$goalId") }, 
        onNavigateToEdit = { goalId -> navController.navigate("goal_form?goalId=$goalId") },
        onShowMenu = viewModel::onShowMenu,
        onDeleteGoal = viewModel::deleteGoal,
        onRecurringChanged = viewModel::onRecurringChanged,
        onShowBoostDetails = viewModel::onShowBoostDetails,
        onTransactionClick = { transaction -> navController.navigate("transaction_edit/${transaction.id}") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreenContent(
    uiState: GoalDetailUiState,
    onNavigateBack: () -> Unit,
    onNavigateToIncome: (String) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onShowMenu: (Boolean) -> Unit,
    onDeleteGoal: () -> Unit,
    onRecurringChanged: (Boolean) -> Unit,
    onShowBoostDetails: (Boolean) -> Unit,
    onTransactionClick: (GoalTransaction) -> Unit
) {
    val goal = uiState.goal

    Scaffold(
        topBar = {
            if (goal != null) {
                TopAppBar(
                    title = { Text(goal.title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.goal_detail_back_button_description))
                        }
                    },
                    actions = {
                        IconButton(onClick = { onShowMenu(true) }) {
                            Icon(Icons.Default.MoreVert, contentDescription = stringResource(id = R.string.goal_detail_more_button_description))
                        }
                        DropdownMenu(
                            expanded = uiState.showMenu,
                            onDismissRequest = { onShowMenu(false) }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.goal_detail_edit_goal_menu_item)) },
                                onClick = {
                                    onShowMenu(false)
                                    onNavigateToEdit(goal.id)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.goal_detail_delete_goal_menu_item), color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    onShowMenu(false)
                                    onDeleteGoal()
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading || goal == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                item { GoalSummary(goal = goal) }

                // Sección de Boost Activo
                if (goal.activeBoostApr != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onShowBoostDetails(true) },
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFE082) // Color dorado para el boost
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.md),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = Color(0xFF5D4037),
                                    modifier = Modifier.size(32.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Boost Activo: +${goal.activeBoostApr}% APR",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF5D4037)
                                    )
                                    if (goal.boostExpiryDate != null) {
                                        Text(
                                            text = "Expira el: ${goal.boostExpiryDate}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF5D4037).copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                // Indicador de click
                                Text("ℹ\uFE0F", fontSize = androidx.compose.ui.unit.TextUnit.Unspecified)
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        PrimaryButton(
                            text = stringResource(id = R.string.goal_detail_deposit_button),
                            onClick = { onNavigateToIncome(goal.id) },
                            // ARREGLADO: El botón se deshabilita si la meta ya se ha completado (100% o más).
                            enabled = goal.progress < 1.0f,
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            text = stringResource(id = R.string.goal_detail_edit_goal_button),
                            onClick = { onNavigateToEdit(goal.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item { 
                    SavingsPlanSection(
                        isRecurringEnabled = uiState.isRecurringEnabled,
                        onRecurringChanged = onRecurringChanged
                    )
                }

                item {
                    Text(
                        text = stringResource(id = R.string.goal_detail_contributions_history_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(uiState.transactions) { transaction ->
                     TransactionItem(transaction = transaction, onClick = { onTransactionClick(transaction) })
                }
            }
            
            // Diálogo de detalles del Boost
            if (uiState.showBoostDetails && goal.activeBoostApr != null) {
                BoostDetailsDialog(
                    goal = goal,
                    onDismiss = { onShowBoostDetails(false) }
                )
            }
        }
    }
}

@Composable
fun BoostDetailsDialog(
    goal: GoalDetail,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalles del Boost") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                DetailRow("APR Aplicado:", "${goal.activeBoostApr}%")
                // No tenemos el monto base original guardado, pero podemos inferir que el beneficio se calculó sobre el monto que había.
                // Sin embargo, lo más importante es mostrar la ganancia FINAL garantizada.
                
                if (goal.activeBoostProfit != null) {
                    DetailRow("Ganancia Garantizada:", NumberFormat.getCurrencyInstance(Locale("es", "PE")).format(goal.activeBoostProfit))
                }
                
                if (goal.boostExpiryDate != null) {
                    DetailRow("Fecha de Acreditación:", goal.boostExpiryDate)
                }
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    "Nota: Este beneficio ya está calculado y es fijo. Nuevos aportes a esta meta no modificarán este monto.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Entendido")
            }
        }
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun GoalSummary(goal: GoalDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Box(contentAlignment = Alignment.Center) {
                DonutChart(
                    progress = goal.progress,
                    color = goal.color,
                    modifier = Modifier.size(180.dp)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(goal.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = goal.color
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = stringResource(
                            id = R.string.goal_detail_progress_label,
                            formatNumber(goal.savedAmount),
                            formatNumber(goal.targetAmount)
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Text(
                text = stringResource(id = R.string.goal_detail_target_date, goal.targetDate),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DonutChart(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 24f
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier) {
        drawArc(
            color = trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = progress * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

private fun formatNumber(number: Double): String {
    if (number < 1000) return number.toInt().toString()
    val exp = (log10(number) / 3.0).toInt()
    val suffix = "kMGTPE"[exp - 1]
    val value = number / Math.pow(10.0, (exp * 3).toDouble())
    return String.format("%.1f%c", value, suffix)
}

@Composable
private fun SavingsPlanSection(isRecurringEnabled: Boolean, onRecurringChanged: (Boolean) -> Unit) {
    Column {
        Text(text = stringResource(id = R.string.goal_detail_savings_plan_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = Spacing.sm), color = MaterialTheme.colorScheme.onSurface)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            CashitoListItem(
                headline = stringResource(id = R.string.goal_detail_recurring_deposit_title),
                supportingText = stringResource(id = R.string.goal_detail_recurring_deposit_subtitle),
                trailingContent = { Switch(checked = isRecurringEnabled, onCheckedChange = onRecurringChanged) },
                onClick = { onRecurringChanged(!isRecurringEnabled) }
            )
        }
    }
}

@Composable
private fun TransactionItem(transaction: GoalTransaction, onClick: () -> Unit) {
    CashitoListItem(
        headline = transaction.title,
        supportingText = transaction.subtitle,
        leadingIcon = null, // TODO: Map transaction type to icon
        onClick = onClick,
        trailingContent = {
            Text(transaction.amount, color = transaction.amountColor, fontWeight = FontWeight.SemiBold)
        }
    )
    HorizontalDivider()
}

@Preview(showBackground = true)
@Composable
fun GoalDetailScreenPreview() {
    CASHiTOTheme {
        GoalDetailScreenContent(
            uiState = GoalDetailUiState(
                goal = GoalDetail(
                    id = "1",
                    title = "Viaje a Cusco",
                    savedAmount = 3420.0,
                    targetAmount = 5000.0,
                    progress = 0.68f,
                    icon = "✈️",
                    color = primaryLight,
                    targetDate = "15 Oct 2024"
                ),
                transactions = listOf(
                    GoalTransaction("1", "Ingreso automático", "Hoy, 09:30", "+S/ 200", primaryLight, "💰", primaryLight)
                ),
                isLoading = false
            ),
            onNavigateBack = {},
            onNavigateToIncome = {},
            onNavigateToEdit = {},
            onShowMenu = {},
            onDeleteGoal = {},
            onRecurringChanged = {},
            onTransactionClick = {},
            onShowBoostDetails = {}
        )
    }
}
