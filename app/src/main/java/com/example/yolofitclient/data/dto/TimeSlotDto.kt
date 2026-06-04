package com.example.yolofitclient.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimeSlotDto(
    @SerialName("date")
    val date: String?,

    @SerialName("startTime")
    val startTime: String?,

    @SerialName("available")
    val available: Boolean?
)