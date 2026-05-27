package com.example.yolofitclient.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseSetDetailDto(
    @SerialName("id")
    val id: Int?,

    @SerialName("exerciseName")
    val exerciseName: String?,

    @SerialName("setNumber")
    val setNumber: Int?,

    @SerialName("repsDone")
    val repsDone: Int?,

    @SerialName("weightDone")
    val weightDone: Double?,

    @SerialName("caloriesBurned")
    val caloriesBurned: Double? = 0.0,

    @SerialName("mistakeCount")
    val mistakeCount: Int? = 0,
)