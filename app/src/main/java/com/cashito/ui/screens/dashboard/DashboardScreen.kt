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
import androidx.compose.material3.TextButton // Importado para TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign // Importado para TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
// Asegúrate de que estos componentes también usen MaterialTheme internamente
import com.cashito.ui.components.cards.GoalCard
import com.cashito.ui.components.cards.HeroCard
import com.cashito.ui.components.navigation.CashitoBottomNavigation
// Asumiendo que estos están definidos en tu Color.kt
import com.cashito.ui.theme.background
import com.cashito.ui.theme.ComponentSize // Asumiendo que esto define Dp values
import com.cashito.ui.theme.lightGreen
import com.cashito.ui.theme.primaryGreen
import com.cashito.ui.theme.Spacing // Asumiendo que esto define Dp values

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
            CashitoBottomNavigation( // Asegúrate que este componente use MaterialTheme
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
                // Considera usar MaterialTheme.colorScheme.primary o tertiaryContainer
                containerColor = PrimaryGreen,
                // Considera usar MaterialTheme.colorScheme.onPrimary o onTertiaryContainer
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Quick Save")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                // Asumiendo que Background es un color de tu ui.theme
                // Si es el fondo principal de la app, MaterialTheme.colorScheme.background sería lo estándar
                .background(Background)
                .padding(paddingValues),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg)
        ) {
            // Hero Card
            item {
                HeroCard( // Asegúrate que este componente use MaterialTheme
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
                    // Considera mover FontWeight a AppTypography si es un estilo consistente
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item { Spacer(modifier = Modifier.height(Spacing.md)) }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    items(getSampleGoals()) { goal -> // getSampleGoals debe proveer Goal objects
                        GoalCard( // Asegúrate que este componente use MaterialTheme
                            title = goal.title,
                            savedAmount = goal.savedAmount,
                            targetAmount = goal.targetAmount,
                            progress = goal.progress,
                            icon = goal.icon,
                            color = goal.color, // El color del GoalCard podría venir del tema también
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
                    // Considera mover FontWeight a AppTypography
                    fontWeight = FontWeight.SemiBold,
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
                        // Considera mover FontWeight a AppTypography
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton( // Usar TextButton de Material 3
                        onClick = onNavigateToTransactions
                    ) {
                        Text(
                            text = "Ver todos",
                            // Considera usar MaterialTheme.colorScheme.primary para el color del texto del botón
                            color = PrimaryGreen
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(Spacing.md)) }

            items(getSampleTransactions()) { transaction -> // getSampleTransactions debe proveer Transaction objects
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
            .padding(Spacing.lg), // Spacing.lg debe ser un Dp value
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(ComponentSize.avatarSize) // ComponentSize.avatarSize debe ser un Dp value
                    .clip(CircleShape)
                    // Considera usar un color del tema como secondaryContainer o tertiaryContainer
                    .background(LightGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A", // Podría ser la inicial del userName
                    style = MaterialTheme.typography.headlineMedium,
                    // Considera mover FontWeight a AppTypography
                    fontWeight = FontWeight.Bold,
                    // El color del texto debería contrastar con LightGreen.
                    // Podría ser onSecondaryContainer u onTertiaryContainer si LightGreen es mapeado.
                    // O un color personalizado que contraste bien.
                    color = PrimaryGreen
                )
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            Text(
                text = "Hola, $userName 👋",
                style = MaterialTheme.typography.headlineMedium,
                // Considera mover FontWeight a AppTypography
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Row {
            BadgedBox(
                badge = {
                    Badge(
                        // Considera MaterialTheme.colorScheme.error o un rojo específico de notificaciones
                        containerColor = Color.Red,
                        contentColor = Color.White // O MaterialTheme.colorScheme.onError
                    ) {
                        Text("3") // La tipografía de la badge también es configurable
                    }
                }
            ) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onBackground // Correcto
                    )
                }
            }

            IconButton(onClick = onSettingsClick) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground // Correcto
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
            // Mapea PrimaryGreen y LightGreen a roles del tema si es posible
            // ej. primary y secondaryContainer, o surfaceVariant
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
                // El color del icono (texto) se tomará de LocalContentColor,
                // que debería ser onPrimaryContainer o similar si los containerColors están bien mapeados.
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                // Considera mover FontWeight a AppTypography
                fontWeight = FontWeight.Medium,
                // El color del texto debe contrastar con el containerColor
                // ej. if (isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                color = if (isPrimary) Color.White else MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction, // Transaction data class debe estar definida
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        // Considera MaterialTheme.colorScheme.surface o surfaceContainerLow
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // El dp está bien, elevation viene del tema
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
                    // El transaction.color.copy(alpha = 0.1f) es una buena forma de crear un fondo suave
                    // Asegúrate que transaction.color contraste bien con el icono de texto.
                    .background(transaction.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = transaction.icon,
                    style = MaterialTheme.typography.headlineSmall,
                    // El color del icono (texto) debería ser transaction.color o uno que contraste
                    // con el fondo del Box (transaction.color.copy(alpha = 0.1f)).
                    // Podría ser transaction.color o MaterialTheme.colorScheme.onSurfaceVariant
                    color = transaction.color // Asumiendo que el color del icono es el mismo que el base
                )
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.bodyMedium,
                    // Considera mover FontWeight a AppTypography
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = transaction.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Buen uso de color de énfasis medio
                )
            }

            Text(
                text = transaction.amount,
                style = MaterialTheme.typography.bodyMedium,
                // Considera mover FontWeight a AppTypography
                fontWeight = FontWeight.Medium,
                color = transaction.amountColor // amountColor debe ser un Color, ej. PrimaryGreen o Color.Red
            )
        }
    }
}

@Composable
fun InsightsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        // Considera MaterialTheme.colorScheme.surface o surfaceContainerLow
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
                // Considera mover FontWeight a AppTypography
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = "Este mes ahorraste S/ 420 (+12% respecto al mes pasado).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Buen uso
            )
        }
    }
}

// Data classes (Asegúrate que estén definidas y sean correctas)
data class Goal(
    val id: String,
    val title: String,
    val savedAmount: String,
    val targetAmount: String,
    val progress: Float, // Debería ser Float (0.0f a 1.0f) o Int (0 a 100) para el progreso
    val icon: String, // O ImageVector
    val color: Color // El color asociado a la meta
)

data class Transaction(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: String,
    val amountColor: Color, // Color para el texto del monto (ej. verde para ingresos, rojo para gastos)
    val icon: String, // O ImageVector
    val color: Color // Color base para el icono/fondo del icono
)

// Sample data (Deberían usar las data classes definidas)
fun getSampleGoals(): List<Goal> = listOf(
    Goal("1", "Laptop nueva", "1200", "2000", 0.6f, "💻", PrimaryGreen),
    Goal("2", "Vacaciones", "800", "3000", 0.27f, "✈️", Color(0xFF3B82F6)), // Considera definir este color en Color.kt
    Goal("3", "Coche", "5000", "15000", 0.33f, "🚗", Color(0xFF8B5CF6)) // Considera definir este color en Color.kt
)

fun getSampleTransactions(): List<Transaction> = listOf(
    Transaction("1", "Depósito automático", "Hoy, 09:30", "+S/ 200", PrimaryGreen, "💰", PrimaryGreen),
    Transaction("2", "Compra en supermercado", "Ayer, 18:45", "-S/ 85.50", Color.Red, "🛒", Color(0xFF3B82F6)), // Color.Red para amountColor, otro color para el icono
    Transaction("3", "Transferencia recibida", "Ayer, 14:20", "+S/ 500", PrimaryGreen, "📥", Color(0xFF10B981)) // Considera definir este color en Color.kt
)
