package com.example.yolofitclient.domain.entity

class WorkoutEntity(
    val id: Int,
    val userId: Int,
    val userName: String,
    val workoutDate: String,
    val completed: Boolean,
    val exercises: List<ExerciseEntity>,
    val startTime: String
)