package com.example.yolofitclient.presentation.ui.screen.calendar

import com.example.yolofitclient.domain.entity.WorkoutEntity

sealed interface CalendarState {
    data object Loading : CalendarState
    data class Error(val reason: String) : CalendarState
    data class Content(val workouts: List<WorkoutEntity>) : CalendarState
}