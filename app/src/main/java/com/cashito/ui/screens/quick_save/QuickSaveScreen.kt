package com.cashito.ui.screens.quick_save

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.components.buttons.SmallButton
import com.cashito.ui.components.inputs.CashitoTextField
import com.cashito.ui.theme.Radius
import com.cashito.ui.theme.Spacing

@Composable
fun QuickSaveScreen(
    navController: NavController,
    onNavigateBack: () -> Unit = { navController.popBackStack() },
    onConfirmDeposit: (String, String) -> Unit = { _, _ ->
        navController.popBackStack()
    }
) {
    var selectedAmount by remember { mutableStateOf("") }
    var selectedGoalId by remember { mutableStateOf("") }
    var customAmount by remember { mutableStateOf("") }

    val presetAmounts = listOf("5", "10", "20", "50")
    val goals = getSampleGoals()

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
                        text = "Ahorro rápido",
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
                    items(presetAmounts) { amount ->
                        PresetAmountButton(
                            amount = "S/ $amount",
                            isSelected = selectedAmount == amount,
                            onClick = {
                                selectedAmount = amount
                                customAmount = ""
                            }
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
                    value = customAmount,
                    onValueChange = {
                        customAmount = it
                        selectedAmount = ""
                    },
                    label = "Monto personalizado",
                    placeholder = "S/ 0.00",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Spacing.xl))

                Text(
                    text = "Selecciona una meta",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    items(goals) { goal ->
                        GoalChip(
                            goal = goal,
                            isSelected = selectedGoalId == goal.id,
                            onClick = { selectedGoalId = goal.id }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                PrimaryButton(
                    text = "Confirmar depósito",
                    onClick = {
                        val amount = customAmount.ifEmpty { selectedAmount }
                        if (amount.isNotEmpty() && selectedGoalId.isNotEmpty()) {
                            onConfirmDeposit(amount, selectedGoalId)
                        }
                    },
                    enabled = (customAmount.isNotEmpty() || selectedAmount.isNotEmpty()) && selectedGoalId.isNotEmpty()
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
fun GoalChip(
    goal: QuickSaveGoal,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) goal.color else goal.color.copy(alpha = 0.2f)
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
                text = goal.icon,
                style = MaterialTheme.typography.headlineSmall,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else goal.color
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = goal.title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else goal.color,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class QuickSaveGoal(
    val id: String,
    val title: String,
    val icon: String,
    val color: Color
)

@Composable
fun getSampleGoals(): List<QuickSaveGoal> {
    return listOf(
        QuickSaveGoal("1", "Viaje a Cusco", "✈️", MaterialTheme.colorScheme.primary),
        QuickSaveGoal("2", "Laptop nueva", "💻", MaterialTheme.colorScheme.secondary),
        QuickSaveGoal("3", "Vacaciones", "🏖️", MaterialTheme.colorScheme.tertiary),
        QuickSaveGoal("4", "Coche", "🚗", Color(0xFFF59E0B))
    )
}