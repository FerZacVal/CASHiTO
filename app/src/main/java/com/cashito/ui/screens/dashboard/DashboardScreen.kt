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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.ui.components.buttons.SmallButton
import com.cashito.ui.components.cards.GoalCard
import com.cashito.ui.components.cards.HeroCard
import com.cashito.ui.components.navigation.CashitoBottomNavigation
import com.cashito.ui.theme.Background
import com.cashito.ui.theme.ComponentSize
import com.cashito.ui.theme.LightGreen
import com.cashito.ui.theme.PrimaryGreen
import com.cashito.ui.theme.Spacing

@Composable
fun DashboardScreen(
    navController: NavController,
    onNavigateToGoalDetail: (String) -> Unit = { navController.navigate("goal_detail/$it") },
    onNavigateToTransactions: () -> Unit = { navController.navigate("transactions") },
    onNavigateToProfile: () -> Unit = { navController.navigate("profile") },
    onNavigateToQuickSave: () -> Unit = { navController.navigate("quick_save") }
) {
    Scaffold(
        topBar = {
            DashboardTopBar(
                userName = "Ana",
                onNotificationClick = { /* Handle notification */ },
                onSettingsClick = { onNavigateToProfile() }
            )
        },
        bottomBar = {
            CashitoBottomNavigation(
                currentRoute = "dashboard",
                onNavigate = { route ->
                    when (route) {
                        "transactions" -> onNavigateToTransactions()
                        "profile" -> onNavigateToProfile()
                        else -> { /* Handle other routes */ }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToQuickSave,
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Quick Save")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg)
        ) {
            // Hero Card
            item {
                HeroCard(
                    totalBalance = "S/ 3,420",
                    goalProgress = "Meta principal: Viaje a Cusco — 65%",
                    progressPercentage = 65,
                    onDepositClick = onNavigateToQuickSave,
                    onWithdrawClick = { /* Handle withdraw */ }
                )
            }
            
            item { Spacer(modifier = Modifier.height(Spacing.xl)) }
            
            // Goals Section
            item {
                Text(
                    text = "Tus metas",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Semibold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            item { Spacer(modifier = Modifier.height(Spacing.md)) }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    items(getSampleGoals()) { goal ->
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
            
            // Quick Actions
            item {
                Text(
                    text = "Acciones rápidas",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Semibold,
                    color = MaterialTheme.colorScheme.onBackground
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
                        text = "Redondeo",
                        icon = "🔄",
                        isPrimary = false,
                        onClick = { /* Handle round up */ },
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
            
            // Recent Transactions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Movimientos recientes",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Semibold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    androidx.compose.material3.TextButton(
                        onClick = onNavigateToTransactions
                    ) {
                        Text(
                            text = "Ver todos",
                            color = PrimaryGreen
                        )
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(Spacing.md)) }
            
            items(getSampleTransactions()) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { /* Handle transaction click */ }
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
            }
            
            item { Spacer(modifier = Modifier.height(Spacing.xl)) }
            
            // Insights Card
            item {
                InsightsCard()
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
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(ComponentSize.avatarSize)
                    .clip(CircleShape)
                    .background(LightGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
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
                    Badge(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ) {
                        Text("3")
                    }
                }
            ) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            
            IconButton(onClick = onSettingsClick) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground
                )
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
        modifier = modifier.height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPrimary) PrimaryGreen else LightGreen
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(Spacing.md)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = if (isPrimary) Color.White else MaterialTheme.colorScheme.onBackground,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(transaction.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = transaction.icon,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            Spacer(modifier = Modifier.width(Spacing.md))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = transaction.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
            
            Text(
                text = transaction.amount,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = transaction.amountColor
            )
        }
    }
}

@Composable
fun InsightsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg)
        ) {
            Text(
                text = "💡 Insight del mes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Semibold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            Text(
                text = "Este mes ahorraste S/ 420 (+12% respecto al mes pasado).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
        }
    }
}

// Data classes
data class Goal(
    val id: String,
    val title: String,
    val savedAmount: String,
    val targetAmount: String,
    val progress: Float,
    val icon: String,
    val color: Color
)

data class Transaction(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: String,
    val amountColor: Color,
    val icon: String,
    val color: Color
)

// Sample data
fun getSampleGoals(): List<Goal> = listOf(
    Goal("1", "Laptop nueva", "1200", "2000", 0.6f, "💻", PrimaryGreen),
    Goal("2", "Vacaciones", "800", "3000", 0.27f, "✈️", Color(0xFF3B82F6)),
    Goal("3", "Coche", "5000", "15000", 0.33f, "🚗", Color(0xFF8B5CF6))
)

fun getSampleTransactions(): List<Transaction> = listOf(
    Transaction("1", "Depósito automático", "Hoy, 09:30", "+S/ 200", PrimaryGreen, "💰", PrimaryGreen),
    Transaction("2", "Compra en supermercado", "Ayer, 18:45", "-S/ 85.50", Color.Red, "🛒", Color(0xFF3B82F6)),
    Transaction("3", "Transferencia recibida", "Ayer, 14:20", "+S/ 500", PrimaryGreen, "📥", Color(0xFF10B981))
)
