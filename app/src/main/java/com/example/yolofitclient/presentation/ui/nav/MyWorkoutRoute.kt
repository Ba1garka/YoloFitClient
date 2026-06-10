package com.example.yolofitclient.presentation.ui.nav

import kotlinx.serialization.Serializable

@Serializable
data object MyWorkoutRoute: AppRoute {
    override val route: String = "myworkout"
}