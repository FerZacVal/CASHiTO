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
import com.cashito.ui.components.buttons.SecondaryButton
import com.cashito.ui.theme.Spacing

@Composable
fun HomeScreen(navController: NavController? = null) {
    val screens = listOf(
        "Dashboard" to Routes.DASHBOARD,
        "Splash" to Routes.SPLASH,
        "Login" to Routes.LOGIN,
        "Auth (Legacy)" to Routes.AUTH,
        "Categories (Legacy)" to Routes.CATEGORY,
        "Expenses (Legacy)" to Routes.EXPENSE,
        "Recommendations (Legacy)" to Routes.RECOMMENDATION,
        "Reports (Legacy)" to Routes.REPORT,
        "Savings (Legacy)" to Routes.SAVINGS,
        "Goal Detail" to "${Routes.GOAL_DETAIL.substringBefore('{')}1",
        "Goal Form" to Routes.GOAL_FORM,
        "Profile" to Routes.PROFILE,
        "Quick Save" to Routes.QUICK_SAVE,
        "Transactions" to Routes.TRANSACTIONS
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CASHiTO Dev Menu",
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
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(screens) { (title, route) ->
                SecondaryButton(
                    text = title,
                    onClick = { navController?.navigate(route) }
                )
            }
        }
    }
}