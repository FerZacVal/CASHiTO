package com.cashito.ui.screens.rewards

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cashito.R
import com.cashito.domain.entities.gamification.Reward
import com.cashito.domain.entities.gamification.RewardType
import com.cashito.domain.entities.gamification.WeeklyChallenge
import com.cashito.domain.entities.goal.Goal
import com.cashito.ui.theme.Spacing
import com.cashito.ui.viewmodel.ProjectedProfit
import com.cashito.ui.viewmodel.RewardsViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    navController: NavController,
    viewModel: RewardsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedRewardId by remember { mutableStateOf<String?>(null) }
    var selectedGoalId by remember { mutableStateOf<String?>(null) }
    var projectedProfit by remember { mutableStateOf<ProjectedProfit?>(null) }
    var showTermsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.dashboard_rewards_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.goal_detail_back_button_description)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading && !uiState.showRewardDialog) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                ) {
                    // Challenge Section
                    if (uiState.weeklyChallenge != null) {
                        item {
                            Text(
                                text = "Reto Semanal",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(Spacing.md))
                            WeeklyChallengeScreenCard(
                                challenge = uiState.weeklyChallenge!!,
                                onClaimClick = { viewModel.claimReward() }
                            )
                        }
                    }

                    // Rewards Section
                    item {
                        Text(
                            text = "Tus Premios",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (uiState.userRewards.isEmpty()) {
                        item {
                             Text(
                                text = "Aún no tienes premios ganados. ¡Completa retos para ganar!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(uiState.userRewards) { reward ->
                            RewardGiftBox(
                                reward = reward,
                                onUseClick = {
                                    selectedRewardId = reward.id
                                    selectedGoalId = null // Reset selected goal
                                    projectedProfit = null // Reset profit
                                    showBottomSheet = true
                                }
                            )
                        }
                    }
                }
            }
            
            if (uiState.showRewardDialog && uiState.lastClaimedReward != null) {
                RewardDialog(
                    reward = uiState.lastClaimedReward!!,
                    onDismiss = { viewModel.dismissRewardDialog() }
                )
            }
            
            if (showTermsDialog) {
                TermsAndConditionsDialog(onDismiss = { showTermsDialog = false })
            }

            if (showBottomSheet && selectedRewardId != null) {
                RewardApplicationBottomSheet(
                    sheetState = sheetState,
                    onDismiss = { showBottomSheet = false },
                    goals = uiState.availableGoals,
                    selectedGoalId = selectedGoalId,
                    projectedProfit = projectedProfit,
                    onGoalSelected = { goalId ->
                        selectedGoalId = goalId
                        projectedProfit = viewModel.calculateProjectedProfit(selectedRewardId!!, goalId)
                    },
                    onConfirm = {
                        if (selectedGoalId != null) {
                            viewModel.applyRewardToGoal(selectedRewardId!!, selectedGoalId!!)
                            showBottomSheet = false
                        }
                    },
                    onShowTerms = { showTermsDialog = true }
                )
            }
        }
    }
}

@Composable
fun WeeklyChallengeScreenCard(
    challenge: WeeklyChallenge,
    onClaimClick: () -> Unit
) {
     Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (challenge.isCompleted && !challenge.isRewardClaimed) {
                    Button(onClick = onClaimClick) {
                        Text("¡Reclamar!")
                    }
                } else if (challenge.isRewardClaimed) {
                     Text(
                        text = "Reclamado",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun RewardGiftBox(
    reward: Reward,
    onUseClick: () -> Unit
) {
    val isBoost = reward.type == RewardType.APR_BOOST
    val containerColor = if (isBoost) Color(0xFFFFE082) else MaterialTheme.colorScheme.surfaceVariant // Gold color for boosts
    val contentColor = if (isBoost) Color(0xFF5D4037) else MaterialTheme.colorScheme.onSurfaceVariant
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de regalo
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🎁", fontSize = 28.sp)
            }
            
            Spacer(modifier = Modifier.width(Spacing.md))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reward.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if(reward.isUsed) "Usado" else "Válido por ${reward.durationDays} días",
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
            
            if (!reward.isUsed && isBoost) {
                Button(
                    onClick = onUseClick,
                    colors = ButtonDefaults.buttonColors(containerColor = contentColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Usar", color = Color.White)
                }
            } else if (reward.isUsed) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = contentColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardApplicationBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    goals: List<Goal>,
    selectedGoalId: String?,
    projectedProfit: ProjectedProfit?,
    onGoalSelected: (String) -> Unit,
    onConfirm: () -> Unit,
    onShowTerms: () -> Unit
) {
    var termsAccepted by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.xl)
        ) {
            Text(
                text = if (selectedGoalId == null) "Aplicar Boost a..." else "Confirmar Aplicación",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md)
            )

            if (selectedGoalId == null) {
                // Paso 1: Selección de Meta
                if (goals.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.xl),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No tienes metas activas para aplicar este premio.")
                    }
                } else {
                    LazyColumn {
                        items(goals) { goal ->
                            ListItem(
                                headlineContent = { Text(goal.name, fontWeight = FontWeight.Medium) },
                                supportingContent = { Text("Meta: S/ ${goal.targetAmount}") },
                                leadingContent = { Text(goal.icon, fontSize = 24.sp) },
                                modifier = Modifier.clickable { onGoalSelected(goal.id) }
                            )
                        }
                    }
                }
            } else if (projectedProfit != null) {
                // Paso 2: Confirmación y Detalles
                Column(modifier = Modifier.padding(horizontal = Spacing.lg)) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Text("Detalle de la Operación", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("APR Aplicado:")
                                Text("${projectedProfit.rewardApr}%", fontWeight = FontWeight.Bold)
                            }
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Fondo Base:")
                                Text(NumberFormat.getCurrencyInstance(Locale("es", "PE")).format(projectedProfit.goalSavedAmount))
                            }
                            
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Inicio:")
                                Text(DateFormat.getDateInstance().format(Date()))
                            }
                             Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Fin / Acreditación:")
                                Text(DateFormat.getDateInstance().format(projectedProfit.endDate))
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                            
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Ganancia Proyectada:", fontWeight = FontWeight.Bold)
                                Text(
                                    NumberFormat.getCurrencyInstance(Locale("es", "PE")).format(projectedProfit.profit),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.md))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it }
                        )
                        Text(
                            text = "Acepto los términos y condiciones",
                            modifier = Modifier.clickable { onShowTerms() },
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.md))

                    Button(
                        onClick = onConfirm,
                        enabled = termsAccepted,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirmar y Aplicar")
                    }
                }
            }
        }
    }
}

@Composable
fun RewardDialog(
    reward: Reward,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¡Felicidades!", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🎉", style = MaterialTheme.typography.displayLarge)
                Spacer(modifier = Modifier.height(Spacing.md))
                Text("Has ganado:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    reward.description, 
                    style = MaterialTheme.typography.headlineSmall, 
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¡Genial!")
            }
        }
    )
}

@Composable
fun TermsAndConditionsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Términos y Condiciones") },
        text = {
            LazyColumn {
                item {
                    Text(
                        "1. El APR (Tasa de Porcentaje Anual) se aplica únicamente sobre el saldo ahorrado en la meta al momento de la activación.\n\n" +
                        "2. La ganancia proyectada es una estimación y se acreditará al finalizar el periodo de duración del premio.\n\n" +
                        "3. Nos reservamos el derecho de cancelar el beneficio si se detecta actividad fraudulenta o inconsistencias en la cuenta.\n\n" +
                        "4. Si la meta asociada es eliminada o se retira el total de los fondos antes de la fecha de acreditación, el beneficio se perderá automáticamente.\n\n" +
                        "5. Este beneficio no es transferible ni canjeable por efectivo inmediato.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido")
            }
        }
    )
}
