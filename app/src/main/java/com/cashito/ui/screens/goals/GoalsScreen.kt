package com.cashito.ui.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cashito.R
import com.cashito.Routes
import com.cashito.ui.components.cards.GoalCard
import com.cashito.ui.theme.CASHiTOTheme
import com.cashito.ui.theme.Spacing
import com.cashito.ui.theme.primaryLight
import com.cashito.ui.theme.secondaryLight
import com.cashito.ui.viewmodel.Goal
import com.cashito.ui.viewmodel.GoalsUiState
import com.cashito.ui.viewmodel.GoalsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GoalsScreen(
    navController: NavController,
    viewModel: GoalsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    GoalsScreenContent(
        uiState = uiState,
        onNavigateToGoalDetail = { goalId -> navController.navigate("goal_detail/$goalId") },
        onNavigateToCreateGoal = { navController.navigate(Routes.GOAL_FORM) },
        onNavigateBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreenContent(
    uiState: GoalsUiState,
    onNavigateToGoalDetail: (String) -> Unit,
    onNavigateToCreateGoal: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.goals_screen_title), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.goals_screen_back_button_description))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateGoal) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.goals_screen_create_goal_button_description))
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            if (uiState.isLoading) {
                item {
                    CircularProgressIndicator()
                }
            } else {
                items(uiState.goals) { goal ->
                    GoalCard(
                        title = goal.title,
                        savedAmount = goal.savedAmount,
                        targetAmount = goal.targetAmount,
                        progress = goal.progress,
                        icon = goal.icon,
                        color = goal.color,
                        onClick = { onNavigateToGoalDetail(goal.id) },
                        modifier = Modifier.fillMaxWidth() // Make the card fill the width
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GoalsScreenPreview() {
    CASHiTOTheme {
        GoalsScreenContent(
            uiState = GoalsUiState(
                goals = listOf(
                    Goal("1", "Viaje a Cusco", "3,420", "5,000", 0.65f, "✈️", primaryLight),
                    Goal("2", "Laptop nueva", "800", "4,500", 0.18f, "💻", secondaryLight),
                    Goal("3", "Ahorro de emergencia", "1,200", "10,000", 0.12f, "🛡️", Color.Gray)
                ),
                isLoading = false
            ),
            onNavigateToGoalDetail = {},
            onNavigateToCreateGoal = {},
            onNavigateBack = {}
        )
    }
}
