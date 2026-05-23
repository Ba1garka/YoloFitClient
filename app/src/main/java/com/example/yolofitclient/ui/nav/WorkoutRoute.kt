package com.example.yolofitclient.ui.nav

import kotlinx.serialization.Serializable


@Serializable
data object WorkoutRoute : AppRoute{
    override val route: String = "workout"
}