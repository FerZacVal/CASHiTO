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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.components.buttons.SecondaryButton
import com.cashito.ui.theme.Background
import com.cashito.ui.theme.ComponentSize
import com.cashito.ui.theme.LightGreen
import com.cashito.ui.theme.PrimaryGreen
import com.cashito.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goalId: String,
    navController: NavController,
    onNavigateBack: () -> Unit = { navController.popBackStack() },
    onNavigateToDeposit: () -> Unit = { navController.navigate("quick_save") },
    onNavigateToEdit: () -> Unit = { navController.navigate("goal_form") }
) {
    var showMenu by remember { mutableStateOf(false) }
    var isRecurringEnabled by remember { mutableStateOf(true) }
    
    val goal = getSampleGoal(goalId)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Semibold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar meta") },
                            onClick = { 
                                showMenu = false
                                onNavigateToEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Compartir meta") },
                            onClick = { showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Pausar autoahorro") },
                            onClick = { showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar meta", color = Color.Red) },
                            onClick = { showMenu = false }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg)
        ) {
            // Hero Section with Donut Chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Large Donut Chart
                        Box(
                            modifier = Modifier.size(ComponentSize.largeDonutSize),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = goal.progress,
                                modifier = Modifier.fillMaxSize(),
                                color = goal.color,
                                strokeWidth = 14.dp,
                                trackColor = Color.Gray.copy(alpha = 0.2f)
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
                            text = "Ahorrado S/ ${goal.savedAmount} / S/ ${goal.targetAmount}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Semibold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        
                        Text(
                            text = "Meta: ${goal.targetDate}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(Spacing.xl)) }
            
            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    PrimaryButton(
                        text = "Depositar",
                        onClick = onNavigateToDeposit,
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text = "Editar meta",
                        onClick = onNavigateToEdit,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(Spacing.xl)) }
            
            // Recurring Savings Plan
            item {
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
                            text = "Plan de ahorro",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Semibold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.md))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Aporte recurrente",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Switch(
                                checked = isRecurringEnabled,
                                onCheckedChange = { isRecurringEnabled = it },
                                colors = androidx.compose.material3.SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PrimaryGreen,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color.Gray
                                )
                            )
                        }
                        
                        if (isRecurringEnabled) {
                            Spacer(modifier = Modifier.height(Spacing.md))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Frecuencia",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "Semanal",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Monto",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "S/ 200",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            
                            Text(
                                text = "Sugerencia: programa aportes automáticos para avanzar sin pensar.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(Spacing.xl)) }
            
            // Transaction History
            item {
                Text(
                    text = "Historial de esta meta",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Semibold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            item { Spacer(modifier = Modifier.height(Spacing.md)) }
            
            if (getGoalTransactions(goalId).isEmpty()) {
                item {
                    EmptyStateCard()
                }
            } else {
                items(getGoalTransactions(goalId)) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = { /* Handle transaction click */ }
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: GoalTransaction,
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = transaction.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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
fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💰",
                style = MaterialTheme.typography.displayLarge
            )
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            Text(
                text = "Aún no tienes aportes en esta meta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Semibold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            Text(
                text = "Empieza con tu primer depósito.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Data classes
data class GoalDetail(
    val id: String,
    val title: String,
    val savedAmount: String,
    val targetAmount: String,
    val progress: Float,
    val targetDate: String,
    val color: Color,
    val icon: String
)

data class GoalTransaction(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: String,
    val amountColor: Color,
    val icon: String,
    val color: Color
)

// Sample data
fun getSampleGoal(goalId: String): GoalDetail = GoalDetail(
    id = goalId,
    title = "Viaje a Cusco",
    savedAmount = "2,600",
    targetAmount = "4,000",
    progress = 0.65f,
    targetDate = "20 dic 2026",
    color = PrimaryGreen,
    icon = "✈️"
)

fun getGoalTransactions(goalId: String): List<GoalTransaction> = listOf(
    GoalTransaction("1", "Depósito automático", "Hoy, 09:30", "+S/ 200", PrimaryGreen, "💰", PrimaryGreen),
    GoalTransaction("2", "Ahorro manual", "Ayer, 15:20", "+S/ 500", PrimaryGreen, "💳", Color(0xFF3B82F6)),
    GoalTransaction("3", "Redondeo", "Hace 2 días", "+S/ 15.50", PrimaryGreen, "🔄", Color(0xFF10B981))
)
