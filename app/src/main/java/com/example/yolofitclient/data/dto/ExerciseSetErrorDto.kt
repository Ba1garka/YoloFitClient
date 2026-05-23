package com.example.yolofitclient.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseSetErrorDto(
    @SerialName("id")
    val id: Long?,

    @SerialName("exerciseSetId")
    val exerciseSetId: Long?,

    @SerialName("repNumber")
    val repNumber: Int?,

    @SerialName("joint")
    val joint: String?,

    @SerialName("errorType")
    val errorType: String?,

    @SerialName("deviationAngle")
    val deviationAngle: Double? = null
)