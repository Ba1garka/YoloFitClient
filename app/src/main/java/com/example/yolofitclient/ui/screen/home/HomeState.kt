package com.example.yolofitclient.ui.screen.home

import com.example.yolofitclient.domain.entity.WorkoutEntity

sealed class HomeState {
    data object Loading : HomeState()
    data class Error(val reason: String) : HomeState()
    data class Content(val todayWorkouts: List<WorkoutEntity>) : HomeState()
}