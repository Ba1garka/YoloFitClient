package com.example.yolofitclient.presentation.ui.nav

import kotlinx.serialization.Serializable

@Serializable
data object RegisterRoute: AppRoute{
    override val route: String = "register"
}