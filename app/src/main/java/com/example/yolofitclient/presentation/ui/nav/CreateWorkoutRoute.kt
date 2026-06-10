package com.example.yolofitclient.presentation.ui.nav

import kotlinx.serialization.Serializable

@Serializable
data object CreateWorkoutRoute: AppRoute{
    override val route: String = "createworkout"
}