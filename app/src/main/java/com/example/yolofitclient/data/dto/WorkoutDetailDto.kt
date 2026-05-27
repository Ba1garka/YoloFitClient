package com.example.yolofitclient.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkoutDetailDto(
    @SerialName("id")
    val id: Int?,

    @SerialName("userId")
    val userId: Int?,

    @SerialName("userName")
    val userName: String? = null,

    @SerialName("workoutDate")
    val workoutDate: String?,

    @SerialName("completed")
    val completed: Boolean? = false,

    @SerialName("totalCalories")
    val totalCalories: Double? = 0.0,

    @SerialName("exerciseSets")
    val exerciseSets: List<ExerciseSetDetailDto>? = emptyList()
)