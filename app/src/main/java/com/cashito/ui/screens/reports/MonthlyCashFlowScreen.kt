package com.cashito.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.domain.usecases.reports.MonthlyCashFlow
import com.cashito.ui.theme.CASHiTOTheme
import com.cashito.ui.theme.Spacing
import com.cashito.ui.viewmodel.MonthlyCashFlowUiState
import com.cashito.ui.viewmodel.MonthlyCashFlowViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MonthlyCashFlowScreen(
    navController: NavController,
    viewModel: MonthlyCashFlowViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MonthlyCashFlowScreenContent(
        uiState = uiState,
        onNavigateBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyCashFlowScreenContent(
    uiState: MonthlyCashFlowUiState,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flujo de Caja Mensual", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay movimientos registrados", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                ) {
                    items(uiState.items) { item ->
                        MonthlyCashFlowCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyCashFlowCard(item: MonthlyCashFlow) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            // Título del Mes
            Text(
                text = item.monthLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.md), color = MaterialTheme.colorScheme.surfaceVariant)

            // Fila de Ingresos y Gastos
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Ingresos", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "S/ ${"%.2f".format(item.totalIncome)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF4CAF50), // Verde
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Gastos", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "S/ ${"%.2f".format(item.totalExpense)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Caja de Resultado Neto
            val isPositive = item.netBalance >= 0
            val containerColor = if (isPositive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            val contentColor = if (isPositive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error

            Card(
                colors = CardDefaults.cardColors(containerColor = containerColor),
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.md, vertical = Spacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Flujo Neto:", style = MaterialTheme.typography.bodyMedium, color = contentColor)
                    Text(
                        "S/ ${"%.2f".format(item.netBalance)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
            }
        }
    }
}

// ------------------------------------------------------------------------
// PREVIEW SECTION
// ------------------------------------------------------------------------

@Preview(showBackground = true, name = "Flujo de Caja Mensual - Datos")
@Composable
fun MonthlyCashFlowScreenPreview() {
    // Datos falsos para la vista previa
    val sampleData = listOf(
        MonthlyCashFlow(
            monthLabel = "Diciembre 2024",
            year = 2024,
            monthIndex = 11,
            totalIncome = 5200.50,
            totalExpense = 3100.00,
            netBalance = 2100.50
        ),
        MonthlyCashFlow(
            monthLabel = "Noviembre 2024",
            year = 2024,
            monthIndex = 10,
            totalIncome = 4500.00,
            totalExpense = 4800.00, // Mes con pérdida
            netBalance = -300.00
        ),
        MonthlyCashFlow(
            monthLabel = "Octubre 2024",
            year = 2024,
            monthIndex = 9,
            totalIncome = 6000.00,
            totalExpense = 2000.00,
            netBalance = 4000.00
        )
    )

    CASHiTOTheme {
        MonthlyCashFlowScreenContent(
            uiState = MonthlyCashFlowUiState(
                isLoading = false,
                items = sampleData
            ),
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Flujo de Caja Mensual - Loading")
@Composable
fun MonthlyCashFlowScreenLoadingPreview() {
    CASHiTOTheme {
        MonthlyCashFlowScreenContent(
            uiState = MonthlyCashFlowUiState(isLoading = true),
            onNavigateBack = {}
        )
    }
}
