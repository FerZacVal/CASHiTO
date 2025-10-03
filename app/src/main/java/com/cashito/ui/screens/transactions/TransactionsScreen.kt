package com.cashito.ui.screens.transactions

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.ChipDefaults
import com.cashito.ui.components.inputs.CashitoSearchField
import com.cashito.ui.theme.Background
import com.cashito.ui.theme.ComponentSize
import com.cashito.ui.theme.PrimaryGreen
import com.cashito.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    navController: NavController,
    onNavigateBack: () -> Unit = { navController.popBackStack() },
    onNavigateToNewTransaction: () -> Unit = { navController.navigate("new_transaction") }
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }
    
    val filters = listOf("Todos", "Depósitos", "Retiros", "Metas")
    val transactions = getSampleTransactions()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Movimientos",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle filter */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = onNavigateToNewTransaction,
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ) {
                Icons.Default.Add
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg)
        ) {
            // Search Bar
            item {
                CashitoSearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Buscar por descripción, monto...",
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item { Spacer(modifier = Modifier.height(Spacing.lg)) }
            
            // Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    items(filters) { filter ->
                        FilterChip(
                            text = filter,
                            isSelected = selectedFilter == filter,
                            onClick = { selectedFilter = filter }
                        )
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(Spacing.xl)) }
            
            // Transactions by Date
            items(getGroupedTransactions(transactions, selectedFilter, searchQuery)) { group ->
                TransactionGroup(
                    group = group,
                    onTransactionClick = { /* Handle transaction click */ }
                )
                Spacer(modifier = Modifier.height(Spacing.lg))
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Chip(
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        },
        colors = ChipDefaults.chipColors(
            containerColor = if (isSelected) PrimaryGreen else Color.White,
            labelColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) PrimaryGreen else Color.Gray.copy(alpha = 0.3f)
        )
    )
}

@Composable
fun TransactionGroup(
    group: TransactionGroup,
    onTransactionClick: (Transaction) -> Unit
) {
    Column {
        Text(
            text = group.date,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(Spacing.md))
        
        group.transactions.forEach { transaction ->
            TransactionItem(
                transaction = transaction,
                onClick = { onTransactionClick(transaction) }
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
                Text(
                    text = transaction.icon,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Spacer(modifier = Modifier.width(Spacing.md))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = transaction.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = transaction.amount,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = transaction.amountColor
                )
                if (transaction.category.isNotEmpty()) {
                    Text(
                        text = transaction.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

// Data classes
data class Transaction(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: String,
    val amountColor: Color,
    val icon: String,
    val color: Color,
    val category: String = "",
    val date: String
)

data class TransactionGroup(
    val date: String,
    val transactions: List<Transaction>
)

// Sample data
fun getSampleTransactions(): List<Transaction> = listOf(
    Transaction("1", "Depósito automático", "Hoy, 09:30", "+S/ 200", PrimaryGreen, "💰", PrimaryGreen, "Ahorro", "Hoy"),
    Transaction("2", "Compra en supermercado", "Hoy, 18:45", "-S/ 85.50", Color.Red, "🛒", Color(0xFF3B82F6), "Compras", "Hoy"),
    Transaction("3", "Transferencia recibida", "Ayer, 14:20", "+S/ 500", PrimaryGreen, "📥", Color(0xFF10B981), "Transferencia", "Ayer"),
    Transaction("4", "Pago de servicios", "Ayer, 10:15", "-S/ 120", Color.Red, "💡", Color(0xFFF59E0B), "Servicios", "Ayer"),
    Transaction("5", "Ahorro redondeo", "Hace 2 días, 16:30", "+S/ 15.50", PrimaryGreen, "🔄", Color(0xFF8B5CF6), "Redondeo", "Hace 2 días"),
    Transaction("6", "Compra de ropa", "Hace 2 días, 12:00", "-S/ 180", Color.Red, "👕", Color(0xFFEC4899), "Ropa", "Hace 2 días"),
    Transaction("7", "Depósito manual", "Hace 3 días, 20:00", "+S/ 300", PrimaryGreen, "💳", PrimaryGreen, "Ahorro", "Hace 3 días"),
    Transaction("8", "Pago de transporte", "Hace 3 días, 08:30", "-S/ 25", Color.Red, "🚌", Color(0xFF6366F1), "Transporte", "Hace 3 días")
)

fun getGroupedTransactions(
    transactions: List<Transaction>,
    filter: String,
    searchQuery: String
): List<TransactionGroup> {
    val filteredTransactions = transactions.filter { transaction ->
        val matchesFilter = when (filter) {
            "Depósitos" -> transaction.amount.startsWith("+")
            "Retiros" -> transaction.amount.startsWith("-")
            "Metas" -> transaction.category == "Ahorro"
            else -> true
        }
        
        val matchesSearch = searchQuery.isEmpty() || 
            transaction.title.contains(searchQuery, ignoreCase = true) ||
            transaction.amount.contains(searchQuery, ignoreCase = true)
        
        matchesFilter && matchesSearch
    }
    
    return filteredTransactions
        .groupBy { it.date }
        .map { (date, transactions) ->
            TransactionGroup(date, transactions)
        }
        .sortedByDescending { group ->
            when (group.date) {
                "Hoy" -> 0
                "Ayer" -> 1
                "Hace 2 días" -> 2
                "Hace 3 días" -> 3
                else -> 4
            }
        }
}
