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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cashito.ui.components.cards.GoalCard
import com.cashito.ui.components.cards.HeroCard
import com.cashito.ui.components.navigation.CashitoBottomNavigation
import com.cashito.ui.theme.ComponentSize
import com.cashito.ui.theme.Spacing
import com.cashito.ui.viewmodel.DashboardGoal
import com.cashito.ui.viewmodel.DashboardTransaction
import com.cashito.ui.viewmodel.DashboardViewModel

@Composable
fun InsightsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                "💡 ¿Sabías que?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                "Ahorrar S/ 20 a la semana se convierte en más de S/ 1,000 al año.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel(),
    onNavigateToGoalDetail: (String) -> Unit = { navController.navigate("goal_detail/$it") },
    onNavigateToTransactions: () -> Unit = { navController.navigate("transactions") },
    onNavigateToReports: () -> Unit = { navController.navigate("reports") },
    onNavigateToGoals: () -> Unit = { navController.navigate("goals") },
    onNavigateToProfile: () -> Unit = { navController.navigate("profile") },
    onNavigateToQuickSave: () -> Unit = { navController.navigate("quick_save") }
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            DashboardTopBar(
                userName = uiState.userName,
                onNotificationClick = { /* Handle notification */ },
                onSettingsClick = { onNavigateToProfile() }
            )
        },
        bottomBar = {
            CashitoBottomNavigation(
                currentRoute = "dashboard",
                onNavigate = { route ->
                    when (route) {
                        "dashboard" -> { /* Already on this screen */ }
                        "transactions" -> onNavigateToTransactions()
                        "reports" -> onNavigateToReports()
                        "goals" -> onNavigateToGoals()
                        "profile" -> onNavigateToProfile()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToQuickSave,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Quick Save")
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
                    HeroCard(
                        totalBalance = uiState.totalBalance,
                        goalProgress = uiState.mainGoalProgressText,
                        progressPercentage = uiState.mainGoalProgressPercentage,
                        onIncomeClick = onNavigateToQuickSave,
                        onExpenseClick = { navController.navigate("quick_out") }
                    )
                }

                item { Spacer(modifier = Modifier.height(Spacing.xl)) }

                item {
                    Text(
                        text = "Tus metas",
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
                                onClick = { onNavigateToGoalDetail(goal.id) }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(Spacing.xl)) }

                item {
                    Text(
                        text = "Acciones rápidas",
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
                            text = "Ahorro rápido",
                            icon = "💰",
                            isPrimary = true,
                            onClick = onNavigateToQuickSave,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            text = "Gasto rápido",
                            icon = "💸",
                            isPrimary = false,
                            onClick = { navController.navigate("quick_out") },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            text = "Enviar",
                            icon = "📤",
                            isPrimary = false,
                            onClick = { /* Handle transfer */ },
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
                            text = "Movimientos recientes",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        TextButton(onClick = onNavigateToTransactions) {
                            Text(
                                text = "Ver todos",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(Spacing.md)) }

                items(uiState.transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = { /* Handle transaction click */ }
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
fun DashboardTopBar(
    userName: String,
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit
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
                text = "Hola, $userName 👋",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Row {
            BadgedBox(
                badge = {
                    Badge { Text("3") }
                }
            ) {
                IconButton(onClick = onNotificationClick) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                }
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
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
