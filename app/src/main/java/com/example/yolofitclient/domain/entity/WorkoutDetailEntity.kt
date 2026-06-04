package com.example.yolofitclient.domain.entity

data class WorkoutDetailEntity(
    val id: Int,
    val userId: Int,
    val userName: String?,
    val workoutDate: String,
    val completed: Boolean,
    val totalCalories: Double,
    val exerciseSets: List<ExerciseSetDetailEntity>,
    val startTime: String
)
