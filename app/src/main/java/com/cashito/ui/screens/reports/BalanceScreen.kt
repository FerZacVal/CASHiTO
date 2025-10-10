package com.cashito.ui.screens.reports

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cashito.ui.theme.Spacing
import com.cashito.ui.viewmodel.BalanceEntry
import com.cashito.ui.viewmodel.BalanceSummary
import com.cashito.ui.viewmodel.BalanceViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceScreen(
    navController: NavController,
    viewModel: BalanceViewModel = viewModel(),
    onNavigateBack: () -> Unit = { navController.popBackStack() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val filters = listOf("Diario", "Semanal", "Mensual")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Análisis de Balances", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, titleContentColor = MaterialTheme.colorScheme.onSurface)
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(paddingValues),
                contentPadding = PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                item { BalanceSummaryCard(summary = uiState.summary) }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        filters.forEach { filter ->
                            FilterChip(
                                selected = uiState.selectedPeriod == filter,
                                onClick = { viewModel.onPeriodSelected(filter) },
                                label = { Text(filter) },
                                leadingIcon = if (uiState.selectedPeriod == filter) { { Icon(Icons.Default.Done, null) } } else { null }
                            )
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                    ) {
                        if (uiState.balanceData.isNotEmpty()) {
                            LineChart(data = uiState.balanceData, modifier = Modifier.padding(Spacing.lg).fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BalanceSummaryCard(summary: BalanceSummary) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "PE")) }
    val isPositive = summary.change >= 0
    val changeColor = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text("Balance actual", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(currencyFormatter.format(summary.currentBalance), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(Spacing.xs))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = "Change",
                    tint = changeColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = " ${currencyFormatter.format(summary.change)} vs ${summary.periodLabel}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = changeColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun LineChart(data: List<BalanceEntry>, modifier: Modifier = Modifier) {
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
