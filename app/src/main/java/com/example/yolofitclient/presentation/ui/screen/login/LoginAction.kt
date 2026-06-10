package com.example.yolofitclient.presentation.ui.screen.login

import com.example.yolofitclient.presentation.ui.nav.AppRoute


sealed interface LoginAction {
    data class OpenScreen(val route: AppRoute)
}