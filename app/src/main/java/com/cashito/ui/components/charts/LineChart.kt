package com.cashito.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.cashito.ui.viewmodel.BalanceEntry

@Composable
fun LineChart(
    data: List<BalanceEntry>,
    modifier: Modifier = Modifier
) {
    val maxBalance = data.maxOfOrNull { it.balance } ?: 1f
    val minBalance = data.minOfOrNull { it.balance } ?: 0f
    val primaryColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stepX = if (data.size > 1) size.width / (data.size - 1) else 0f
            val path = Path()
            val gradientPath = Path()

            (0..4).forEach { i ->
                val y = size.height * (i / 4f)
                drawLine(gridColor, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 1f)
            }

            data.forEachIndexed { i, entry ->
                val x = stepX * i
                val y = size.height - ((entry.balance - minBalance) / (maxBalance - minBalance).coerceAtLeast(1f)) * size.height
                if (i == 0) {
                    path.moveTo(x, y)
                    gradientPath.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                    gradientPath.lineTo(x, y)
                }
            }

            gradientPath.lineTo(size.width, size.height)
            gradientPath.lineTo(0f, size.height)
            gradientPath.close()
            drawPath(
                gradientPath,
                brush = Brush.verticalGradient(colors = listOf(primaryColor.copy(alpha = 0.3f), Color.Transparent)),
            )

            drawPath(path, color = primaryColor, style = Stroke(width = 4.dp.toPx()))

            data.forEachIndexed { i, entry ->
                val x = stepX * i
                val y = size.height - ((entry.balance - minBalance) / (maxBalance - minBalance).coerceAtLeast(1f)) * size.height
                drawCircle(color = primaryColor, radius = 8f, center = Offset(x, y))
                drawCircle(color = Color.White, radius = 4f, center = Offset(x, y))
            }
        }
    }
}
