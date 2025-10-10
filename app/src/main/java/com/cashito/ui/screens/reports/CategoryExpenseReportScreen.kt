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
import com.cashito.ui.theme.errorLight
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import com.cashito.ui.theme.tertiaryLight
import com.cashito.ui.viewmodel.CategoryExpense
import com.cashito.ui.viewmodel.CategoryExpenseReportUiState
import com.cashito.ui.viewmodel.CategoryExpenseReportViewModel

@Composable
fun CategoryExpenseReportScreen(
    navController: NavController,
    viewModel: CategoryExpenseReportViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CategoryExpenseReportScreenContent(
        uiState = uiState,
        onNavigateBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryExpenseReportScreenContent(
    uiState: CategoryExpenseReportUiState,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gastos por Categoría", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold) },
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
                    Text("Distribución de tus gastos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = Spacing.md))
                }
                item {
                    PieChart(
                        entries = uiState.expenses.map { PieChartEntry(it.amount, it.color) },
                        modifier = Modifier.size(250.dp),
                        emptyChartMessage = "Sin gastos"
                    )
                    Spacer(modifier = Modifier.height(Spacing.xl))
                }
                item {
                    ExpenseCategoryLegend(expenses = uiState.expenses)
                    Spacer(modifier = Modifier.height(Spacing.lg))
                }
            }
        }
    }
}

@Composable
fun ExpenseCategoryLegend(expenses: List<CategoryExpense>) {
    val totalAmount = expenses.sumOf { it.amount.toDouble() }.toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            if (totalAmount == 0f) {
                Text(
                    text = "No hay datos de gastos para mostrar.",
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = Spacing.md),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                expenses.forEach { expense ->
                    val percentage = (expense.amount / totalAmount) * 100
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(16.dp).background(expense.color, shape = CircleShape))
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Text(expense.categoryName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        }
                        Text("S/ ${String.format("%.2f", expense.amount)} (${String.format("%.1f", percentage)}%)", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryExpenseReportScreenPreview() {
    val sampleExpenses = listOf(
        CategoryExpense("Comida", 750.50f, primaryLight),
        CategoryExpense("Transporte", 320.00f, secondaryLight),
        CategoryExpense("Ocio", 450.75f, tertiaryLight),
        CategoryExpense("Ropa", 250.00f, Color(0xFFF59E0B)),
        CategoryExpense("Pagos", 1200.00f, errorLight)
    )

    CASHiTOTheme {
        CategoryExpenseReportScreenContent(
            uiState = CategoryExpenseReportUiState(
                expenses = sampleExpenses,
                isLoading = false
            ),
            onNavigateBack = {}
        )
    }
}
