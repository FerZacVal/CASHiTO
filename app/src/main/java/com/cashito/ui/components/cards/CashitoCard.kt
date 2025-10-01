package com.cashito.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cashito.ui.theme.ComponentSize
import com.cashito.ui.theme.LightGreen
import com.cashito.ui.theme.PrimaryGreen
import com.cashito.ui.theme.Radius
import com.cashito.ui.theme.ShadowColor
import com.cashito.ui.theme.Spacing

@Composable
fun HeroCard(
    totalBalance: String,
    goalProgress: String,
    progressPercentage: Int,
    onDepositClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(Radius.lg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            LightGreen.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg)
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Saldo total",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = totalBalance,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text(
                        text = goalProgress,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Row {
                        androidx.compose.material3.Button(
                            onClick = onDepositClick,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen
                            ),
                            shape = RoundedCornerShape(Radius.sm)
                        ) {
                            Text("Depositar")
                        }
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        androidx.compose.material3.OutlinedButton(
                            onClick = onWithdrawClick,
                            shape = RoundedCornerShape(Radius.sm)
                        ) {
                            Text("Retirar")
                        }
                    }
                }
                
                // Progress Ring
                Box(
                    modifier = Modifier
                        .size(ComponentSize.donutSize)
                        .align(Alignment.CenterVertically)
                ) {
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val strokeWidth = 8.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2
                        val center = androidx.compose.ui.geometry.Offset(
                            size.width / 2,
                            size.height / 2
                        )
                        
                        // Background circle
                        drawCircle(
                            color = Color.Gray.copy(alpha = 0.2f),
                            radius = radius,
                            center = center,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                        )
                        
                        // Progress arc
                        val sweepAngle = (progressPercentage / 100f) * 360f
                        drawArc(
                            color = PrimaryGreen,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                center.x - radius,
                                center.y - radius
                            ),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                        )
                    }
                    
                    Text(
                        text = "$progressPercentage%",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun GoalCard(
    title: String,
    savedAmount: String,
    targetAmount: String,
    progress: Float,
    icon: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .width(140.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(Radius.lg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.headlineMedium
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(Radius.round))
                        .background(color)
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(Spacing.xs))
            
            Text(
                text = "S/ $savedAmount / S/ $targetAmount",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            androidx.compose.material3.LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = color,
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )
        }
    }
}
