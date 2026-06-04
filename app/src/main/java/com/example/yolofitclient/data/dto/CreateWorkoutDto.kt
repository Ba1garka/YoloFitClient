package com.example.yolofitclient.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateWorkoutDto(
    @SerialName("userId")
    val userId: Int?,

    @SerialName("workoutDate")
    val workoutDate: String,

    @SerialName("completed")
    val completed: Boolean = false,

    @SerialName("exerciseIds")
    val exerciseIds: List<Int> = emptyList(),

    @SerialName("startTime")
    val startTime: String
)