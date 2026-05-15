package com.example.yolofitclient.ui.nav

import kotlinx.serialization.Serializable

@Serializable
data object CreateWorkoutRoute: AppRoute{
    override val route: String = "createworkout"
}