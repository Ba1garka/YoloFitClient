package com.example.yolofitclient.ui.nav

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute: AppRoute {
    override val route: String = "home"
}