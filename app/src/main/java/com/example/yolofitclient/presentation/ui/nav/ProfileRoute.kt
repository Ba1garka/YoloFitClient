package com.example.yolofitclient.presentation.ui.nav

import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute: AppRoute {
    override val route: String = "profile"
}