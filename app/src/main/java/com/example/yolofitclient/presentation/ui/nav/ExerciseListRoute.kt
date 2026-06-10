package com.example.yolofitclient.presentation.ui.nav


import kotlinx.serialization.Serializable

@Serializable
data object ExerciseListRoute: AppRoute {
    override val route: String = "exerciseList"
}