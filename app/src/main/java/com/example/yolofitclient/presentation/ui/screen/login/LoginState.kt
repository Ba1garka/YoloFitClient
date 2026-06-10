package com.example.yolofitclient.presentation.ui.screen.login

sealed interface LoginState {
    object Loading: LoginState

    data class Data(
        val isEnabledSend: Boolean,
        val error: String?
    ): LoginState
}