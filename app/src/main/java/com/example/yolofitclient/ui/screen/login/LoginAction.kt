package com.example.yolofitclient.ui.screen.login

import com.example.yolofitclient.ui.nav.AppRoute

sealed interface LoginAction {
    data class OpenScreen(val route: AppRoute)
}