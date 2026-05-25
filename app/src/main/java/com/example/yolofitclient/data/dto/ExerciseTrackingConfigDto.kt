package com.example.yolofitclient.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseTrackingConfigDto(
    @SerialName("id")
    val id: Int?,

    @SerialName("exerciseId")
    val exerciseId: Int?,

    @SerialName("exerciseName")
    val exerciseName: String?,

    @SerialName("jointIndices")
    val jointIndices: String?,

    @SerialName("angleDown")
    val angleDown: Double?,

    @SerialName("angleUp")
    val angleUp: Double?,

    @SerialName("countDirection")
    val countDirection: String?,

    @SerialName("minConfidence")
    val minConfidence: Double?,

    @SerialName("framesToConfirm")
    val framesToConfirm: Int?,

    @SerialName("description")
    val description: String? = null,

    @SerialName("bendHint")
    val bendHint: String? = null,

    @SerialName("straightenHint")
    val straightenHint: String? = null
)
