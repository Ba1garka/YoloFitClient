package com.example.yolofitclient.ui.screen.login

sealed interface LoginIntent {
    data class Send(val login: String, val password: String): LoginIntent
    data class TextInput(val login: String, val password: String): LoginIntent
}