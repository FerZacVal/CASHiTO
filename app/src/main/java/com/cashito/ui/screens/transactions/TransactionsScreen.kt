package com.cashito.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.ui.components.inputs.CashitoSearchField
import com.cashito.ui.theme.ComponentSize
import com.cashito.ui.theme.Spacing
import com.cashito.ui.viewmodel.Transaction
import com.cashito.ui.viewmodel.TransactionGroup
import com.cashito.ui.viewmodel.TransactionsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    navController: NavController,
    viewModel: TransactionsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = { navController.popBackStack() },
    onNavigateToNewTransaction: () -> Unit = { navController.navigate("quick_save") } // Or a more general screen
) {
    val uiState by viewModel.uiState.collectAsState()
    val filters = listOf("Todos", "Ingresos", "Gastos")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movimientos", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                actions = { IconButton(onClick = { /* TODO: Show filter dialog */ }) { Icon(Icons.Default.FilterList, contentDescription = "Filter") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNewTransaction) {
                Icon(Icons.Default.Add, contentDescription = "Añadir transacción")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = Spacing.lg)
        ) {
            CashitoSearchField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = "Buscar...",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                items(filters) { filter ->
                    CustomFilterChip(
                        text = filter,
                        isSelected = uiState.selectedFilter == filter,
                        onClick = { viewModel.onFilterChanged(filter) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                    items(uiState.filteredTransactions) { group ->
                        TransactionGroup(group = group, onTransactionClick = { /* Handle click */ })
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CustomFilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text, style = MaterialTheme.typography.labelLarge) },
        leadingIcon = if (isSelected) { { Icon(Icons.Filled.Done, "Selected", Modifier.size(FilterChipDefaults.IconSize)) } } else { null },
    )
}

@Composable
fun TransactionGroup(group: TransactionGroup, onTransactionClick: (Transaction) -> Unit) {
    Column {
        Text(
            text = group.date,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
            Column {
                group.transactions.forEach { transaction ->
                    TransactionItem(transaction = transaction, onClick = { onTransactionClick(transaction) })
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(ComponentSize.iconSize)
                .clip(CircleShape)
                .background(transaction.color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = transaction.icon, style = MaterialTheme.typography.bodyLarge, color = transaction.color)
        }
        Spacer(modifier = Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(transaction.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(transaction.amount, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = transaction.amountColor)
            if (transaction.category.isNotEmpty()) {
                Text(transaction.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
