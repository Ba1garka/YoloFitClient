package com.example.yolofitclient.ui.screen.myworkout

import com.example.yolofitclient.domain.entity.WorkoutEntity

sealed interface MyWorkoutState  {
    data class Error(val reason : String): MyWorkoutState
    data object Loading : MyWorkoutState
    data class Content(val workouts: List<WorkoutEntity> ) : MyWorkoutState
    data object Success : MyWorkoutState
}