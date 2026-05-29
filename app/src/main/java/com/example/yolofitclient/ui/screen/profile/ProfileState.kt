package com.example.yolofitclient.ui.screen.profile

sealed interface ProfileState  {
    data class Error(val reason : String): ProfileState
    data object Loading : ProfileState
    data object Content : ProfileState
    data object Success : ProfileState
}