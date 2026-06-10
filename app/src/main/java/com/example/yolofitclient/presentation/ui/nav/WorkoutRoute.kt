package com.example.yolofitclient.presentation.ui.nav

import kotlinx.serialization.Serializable


@Serializable
data object WorkoutRoute : AppRoute{
    override val route: String = "workout"
}