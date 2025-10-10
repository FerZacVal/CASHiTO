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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cashito.ui.components.charts.PieChart
import com.cashito.ui.components.charts.PieChartEntry
import com.cashito.ui.theme.CASHiTOTheme
import com.cashito.ui.theme.Spacing
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import com.cashito.ui.theme.tertiaryLight
import com.cashito.ui.viewmodel.CategoryIncome
import com.cashito.ui.viewmodel.IncomeReportUiState
import com.cashito.ui.viewmodel.IncomeReportViewModel

@Composable
fun IncomeReportScreen(
    navController: NavController,
    viewModel: IncomeReportViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    IncomeReportScreenContent(
        uiState = uiState,
        onNavigateBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeReportScreenContent(
    uiState: IncomeReportUiState,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ingresos por Categoría", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, titleContentColor = MaterialTheme.colorScheme.onSurface)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                item {
                    Text("Distribución de tus ingresos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = Spacing.md))
                }
                item {
                    PieChart(
                        entries = uiState.incomes.map { PieChartEntry(it.amount, it.color) },
                        modifier = Modifier.size(250.dp),
                        emptyChartMessage = "Sin ingresos"
                    )
                    Spacer(modifier = Modifier.height(Spacing.xl))
                }
                item {
                    IncomeCategoryLegend(incomes = uiState.incomes)
                    Spacer(modifier = Modifier.height(Spacing.lg))
                }
            }
        }
    }
}

@Composable
fun IncomeCategoryLegend(incomes: List<CategoryIncome>) {
    val totalAmount = incomes.sumOf { it.amount.toDouble() }.toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            if (totalAmount == 0f) {
                Text(
                    text = "No hay datos de ingresos para mostrar.",
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = Spacing.md),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                incomes.forEach { income ->
                    val percentage = (income.amount / totalAmount) * 100
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(16.dp).background(income.color, shape = CircleShape))
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Text(income.categoryName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        }
                        Text("S/ ${String.format("%.2f", income.amount)} (${String.format("%.1f", percentage)}%)", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IncomeReportScreenPreview() {
    val sampleIncomes = listOf(
        CategoryIncome("Trabajo principal", 5200.00f, primaryLight),
        CategoryIncome("Medio tiempo", 950.50f, secondaryLight),
        CategoryIncome("Recurrente", 300.00f, tertiaryLight),
        CategoryIncome("Otros", 150.00f, Color(0xFF8B5CF6))
    )
    
    CASHiTOTheme {
        IncomeReportScreenContent(
            uiState = IncomeReportUiState(
                incomes = sampleIncomes,
                isLoading = false
            ),
            onNavigateBack = {}
        )
    }
}
