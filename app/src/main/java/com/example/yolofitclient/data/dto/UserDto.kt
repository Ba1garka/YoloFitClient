package com.example.yolofitclient.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class UserDto(

    @SerialName("id")
    val id : Int?,

    @SerialName("name")
    val name: String?,

    @SerialName("email")
    val email: String?,

    @SerialName("birthDate")
    val birthDate: String?,

    @SerialName("gender")
    val gender: String?,

    @SerialName("height")
    val height: String?,

    @SerialName("weight")
    val weight: String?,

    @SerialName("fitnessLevel")
    val fitnessLevel: String?,

    @SerialName("photoUrl")
    val photoUrl: String? = null,

    @SerialName("goal")
    var goal: String?,

    @SerialName("dailyCalorieTarget")
    var dailyCalorieTarget: String?
)