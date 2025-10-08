package com.cashito

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cashito.ui.screens.auth.AuthScreen
import com.cashito.ui.screens.category.CategoryScreen
import com.cashito.ui.screens.dashboard.DashboardScreen
import com.cashito.ui.screens.expense.ExpenseScreen
import com.cashito.ui.screens.goal_detail.GoalDetailScreen
import com.cashito.ui.screens.goal_form.GoalFormScreen
import com.cashito.ui.screens.home.HomeScreen
import com.cashito.ui.screens.login.LoginScreen
import com.cashito.ui.screens.profile.ProfileScreen
import com.cashito.ui.screens.quick_save.QuickSaveScreen
import com.cashito.ui.screens.recommendation.RecommendationScreen
import com.cashito.ui.screens.report.ReportScreen
import com.cashito.ui.screens.savings.SavingsScreen
import com.cashito.ui.screens.splash.SplashScreen
import com.cashito.ui.screens.transactions.TransactionsScreen

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val GOAL_DETAIL = "goal_detail/{goalId}"
    const val GOAL_FORM = "goal_form"
    const val TRANSACTIONS = "transactions"
    const val QUICK_SAVE = "quick_save"
    const val INSIGHTS = "insights"
    const val PROFILE = "profile"
    const val NOTIFICATIONS = "notifications"
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
    startDestination: String = Routes.SPLASH
) {
    NavHost(navController = navController, startDestination = startDestination) {
        // New Cashito screens
        composable(Routes.SPLASH) { SplashScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.DASHBOARD) { DashboardScreen(navController) }
        composable(Routes.GOAL_DETAIL) { goalId ->
            GoalDetailScreen(
                goalId = goalId.arguments?.getString("goalId") ?: "",
                navController = navController
            )
        }
        composable(Routes.TRANSACTIONS) { TransactionsScreen(navController) }
        composable(Routes.QUICK_SAVE) { QuickSaveScreen(navController) }
        composable(Routes.GOAL_FORM) { GoalFormScreen(navController) }
        composable(Routes.PROFILE) { ProfileScreen(navController) }
        
        // Legacy screens
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.AUTH) { AuthScreen(navController) }
        composable(Routes.CATEGORY) { CategoryScreen(navController) }
        composable(Routes.EXPENSE) { ExpenseScreen(navController) }
        composable(Routes.RECOMMENDATION) { RecommendationScreen(navController) }
        composable(Routes.REPORT) { ReportScreen(navController) }
        composable(Routes.SAVINGS) { SavingsScreen(navController) }
    }
}

