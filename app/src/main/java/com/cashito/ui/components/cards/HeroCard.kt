package com.cashito.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cashito.R
import com.cashito.ui.theme.Spacing

@Composable
fun HeroCard(
    totalBalance: String,
    freeBalance: String,
    goalsBalance: String,
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl)
        ) {
            Text(
                text = stringResource(id = R.string.dashboard_total_balance_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            
            // Saldo Libre (Destacado)
            Text(
                text = freeBalance,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "Saldo Disponible",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Desglose Secundario
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .padding(Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // En Metas
                Column {
                    Text(
                        text = "En Metas",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = goalsBalance,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                // Total Real
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Patrimonio Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = totalBalance,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Botones de Acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionButton(
                    text = stringResource(id = R.string.dashboard_income_button),
                    icon = Icons.Default.ArrowUpward,
                    color = Color(0xFF4CAF50), // Greenish
                    onClick = onIncomeClick
                )
                ActionButton(
                    text = stringResource(id = R.string.dashboard_expense_button),
                    icon = Icons.Default.ArrowDownward,
                    color = Color(0xFFE57373), // Reddish
                    onClick = onExpenseClick
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.background(Color.White, CircleShape).padding(4.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview
@Composable
fun HeroCardPreview() {
    HeroCard(
        totalBalance = "S/ 500.00",
        freeBalance = "S/ 250.00",
        goalsBalance = "S/ 250.00",
        onIncomeClick = {},
        onExpenseClick = {}
    )
}
