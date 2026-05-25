package com.example.yolofitclient.domain.entity


class UserEntity(

    val id : Int,

    val name: String,

    val email: String,

    val birthDate: String,

    val gender: String,

    val height: String,

    val weight: String,

    val fitnessLevel: String,

    val photoUrl: String? = null,

    var goal: String,

    var dailyCalorieTarget: String?
)