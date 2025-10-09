package com.cashito.ui.screens.savings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.Routes
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.components.buttons.SecondaryButton
import com.cashito.ui.components.inputs.CashitoTextField
import com.cashito.ui.theme.Spacing

@Composable
fun SavingsScreen(navController: NavController? = null) {
    var newSavingsGoal by remember { mutableStateOf("") }
    var newSavingsAmount by remember { mutableStateOf("") }

    val sampleSavings = listOf(
        "Emergency Fund" to ("$2,500 / $5,000" to 0.50f),
        "Vacation" to ("$800 / $2,000" to 0.40f),
        "New Laptop" to ("$300 / $1,200" to 0.25f),
        "Car Down Payment" to ("$1,200 / $3,000" to 0.40f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.lg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Savings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            FloatingActionButton(
                onClick = { /* TODO: Add savings goal */ },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Savings Goal")
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(Spacing.lg)
            ) {
                Text(
                    text = "Add New Savings Goal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                CashitoTextField(
                    value = newSavingsGoal,
                    onValueChange = { newSavingsGoal = it },
                    label = "Goal Name",
                    placeholder = "e.g., Emergency Fund"
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                CashitoTextField(
                    value = newSavingsAmount,
                    onValueChange = { newSavingsAmount = it },
                    label = "Target Amount",
                    placeholder = "S/ 5,000",
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                PrimaryButton(
                    onClick = { /* TODO: Save savings goal */ },
                    text = "Add Goal"
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            text = "Your Savings Goals",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = Spacing.md)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            items(sampleSavings) { (goal, progressData) ->
                val (progressText, progressValue) = progressData
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.lg)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = goal,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = progressText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.sm))

                        LinearProgressIndicator(
                            progress = { progressValue },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        SecondaryButton(
            onClick = { navController?.navigate(Routes.HOME) },
            text = "Back to Home"
        )
    }
}