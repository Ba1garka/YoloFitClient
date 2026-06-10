package com.example.yolofitclient.presentation.ui.screen.register

import com.example.yolofitclient.domain.entity.UserEntity

sealed interface RegisterState {

    data object Initial : RegisterState
    data class Error( val reason: String ): RegisterState
    data object Loading: RegisterState
    data class Content(
        val user: UserEntity
    ): RegisterState
}