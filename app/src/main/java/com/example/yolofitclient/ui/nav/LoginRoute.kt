package com.example.yolofitclient.ui.nav

import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute: AppRoute {
    override val route: String = "login"
}