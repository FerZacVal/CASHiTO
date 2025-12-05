package com.cashito.ui.screens.dashboard

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.R
import com.cashito.Routes
import com.cashito.domain.entities.gamification.WeeklyChallenge
import com.cashito.ui.components.cards.GoalCard
import com.cashito.ui.components.cards.HeroCard
import com.cashito.ui.components.navigation.CashitoBottomNavigation
import com.cashito.ui.theme.CASHiTOTheme
import com.cashito.ui.theme.ComponentSize
import com.cashito.ui.theme.Spacing
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import com.cashito.ui.viewmodel.DashboardGoal
import com.cashito.ui.viewmodel.DashboardTransaction
import com.cashito.ui.viewmodel.DashboardUiState
import com.cashito.ui.viewmodel.DashboardViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = koinViewModel() // CORREGIDO
) {
    val uiState by viewModel.uiState.collectAsState()

    DashboardScreenContent(
        uiState = uiState,
        onGoalClick = { goalId -> navController.navigate("goal_detail/$goalId") },
        onTransactionsClick = { navController.navigate(Routes.TRANSACTIONS) },
        onReportsClick = { navController.navigate(Routes.REPORTS) },
        onGoalsClick = { navController.navigate(Routes.GOALS) },
        onProfileClick = { navController.navigate(Routes.PROFILE) },
        onQuickSaveClick = { navController.navigate(Routes.QUICK_SAVE) },
        onQuickOutClick = { navController.navigate(Routes.QUICK_OUT) },
        onAddCategoryClick = { navController.navigate(Routes.CATEGORY_FORM) },
        onCreateGoalClick = { navController.navigate(Routes.GOAL_FORM) },
        onNotificationClick = { /* TODO */ },
        onTransactionItemClick = { /* TODO */ },
        onCategoriesClick = { navController.navigate(Routes.CATEGORIES) },
        onRewardsClick = { navController.navigate(Routes.REWARDS) }
    )
}

@Composable
fun DashboardScreenContent(
    uiState: DashboardUiState,
    onGoalClick: (String) -> Unit,
    onTransactionsClick: () -> Unit,
    onReportsClick: () -> Unit,
    onGoalsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onQuickSaveClick: () -> Unit,
    onQuickOutClick: () -> Unit,
    onAddCategoryClick: () -> Unit,
    onCreateGoalClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onTransactionItemClick: (DashboardTransaction) -> Unit,
    onCategoriesClick: () -> Unit,
    onRewardsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            DashboardTopBar(
                userName = uiState.userName,
                onNotificationClick = onNotificationClick,
                onSettingsClick = onProfileClick,
                onRewardsClick = onRewardsClick
            )
        },
        bottomBar = {
            CashitoBottomNavigation(
                currentRoute = "dashboard",
                onNavigate = { route ->
                    when (route) {
                        "dashboard" -> { /* Already on this screen */ }
                        "transactions" -> onTransactionsClick()
                        "reports" -> onReportsClick()
                        "goals" -> onGoalsClick()
                        "profile" -> onProfileClick()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCategoryClick,
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.dashboard_add_category_button_description))
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg)
            ) {
                item {
                    // ACTUALIZADO: Ahora usamos los 3 balances
                    HeroCard(
                        totalBalance = uiState.totalBalance,
                        freeBalance = uiState.freeBalance,
                        goalsBalance = uiState.goalsBalance,
                        onIncomeClick = onQuickSaveClick,
                        onExpenseClick = onQuickOutClick
                    )
                }

                item { Spacer(modifier = Modifier.height(Spacing.xl)) }

                // Weekly Challenge Section
                if (uiState.weeklyChallenge != null) {
                    item {
                        WeeklyChallengeCard(
                            challenge = uiState.weeklyChallenge,
                            onClick = onRewardsClick
                        )
                        Spacer(modifier = Modifier.height(Spacing.xl))
                    }
                }

                item {
                    Text(
                        text = stringResource(id = R.string.dashboard_goals_title),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                item { Spacer(modifier = Modifier.height(Spacing.md)) }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(uiState.goals) { goal ->
                            GoalCard(
                                title = goal.title,
                                savedAmount = goal.savedAmount,
                                targetAmount = goal.targetAmount,
                                progress = goal.progress,
                                icon = goal.icon,
                                color = goal.color,
                                onClick = { onGoalClick(goal.id) }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(Spacing.xl)) }

                item {
                    Text(
                        text = stringResource(id = R.string.dashboard_quick_actions_title),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                item { Spacer(modifier = Modifier.height(Spacing.md)) }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        QuickActionButton(
                            text = stringResource(id = R.string.dashboard_quick_action_save),
                            icon = "💰",
                            isPrimary = true,
                            onClick = onQuickSaveClick,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            text = stringResource(id = R.string.dashboard_quick_action_spend),
                            icon = "💸",
                            isPrimary = false,
                            onClick = onQuickOutClick,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            text = stringResource(id = R.string.dashboard_quick_action_create_goal),
                            icon = "🎯",
                            isPrimary = false,
                            onClick = onCreateGoalClick,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            text = stringResource(id = R.string.dashboard_quick_action_categories),
                            icon = "cat",
                            isPrimary = false,
                            onClick = onCategoriesClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(Spacing.xl)) }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.dashboard_recent_transactions_title),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        TextButton(onClick = onTransactionsClick) {
                            Text(
                                text = stringResource(id = R.string.dashboard_view_all_button),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(Spacing.md)) }

                items(uiState.transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = { onTransactionItemClick(transaction) }
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                }

                item { Spacer(modifier = Modifier.height(Spacing.xl)) }

                item {
                    InsightsCard()
                }
            }
        }
    }
}

@Composable
fun WeeklyChallengeCard(
    challenge: WeeklyChallenge,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏆", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = challenge.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                if (challenge.isCompleted && !challenge.isRewardClaimed) {
                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                        Text("¡Reclamar!", modifier = Modifier.padding(4.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))

            val progress = (challenge.currentAmount / challenge.targetAmount).toFloat().coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )
            
            Spacer(modifier = Modifier.height(Spacing.xs))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // ARREGLADO: Mostramos el monto limitado al objetivo para no confundir al usuario (ej: 200 / 200)
                val displayAmount = challenge.currentAmount.coerceAtMost(challenge.targetAmount)
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale("es", "PE")).format(displayAmount),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale("es", "PE")).format(challenge.targetAmount),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun InsightsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                stringResource(id = R.string.dashboard_insights_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                stringResource(id = R.string.dashboard_insights_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
fun DashboardTopBar(
    userName: String,
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onRewardsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(ComponentSize.avatarSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercase() ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            Text(
                text = stringResource(id = R.string.dashboard_welcome_message, userName),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Row {
             IconButton(onClick = onRewardsClick) {
                Icon(Icons.Default.EmojiEvents, contentDescription = stringResource(id = R.string.dashboard_rewards_button_description))
            }
            BadgedBox(
                badge = {
                    Badge { Text("3") }
                }
            ) {
                IconButton(onClick = onNotificationClick) {
                    Icon(Icons.Default.Notifications, contentDescription = stringResource(id = R.string.dashboard_notifications_button_description))
                }
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = stringResource(id = R.string.dashboard_settings_button_description))
            }
        }
    }
}

@Composable
fun QuickActionButton(
    text: String,
    icon: String,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPrimary) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: DashboardTransaction, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(ComponentSize.iconSize)
                    .clip(CircleShape)
                    .background(transaction.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = transaction.icon, style = MaterialTheme.typography.bodyLarge, color = transaction.color)
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Text(
                text = transaction.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    CASHiTOTheme {
        DashboardScreenContent(
            uiState = DashboardUiState(
                userName = "Ana",
                totalBalance = "S/ 3,420.50",
                freeBalance = "S/ 1,420.50",
                goalsBalance = "S/ 2,000.00",
                goals = listOf(
                    DashboardGoal("1", "Viaje a Cusco", "3,420", "5,000", 0.65f, "✈️", primaryLight),
                    DashboardGoal("2", "Laptop nueva", "800", "4,500", 0.18f, "💻", secondaryLight)
                ),
                transactions = listOf(
                    DashboardTransaction("Ingreso automático", "💰", primaryLight),
                    DashboardTransaction("Compra en supermercado", "🛒", secondaryLight)
                ),
                isLoading = false
            ),
            onGoalClick = {},
            onTransactionsClick = {},
            onReportsClick = {},
            onGoalsClick = {},
            onProfileClick = {},
            onQuickSaveClick = {},
            onQuickOutClick = {},
            onAddCategoryClick = {},
            onCreateGoalClick = {},
            onNotificationClick = {},
            onTransactionItemClick = {},
            onCategoriesClick = {},
            onRewardsClick = {}
        )
    }
}
