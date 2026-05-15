package com.example.yolofitclient.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkoutDto(
    @SerialName("id")
    val id: Long?,

    @SerialName("userId")
    val userId: Long?,

    @SerialName("userName")
    val userName: String? = null,

    @SerialName("workoutDate")
    val workoutDate: String?,

    @SerialName("completed")
    val completed: Boolean? = false,

    @SerialName("exercises")
    val exercises: List<ExerciseDto>? = emptyList()
)