package com.cashito

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cashito.ui.screens.category.CategoryEditScreen
import com.cashito.ui.screens.category.CategoriesScreen
import com.cashito.ui.screens.category_form.CategoryFormScreen
import com.cashito.ui.screens.dashboard.DashboardScreen
import com.cashito.ui.screens.goal_detail.GoalDetailScreen
import com.cashito.ui.screens.goal_form.GoalFormScreen
import com.cashito.ui.screens.goals.GoalsScreen
import com.cashito.ui.screens.login.CreateUserScreen
import com.cashito.ui.screens.login.LoginScreen
import com.cashito.ui.screens.profile.ProfileScreen
import com.cashito.ui.screens.quick_out.QuickOutScreen
import com.cashito.ui.screens.quick_save.QuickSaveScreen
import com.cashito.ui.screens.reports.BalanceScreen
import com.cashito.ui.screens.reports.CategoryExpenseReportScreen
import com.cashito.ui.screens.reports.IncomeReportScreen
import com.cashito.ui.screens.reports.ReportsScreen
import com.cashito.ui.screens.rewards.RewardsScreen
import com.cashito.ui.screens.splash.SplashScreen
import com.cashito.ui.screens.transactions.TransactionEditScreen
import com.cashito.ui.screens.transactions.TransactionsScreen
import com.cashito.ui.screens.reports.MonthlyCashFlowScreen

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val GOAL_DETAIL = "goal_detail/{goalId}"
    const val GOAL_FORM = "goal_form"
    const val GOALS = "goals"
    const val TRANSACTIONS = "transactions"
    const val QUICK_SAVE = "quick_save"
    const val QUICK_OUT = "quick_out"
    const val REPORTS = "reports"
    const val CATEGORY_EXPENSE_REPORT = "category_expense_report"
    const val INCOME_REPORT = "income_report"
    const val BALANCE_REPORT = "balance_report"
    const val PROFILE = "profile"
    const val CATEGORY_FORM = "category_form"
    const val CATEGORIES = "categories"
    const val REWARDS = "rewards"
    const val CATEGORY_EDIT = "category_edit/{categoryId}" // ACTUALIZADO
    // ARREGLADO: La ruta debe coincidir con cómo la usamos.
    const val TRANSACTION_EDIT = "transaction_edit/{transactionId}"
    const val MONTHLY_CASH_FLOW = "monthly_cash_flow"
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SPLASH
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.SPLASH) { SplashScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.REGISTER) { CreateUserScreen(navController) }
        composable(Routes.DASHBOARD) { DashboardScreen(navController) }
        composable(
            route = Routes.GOAL_DETAIL,
            arguments = listOf(navArgument("goalId") { type = NavType.StringType })
        ) {
            GoalDetailScreen(navController = navController)
        }
        composable(Routes.GOALS) { GoalsScreen(navController) }
        composable(Routes.TRANSACTIONS) { TransactionsScreen(navController) }

        // ARREGLADO: Definimos la ruta para que acepte el goalId opcional.
        composable(
            route = "${Routes.QUICK_SAVE}?goalId={goalId}",
            arguments = listOf(navArgument("goalId") {
                type = NavType.StringType
                nullable = true
            })
        ) {
            QuickSaveScreen(navController)
        }

        composable(Routes.QUICK_OUT) { QuickOutScreen(navController) }
        composable(Routes.REPORTS) { ReportsScreen(navController) }
        composable(Routes.CATEGORY_EXPENSE_REPORT) { CategoryExpenseReportScreen(navController) }
        composable(Routes.INCOME_REPORT) { IncomeReportScreen(navController) }
        composable(Routes.BALANCE_REPORT) { BalanceScreen(navController) }
        composable(Routes.PROFILE) { ProfileScreen(navController) }
        composable(Routes.CATEGORY_FORM) { CategoryFormScreen(navController) }
        composable(Routes.CATEGORIES) { CategoriesScreen(navController) }
        composable(Routes.REWARDS) { RewardsScreen(navController) }

        composable(
            route = "${Routes.GOAL_FORM}?goalId={goalId}",
            arguments = listOf(navArgument("goalId") { type = NavType.StringType; nullable = true })
        ) { 
            GoalFormScreen(navController = navController) 
        }
        composable(
            route = Routes.CATEGORY_EDIT, // ACTUALIZADO
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) {
            CategoryEditScreen(navController) // ACTUALIZADO
        }
        composable(
            route = Routes.TRANSACTION_EDIT, // Ahora usa la constante correcta
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) {
            TransactionEditScreen(navController)
        }
        composable(Routes.MONTHLY_CASH_FLOW) {
            MonthlyCashFlowScreen(navController = navController)
        }
    }
}
