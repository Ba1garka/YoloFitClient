package com.example.yolofitclient.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseSetDto(
    @SerialName("id")
    val id: Long?,

    @SerialName("workoutId")
    val workoutId: Long,

    @SerialName("exerciseId")
    val exerciseId: Long,

    @SerialName("exerciseName")
    val exerciseName: String?,

    @SerialName("setNumber")
    val setNumber: Int?,

    @SerialName("repsDone")
    val repsDone: Int? = null,

    @SerialName("weightDone")
    val weightDone: Double? = null,

    @SerialName("mistakeCount")
    val mistakeCount: Int? = 0
)