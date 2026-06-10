package com.example.yolofitclient.presentation.ui.nav

import kotlinx.serialization.Serializable

@Serializable
data object WorkoutDetailRoute : AppRoute{
    override val route: String = "workoutdetail"
}