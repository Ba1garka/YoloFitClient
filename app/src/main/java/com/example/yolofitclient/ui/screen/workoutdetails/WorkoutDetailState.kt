package com.example.yolofitclient.ui.screen.workoutdetails

import com.example.yolofitclient.domain.entity.WorkoutDetailEntity

sealed interface WorkoutDetailState {
    data object Loading : WorkoutDetailState
    data class Error(val reason: String) : WorkoutDetailState
    data class Content(val detail: WorkoutDetailEntity) : WorkoutDetailState
}