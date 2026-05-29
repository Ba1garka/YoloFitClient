package com.example.yolofitclient.ui.nav

import kotlinx.serialization.Serializable

@Serializable
data object CalendarRoute : AppRoute {
    override val route: String = "calendar"
}