package com.cashito.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.Routes

@Composable
fun HomeScreen(navController: NavController? = null) {
    val screens = listOf(
        "Auth" to Routes.AUTH,
        "Categories" to Routes.CATEGORY,
        "Expenses" to Routes.EXPENSE,
        "Recommendations" to Routes.RECOMMENDATION,
        "Reports" to Routes.REPORT,
        "Savings" to Routes.SAVINGS
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CASHiTO",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 32.dp)
        )
        
        Text(
            text = "Personal Finance Manager",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(screens) { (title, route) ->
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController?.navigate(route) }
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}


