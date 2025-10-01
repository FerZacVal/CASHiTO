package com.cashito.ui.screens.quick_save

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.components.buttons.SmallButton
import com.cashito.ui.theme.Background
import com.cashito.ui.theme.ComponentSize
import com.cashito.ui.theme.LightGreen
import com.cashito.ui.theme.PrimaryGreen
import com.cashito.ui.theme.Radius
import com.cashito.ui.theme.Spacing

@Composable
fun QuickSaveScreen(
    navController: NavController,
    onNavigateBack: () -> Unit = { navController.popBackStack() },
    onConfirmDeposit: (String, String) -> Unit = { amount, goalId -> 
        // Handle deposit confirmation
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
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(Radius.lg),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg)
            ) {
                // Header
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
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.xl))
                
                // Preset Amounts
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
                
                // Custom Amount Input
                Text(
                    text = "O ingresa un monto personalizado",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                OutlinedTextField(
                    value = customAmount,
                    onValueChange = { 
                        customAmount = it
                        selectedAmount = ""
                    },
                    placeholder = { Text("S/ 0") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(Radius.md),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(Spacing.xl))
                
                // Goal Selection
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
                
                // Confirm Button
                PrimaryButton(
                    text = "Confirmar depósito",
                    onClick = {
                        val amount = if (customAmount.isNotEmpty()) customAmount else selectedAmount
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
            containerColor = if (isSelected) PrimaryGreen else LightGreen
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
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = goal.title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// Data classes
data class QuickSaveGoal(
    val id: String,
    val title: String,
    val icon: String,
    val color: Color
)

// Sample data
fun getSampleGoals(): List<QuickSaveGoal> = listOf(
    QuickSaveGoal("1", "Viaje a Cusco", "✈️", PrimaryGreen),
    QuickSaveGoal("2", "Laptop nueva", "💻", Color(0xFF3B82F6)),
    QuickSaveGoal("3", "Vacaciones", "🏖️", Color(0xFF8B5CF6)),
    QuickSaveGoal("4", "Coche", "🚗", Color(0xFFF59E0B))
)
