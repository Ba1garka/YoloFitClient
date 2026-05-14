package com.example.yolofitclient.data.repository

import com.example.yolofitclient.data.dto.UserDto
import com.example.yolofitclient.data.source.AuthLocalDataSource
import com.example.yolofitclient.data.source.AuthNetworkDataSource

class AuthRepository(
    private val authNetworkDataSource: AuthNetworkDataSource,
    private  val authLocalDataSource: AuthLocalDataSource
) {
    suspend fun checkAndAuth(
        login: String,
        password: String,
    ): Result<UserDto> {
        authLocalDataSource.setToken(login,password)
        return authNetworkDataSource.checkAuth()
            .onSuccess { userDto ->
                userDto
            }
            .onFailure {
                authLocalDataSource.clearToken()
            }
    }
}