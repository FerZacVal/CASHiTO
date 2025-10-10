package com.cashito.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

data class PieChartEntry(val value: Float, val color: Color)

@Composable
fun PieChart(
    entries: List<PieChartEntry>,
    modifier: Modifier = Modifier,
    emptyChartMessage: String = "Sin datos"
) {
    val totalValue = entries.sumOf { it.value.toDouble() }.toFloat()
    val emptyColor = MaterialTheme.colorScheme.surfaceVariant

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (totalValue == 0f) {
                drawArc(
                    color = emptyColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = true
                )
            } else {
                var startAngle = -90f
                entries.forEach { entry ->
                    val sweepAngle = (entry.value / totalValue) * 360f
                    drawArc(
                        color = entry.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true
                    )
                    startAngle += sweepAngle
                }
            }
        }
        if (totalValue == 0f) {
            Text(emptyChartMessage)
        }
    }
}
