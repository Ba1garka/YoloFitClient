package com.example.yolofitclient.ui.nav

import kotlinx.serialization.Serializable

@Serializable
data object WorkoutDetailRoute : AppRoute{
    override val route: String = "workoutdetail"
}