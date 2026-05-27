package com.example.yolofitclient.domain.entity

data class ExerciseSetDetailEntity(
    val id: Int,
    val exerciseName: String,
    val setNumber: Int,
    val repsDone: Int?,
    val weightDone: Double?,
    val caloriesBurned: Double,
    val mistakeCount: Int,
)