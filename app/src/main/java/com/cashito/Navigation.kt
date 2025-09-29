package com.cashito

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cashito.ui.screens.auth.AuthScreen
import com.cashito.ui.screens.category.CategoryScreen
import com.cashito.ui.screens.expense.ExpenseScreen
import com.cashito.ui.screens.home.HomeScreen
import com.cashito.ui.screens.recommendation.RecommendationScreen
import com.cashito.ui.screens.report.ReportScreen
import com.cashito.ui.screens.savings.SavingsScreen

object Routes {
    const val HOME = "home"
    const val AUTH = "auth"
    const val CATEGORY = "category"
    const val EXPENSE = "expense"
    const val RECOMMENDATION = "recommendation"
    const val REPORT = "report"
    const val SAVINGS = "savings"
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.HOME
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.HOME) { HomeScreen() }
        composable(Routes.AUTH) { AuthScreen() }
        composable(Routes.CATEGORY) { CategoryScreen() }
        composable(Routes.EXPENSE) { ExpenseScreen() }
        composable(Routes.RECOMMENDATION) { RecommendationScreen() }
        composable(Routes.REPORT) { ReportScreen() }
        composable(Routes.SAVINGS) { SavingsScreen() }
    }
}

