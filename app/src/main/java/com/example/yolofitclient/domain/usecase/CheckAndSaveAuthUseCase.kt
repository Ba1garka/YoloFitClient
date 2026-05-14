package com.example.yolofitclient.domain.usecase

import com.example.yolofitclient.data.dto.UserDto
import com.example.yolofitclient.data.repository.AuthRepository

class CheckAndSaveAuthUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        login: String,
        password: String,
    ): Result<UserDto> {
        return authRepository.checkAndAuth(login, password).mapCatching { userDto ->
            println("CheckAndSaveAuthUseCase: Starting auth for $login")

//            if(!userEntity) error("Login or pass incorrect")
            userDto

        }
    }
}