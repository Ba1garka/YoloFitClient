package com.example.yolofitclient.ui.screen.home

import com.example.yolofitclient.domain.entity.WorkoutEntity

sealed interface HomeState {
    data object Loading : HomeState
    data class Error(val reason: String) : HomeState
    data class Content(
        val todayWorkouts: List<WorkoutEntity>,
        val dailyCalories: Int = 0,
        val dailyCalorieTarget: Int?
    ) : HomeState
}