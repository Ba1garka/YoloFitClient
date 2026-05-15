package com.example.yolofitclient.domain.entity

class WorkoutEntity(
    val id: Long,
    val userId: Long,
    val userName: String,
    val workoutDate: String,
    val completed: Boolean,
    val exercises: List<ExerciseEntity>
)