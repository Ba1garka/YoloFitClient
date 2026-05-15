package com.example.yolofitclient.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseDto(
    @SerialName("id")
    val id : Int?,
    @SerialName("name")
    val name: String?,
    @SerialName("defaultSets")
    val defaultSets: Int?,
    @SerialName("defaultReps")
    val defaultReps: Int?,
    @SerialName("weightCoefficient")
    val weightCoefficient: String?,
    @SerialName("bodyZoneName")
    val bodyZoneName: String?,
)