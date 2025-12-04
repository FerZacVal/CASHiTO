package com.cashito.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.gamification.Reward
import com.cashito.domain.entities.gamification.WeeklyChallenge
import com.cashito.domain.usecases.gamification.ClaimRewardUseCase
import com.cashito.domain.usecases.gamification.GetUserRewardsUseCase
import com.cashito.domain.usecases.gamification.GetWeeklyChallengeUseCase
import com.cashito.domain.usecases.gamification.UseRewardUseCase
import com.cashito.domain.usecases.goal.GetGoalsUseCase
import com.cashito.domain.entities.goal.Goal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

data class RewardsUiState(
    val weeklyChallenge: WeeklyChallenge? = null,
    val userRewards: List<Reward> = emptyList(),
    val availableGoals: List<Goal> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastClaimedReward: Reward? = null,
    val showRewardDialog: Boolean = false
)

data class ProjectedProfit(
    val rewardApr: Double,
    val goalSavedAmount: Double,
    val profit: Double,
    val endDate: Date
)

class RewardsViewModel(
    private val getWeeklyChallengeUseCase: GetWeeklyChallengeUseCase,
    private val claimRewardUseCase: ClaimRewardUseCase,
    private val getUserRewardsUseCase: GetUserRewardsUseCase,
    private val useRewardUseCase: UseRewardUseCase,
    private val getGoalsUseCase: GetGoalsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RewardsUiState())
    val uiState: StateFlow<RewardsUiState> = _uiState.asStateFlow()

    init {
        observeChallenge()
        observeRewards()
        observeGoals()
    }

    private fun observeChallenge() {
        viewModelScope.launch {
            getWeeklyChallengeUseCase().collect { challenge ->
                _uiState.update { it.copy(weeklyChallenge = challenge) }
            }
        }
    }

    private fun observeRewards() {
        viewModelScope.launch {
            getUserRewardsUseCase().collect { rewards ->
                _uiState.update { it.copy(userRewards = rewards) }
            }
        }
    }

    private fun observeGoals() {
        viewModelScope.launch {
            getGoalsUseCase().collect { goals ->
                _uiState.update { it.copy(availableGoals = goals) }
            }
        }
    }

    fun claimReward() {
        val challenge = _uiState.value.weeklyChallenge ?: return
        if (!challenge.isCompleted || challenge.isRewardClaimed) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val reward = claimRewardUseCase(challenge.id)
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        lastClaimedReward = reward,
                        showRewardDialog = true
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun dismissRewardDialog() {
        _uiState.update { it.copy(showRewardDialog = false, lastClaimedReward = null) }
    }

    fun applyRewardToGoal(rewardId: String, goalId: String) {
        viewModelScope.launch {
            useRewardUseCase(rewardId, goalId)
        }
    }

    fun calculateProjectedProfit(rewardId: String, goalId: String): ProjectedProfit? {
        val reward = _uiState.value.userRewards.find { it.id == rewardId } ?: return null
        val goal = _uiState.value.availableGoals.find { it.id == goalId } ?: return null
        
        val apr = reward.value
        val dailyRate = apr / 365.0 / 100.0
        val duration = reward.durationDays
        
        // Simple interest calculation for the boost period
        // Profit = Principal * Daily Rate * Days
        val profit = goal.savedAmount * dailyRate * duration
        
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, duration)
        
        return ProjectedProfit(
            rewardApr = apr,
            goalSavedAmount = goal.savedAmount,
            profit = profit,
            endDate = calendar.time
        )
    }
}
