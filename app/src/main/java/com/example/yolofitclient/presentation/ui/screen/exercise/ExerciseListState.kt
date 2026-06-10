package com.example.yolofitclient.presentation.ui.screen.exercise

import com.example.yolofitclient.domain.entity.ExerciseEntity

sealed interface ExerciseListState{
    data class Error(val reason : String): ExerciseListState
    data object Loading : ExerciseListState
    data class Content(
        val users: List<ExerciseEntity>
    ) : ExerciseListState
}