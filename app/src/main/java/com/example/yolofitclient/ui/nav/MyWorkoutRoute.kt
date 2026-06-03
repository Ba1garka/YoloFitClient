package com.example.yolofitclient.ui.nav

import kotlinx.serialization.Serializable

@Serializable
data object MyWorkoutRoute: AppRoute {
    override val route: String = "myworkout"
}