package com.cashito.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cashito.Routes
import com.cashito.ui.components.cards.ReportNavigationCard
import com.cashito.ui.theme.CASHiTOTheme
import com.cashito.ui.theme.Spacing
import com.cashito.ui.viewmodel.ReportsUiState
import com.cashito.ui.viewmodel.ReportsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReportsScreen(
    navController: NavController,
    viewModel: ReportsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ReportsScreenContent(
        uiState = uiState,
        onNavigateBack = { navController.popBackStack() },
        onNavigateToCategoryReport = { navController.navigate(Routes.CATEGORY_EXPENSE_REPORT) },
        onNavigateToIncomeReport = { navController.navigate(Routes.INCOME_REPORT) },
        onNavigateToBalanceReport = { navController.navigate(Routes.BALANCE_REPORT) },
        onNavigateToCashFlow = { navController.navigate(Routes.MONTHLY_CASH_FLOW) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreenContent(
    uiState: ReportsUiState,
    onNavigateBack: () -> Unit,
    onNavigateToCategoryReport: () -> Unit,
    onNavigateToIncomeReport: () -> Unit,
    onNavigateToBalanceReport: () -> Unit,
    onNavigateToCashFlow: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen Financiero", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, titleContentColor = MaterialTheme.colorScheme.onSurface)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            if (uiState.isLoading) {
                item {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                item { SummaryCard(income = uiState.income, expenses = uiState.expenses, balance = uiState.balance) }
                item { ReportNavigationCard("Desglose de Ingresos", "Analiza tus ingresos por cada categoría", onNavigateToIncomeReport) }
                item { ReportNavigationCard("Desglose de Gastos", "Analiza tus gastos por cada categoría", onNavigateToCategoryReport) }
                item { ReportNavigationCard("Análisis de Balances", "Revisa la evolución de tu saldo", onNavigateToBalanceReport) }
                item { ReportNavigationCard("Flujo de Caja Mensual", "Descubre tus movimientos diarios", onNavigateToCashFlow) }
            }
        }
    }
}

@Composable
fun SummaryCard(income: String, expenses: String, balance: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(Spacing.lg)) {
            Text("Resumen del Mes", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(Spacing.md))
            SummaryRow("Ingresos", income, MaterialTheme.colorScheme.primary)
            SummaryRow("Gastos", expenses, MaterialTheme.colorScheme.error)
            SummaryRow("Saldo Disponible", balance, MaterialTheme.colorScheme.onSurface, isBold = true)
        }
    }
}

@Composable
fun SummaryRow(label: String, amount: String, color: androidx.compose.ui.graphics.Color, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(amount, style = MaterialTheme.typography.bodyLarge, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, color = color)
    }
}

@Preview(showBackground = true)
@Composable
fun ReportsScreenPreview() {
    CASHiTOTheme {
        ReportsScreenContent(
            uiState = ReportsUiState(
                income = "S/ 5,234.50",
                expenses = "S/ 1,890.20",
                balance = "S/ 3,344.30",
                isLoading = false
            ),
            onNavigateBack = {},
            onNavigateToCategoryReport = {},
            onNavigateToIncomeReport = {},
            onNavigateToBalanceReport = {},
            onNavigateToCashFlow = {}
        )
    }
}