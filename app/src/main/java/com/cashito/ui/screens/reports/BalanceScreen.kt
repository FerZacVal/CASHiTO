package com.cashito.ui.screens.reports

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cashito.ui.components.charts.LineChart
import com.cashito.ui.theme.CASHiTOTheme
import com.cashito.ui.theme.Spacing
import com.cashito.ui.viewmodel.BalanceEntry
import com.cashito.ui.viewmodel.BalanceSummary
import com.cashito.ui.viewmodel.BalanceUiState
import com.cashito.ui.viewmodel.BalanceViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BalanceScreen(
    navController: NavController,
    viewModel: BalanceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    BalanceScreenContent(
        uiState = uiState,
        onPeriodSelected = viewModel::onPeriodSelected,
        onNavigateBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceScreenContent(
    uiState: BalanceUiState,
    onPeriodSelected: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
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
                                onClick = { onPeriodSelected(filter) },
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

@Preview(showBackground = true)
@Composable
fun BalanceScreenPreview() {
    val sampleData = listOf(
        BalanceEntry("Sem 1", 5200f),
        BalanceEntry("Sem 2", 5600f),
        BalanceEntry("Sem 3", 5400f),
        BalanceEntry("Sem 4", 5800f)
    )
    val sampleSummary = BalanceSummary(5800f, 250.75f, "semana anterior")

    CASHiTOTheme {
        BalanceScreenContent(
            uiState = BalanceUiState(
                balanceData = sampleData,
                summary = sampleSummary,
                selectedPeriod = "Semanal",
                isLoading = false
            ),
            onPeriodSelected = {},
            onNavigateBack = {}
        )
    }
}
