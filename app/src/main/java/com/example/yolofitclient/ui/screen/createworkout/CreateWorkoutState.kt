package com.example.yolofitclient.ui.screen.createworkout



sealed interface CreateWorkoutState  {
    data class Error(val reason : String): CreateWorkoutState
    data object Loading : CreateWorkoutState
    data object Content : CreateWorkoutState
    data object Success : CreateWorkoutState
}