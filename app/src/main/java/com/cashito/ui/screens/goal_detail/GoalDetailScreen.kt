
package com.cashito.ui.screens.goal_detail

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
        onNavigateToIncome = { navController.navigate("quick_save") },
        onNavigateToEdit = { goalId -> navController.navigate("goal_form?goalId=$goalId") },
        onShowMenu = viewModel::onShowMenu,
        onDeleteGoal = viewModel::deleteGoal,
        onRecurringChanged = viewModel::onRecurringChanged,
        onTransactionClick = { /* TODO */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreenContent(
    uiState: GoalDetailUiState,
    onNavigateBack: () -> Unit,
    onNavigateToIncome: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onShowMenu: (Boolean) -> Unit,
    onDeleteGoal: () -> Unit,
    onRecurringChanged: (Boolean) -> Unit,
    onTransactionClick: (GoalTransaction) -> Unit
) {
    val goal = uiState.goal

    Scaffold(
        topBar = {
            if (goal != null) {
                TopAppBar(
                    title = {
                        Text(
                            text = goal.title,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
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
                                    uiState.goal.id.let(onNavigateToEdit)
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

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        PrimaryButton(
                            text = stringResource(id = R.string.goal_detail_deposit_button),
                            onClick = onNavigateToIncome,
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            text = stringResource(id = R.string.goal_detail_edit_goal_button),
                            onClick = { uiState.goal.id.let(onNavigateToEdit) },
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
        }
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { goal.progress },
                    modifier = Modifier.fillMaxSize(),
                    color = goal.color,
                    strokeWidth = 14.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = "${(goal.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = goal.color
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                text = stringResource(id = R.string.goal_detail_saved_amount, goal.savedAmount, goal.targetAmount),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

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
                    savedAmount = "3,420",
                    targetAmount = "5,000",
                    progress = 0.65f,
                    icon = "✈️",
                    color = primaryLight,
                    targetDate = "15 Oct 2024"
                ),
                transactions = listOf(
                    GoalTransaction("1", "Ingreso automático", "Hoy, 09:30", "+S/ 200", primaryLight, "💰", primaryLight),
                    GoalTransaction("2", "Ingreso extra", "Ayer, 14:20", "+S/ 50", primaryLight, "💸", primaryLight)
                ),
                isLoading = false
            ),
            onNavigateBack = {},
            onNavigateToIncome = {},
            onNavigateToEdit = {},
            onShowMenu = {},
            onDeleteGoal = {},
            onRecurringChanged = {},
            onTransactionClick = {}
        )
    }
}
